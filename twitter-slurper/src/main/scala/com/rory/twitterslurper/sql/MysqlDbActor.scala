package com.rory.twitterslurper.sql

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.rory.twitterslurper.sql.MysqlDbActor.{Connection, Connect}

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by rory on 11/19/14.
 */
object MysqlDbActor {
  case object Connect
  case class Connection(db: MySQLDriver.backend.DatabaseDef)
}

class MysqlDbActor
    extends Actor
    with ActorLogging {

  val cpds = new ComboPooledDataSource

  override def receive = LoggingReceive {
    case Connect ⇒ sender ! Connection(Database.forDataSource(cpds))
  }

  override def postStop() = {
    log.info("Closing connection")
    cpds.close()
  }
}
