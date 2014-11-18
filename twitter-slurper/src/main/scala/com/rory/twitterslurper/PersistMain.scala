package com.rory.datapersister

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.rabbitmq.client.Connection
import com.rory.twitterslurper.Queues
import com.rory.twitterslurper.rabbit.RabbitConnectionActor.Connect
import com.rory.twitterslurper.rabbit.{RabbitConsumerActor, RabbitConnectionActor}
import scala.concurrent.duration._

/**
 * Created by rory on 11/17/14.
 */
object PersistMain extends App {

  implicit val timeout = Timeout(2 seconds)

  val actorSystem = ActorSystem("data-persister")
  implicit val executor = actorSystem.dispatcher

  val connectionActor = actorSystem.actorOf(
    RabbitConnectionActor.props(new InetSocketAddress("127.0.0.1", 5672)),
    "rmq-conn-provider-persist")

  (connectionActor ? Connect).mapTo[Connection] map {
    implicit conn ⇒
      val rabbitConsumer = actorSystem.actorOf(
        RabbitConsumerActor.props(Queues.tweetInputBinding),
        "rmq-tweet-consumer")
  }
}
