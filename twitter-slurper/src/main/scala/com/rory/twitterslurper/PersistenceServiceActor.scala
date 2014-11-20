package com.rory.twitterslurper

import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import com.rabbitmq.client.Connection
import com.rory.twitterslurper.rabbit.{RabbitConsumerActor, RabbitConnectionActor}
import com.rory.twitterslurper.sql.{DatabaseActor, MysqlDbActor}

import scala.slick.driver.MySQLDriver

/**
 * Created by rory on 11/19/14.
 */
class PersistenceServiceActor(mqHost: String, mqPort: Int)
    extends Actor
    with ActorLogging {

  val rabbitConnProvider = context.actorOf(
    RabbitConnectionActor.props(new InetSocketAddress(mqHost, mqPort)),
    "rmq-conn-provider-persist")

  val dbConnProvider = context.actorOf(Props[MysqlDbActor])

  var rabbitConn: Option[Connection] = None
  var db: Option[MySQLDriver.backend.DatabaseDef] = None

  rabbitConnProvider ! RabbitConnectionActor.Connect
  dbConnProvider ! MysqlDbActor.Connect

  override def receive = idle

  def idle = LoggingReceive {
    case conn: Connection ⇒ {
      rabbitConn = Some(conn)
      log.info("Connected to RabbitMQ")
      db match {
        case Some(dbDef) ⇒ activate(conn,dbDef)
        case None ⇒ log.info("Waiting for DB connection")
      }
    }

    case MysqlDbActor.Connection(dbDef) ⇒ {
      db = Some(dbDef)
      log.info("Connected to DB")
      rabbitConn match {
        case Some(conn) ⇒ activate(conn,dbDef)
        case None ⇒ log.info("Waiting for Rabbit connection")
      }
    }
  }

  private def activate(rabbitConn: Connection, dbDef: MySQLDriver.backend.DatabaseDef) = {
    val dbActor = context.actorOf(Props(new DatabaseActor(dbDef)),"mysql-db-supervisor")

    val queueActor = context.actorOf(
      Props(new RabbitConsumerActor(rabbitConn, Queues.tweetInputBinding, dbActor)),
      "rmq-tweet-consumer")

    context.become(active(dbActor, queueActor))
  }

  def active(dbActor: ActorRef, queueActor: ActorRef) = LoggingReceive {
    case _ ⇒ log.info("Purely supervisory at present")
  }
}
