package com.rory.twitterslurper.rabbit

import java.nio.charset.StandardCharsets

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive
import com.rabbitmq.client.{AMQP, Connection}
import com.rory.twitterslurper.Messages._
import org.json4s.jackson.JsonMethods._

/**
 * Created by rory on 11/17/14.
 */

class RabbitPublisherActor(connection: Connection, tweetBinding: RabbitBinding, commandBinding: RabbitBinding)
    extends Actor
    with ActorLogging
    with ChannelInitializer {

  val tweetChannel = initChannel(connection, tweetBinding)
  val commandChannel = initChannel(connection, commandBinding)
  val propsBuilder = new AMQP.BasicProperties.Builder()
  val jsonProps = propsBuilder.contentType("application/json").build()
  val textProps = propsBuilder.contentType("text/plain").build()

  override def receive = LoggingReceive {
    case JsonTweetData(data) ⇒ {
      tweetChannel.basicPublish(
        tweetBinding.exchange, tweetBinding.routingKey, jsonProps,
        compact(render(data)).getBytes(StandardCharsets.UTF_8))
    }

    case cmd: APICommand ⇒ processCommand(cmd)

    case MalformedAPIMessage(data) ⇒ log.warning(s"IGNORING bad tweet")
  }

  // TODO: not handling commands right now, for shame!
  def processCommand(command: APICommand) = {
    log.info("Processing command {}", command)
    val order = command match {
      case DeleteTweetCommand(id,userId) ⇒ s"D|$id|$userId"
      case ScrubGeoCommand(upTo,userId) ⇒ s"S|$upTo|$userId"
      case ContentWithheldCommand(optId, userId, countries) ⇒ s"W|$countries|$userId|$optId"
    }

    commandChannel.basicPublish(
      commandBinding.exchange, commandBinding.routingKey, textProps,
      order.getBytes(StandardCharsets.UTF_8))
  }
}
