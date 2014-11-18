package com.rory.twitterslurper.rabbit

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive
import com.rabbitmq.client.{AMQP, Connection}
import com.rory.twitterslurper.Messages.{BadTweet, Tweet}
import org.json4s.jackson.JsonMethods._

/**
 * Created by rory on 11/17/14.
 */

class RabbitPublisherActor(binding: RabbitBinding)(implicit connection: Connection)
    extends Actor
    with ActorLogging
    with ChannelInitializer {

  val channel = initChannel(binding)
  val propsBuilder = new AMQP.BasicProperties.Builder()
  val jsonProps = propsBuilder.contentType("application/json").build()

  override def receive = LoggingReceive {
    case Tweet(data,prov) ⇒ {
      channel.basicPublish(binding.exchange, "", jsonProps, compact(render(data)).getBytes)
    }

    case BadTweet(data,err) ⇒ log.warning(s"IGNORING bad tweet, REASON: ${err.getMessage}")
  }
}
