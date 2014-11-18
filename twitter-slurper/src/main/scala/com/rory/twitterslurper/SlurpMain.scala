package com.rory.twitterslurper

import java.net.InetSocketAddress

import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.rabbitmq.client.Connection
import com.rory.twitterslurper.rabbit.RabbitConnectionActor.Connect
import com.rory.twitterslurper.rabbit.{RabbitConnectionActor, RabbitPublisherActor}

import com.rory.twitterslurper.twitter.TwitterConnection
import scala.concurrent.duration._

/**
 * Created by rory on 11/17/14.
 */
object SlurpMain extends App {

  implicit val timeout = Timeout(2 seconds)
  val actorSystem = ActorSystem("twitter-slurper")
  implicit val executor = actorSystem.dispatcher

  val consumerKey =  args(0)
  val consumerSecret = args(1)
  val token = args(2)
  val secret = args(3)

  val twitterConnection = new TwitterConnection(consumerKey, consumerSecret, token, secret)

  val connectionActor = actorSystem.actorOf(
    RabbitConnectionActor.props(
      new InetSocketAddress("127.0.0.1", 5672)),
    "rmq-conn-provider-slurp")

  (connectionActor ? Connect).mapTo[Connection] map {
    implicit conn ⇒

      val rabbitPublisher = actorSystem.actorOf(
        Props(new RabbitPublisherActor(Queues.tweetInputBinding)),
        "rmq-tweet-publisher")

      val tweetParser = actorSystem.actorOf(
        Props(new TweetParser(rabbitPublisher)),
        "slurp-tweet-parser")

      val tweetFetcher = actorSystem.actorOf(
        Props(new TweetFetcher(
          twitterConnection.hosebirdClient,
          twitterConnection.msgQueue,
          tweetParser)),
        "slurp-tweet-farmer")
  }
}
