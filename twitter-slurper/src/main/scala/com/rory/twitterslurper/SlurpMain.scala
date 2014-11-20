package com.rory.twitterslurper

import java.net.InetSocketAddress

import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.rabbitmq.client.Connection
import com.rory.twitterslurper.rabbit.RabbitConnectionActor.Connect
import com.rory.twitterslurper.rabbit.{RabbitConnectionActor, RabbitPublisherActor}

import com.rory.twitterslurper.twitter.{MessageClassifier, TwitterConnection}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

/**
 * Created by rory on 11/17/14.
 */
object SlurpMain extends App {
  val actorSystem = ActorSystem("twitter-slurper")

  val config = ConfigFactory.load()

  val consumerKey = config.getString("twitter.consumer.key")
  val consumerSecret = config.getString("twitter.consumer.secret")
  val token = config.getString("twitter.access.token")
  val secret = config.getString("twitter.access.secret")

  val mqHost = config.getString("rabbitmq.host")
  val mqPort = config.getString("rabbitmq.port")

  val twitterConnection = new TwitterConnection(consumerKey, consumerSecret, token, secret)

  val connectionActor = actorSystem.actorOf(
    RabbitConnectionActor.props(
      new InetSocketAddress(mqHost, mqPort.toInt)),
    "rmq-conn-provider-slurp")

  implicit val timeout = Timeout(2 seconds)
  implicit val executor = actorSystem.dispatcher

  (connectionActor ? Connect).mapTo[Connection] map {
    conn ⇒

      val rabbitPublisher = actorSystem.actorOf(
        Props(new RabbitPublisherActor(conn, Queues.tweetInputBinding, Queues.commandBinding)),
        "rmq-tweet-publisher")

      val messageClassifier = actorSystem.actorOf(
        Props(new MessageClassifier(rabbitPublisher)),
        "slurp-msg-classifier")

      val tweetParser = actorSystem.actorOf(
        Props(new TweetParser(messageClassifier)),
        "slurp-tweet-parser")

      val tweetFetcher = actorSystem.actorOf(
        Props(new TweetFetcher(
          twitterConnection.hosebirdClient,
          twitterConnection.msgQueue,
          tweetParser)),
        "slurp-tweet-farmer")
  }
}
