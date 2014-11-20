package com.rory.twitterslurper

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
 * Created by rory on 11/17/14.
 */
object PersistMain extends App {

  val config = ConfigFactory.load()
  val mqHost = config.getString("rabbitmq.host")
  val mqPort = config.getInt("rabbitmq.port")

  val actorSystem = ActorSystem("data-persister")

  implicit val timeout = Timeout(2 seconds)
  implicit val executor = actorSystem.dispatcher

  actorSystem.actorOf(
    Props(new PersistenceServiceActor(mqHost,mqPort)),
    "persistence-service")
}


