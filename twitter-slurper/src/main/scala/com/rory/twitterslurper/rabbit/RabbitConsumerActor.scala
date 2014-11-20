package com.rory.twitterslurper.rabbit

import java.nio.charset.StandardCharsets

import akka.actor.{ActorRef, Actor, ActorLogging}
import akka.event.LoggingReceive
import akka.util.ByteString
import com.rabbitmq.client._
import com.rory.twitterslurper.Messages.Tweet
import org.json4s._
import org.json4s.jackson.JsonMethods._

class RabbitConsumerActor(connection: Connection, binding: RabbitBinding, next: ActorRef)
    extends Actor
    with ActorLogging
    with ChannelInitializer {

  implicit val formats = DefaultFormats

  val channel = initChannel(connection, binding)

  val consumer = new DefaultConsumer(channel) {
    override def handleDelivery(
        consumerTag: String, 
        envelope: Envelope, 
        properties: AMQP.BasicProperties, 
        body: Array[Byte]) = {
      self ! new RabbitMessage(envelope.getDeliveryTag(),
        ByteString(body), properties, channel)
    }
  }

  channel.basicConsume(binding.queue, false, consumer)

  override def receive = LoggingReceive {
    case msg: RabbitMessage ⇒ {
      log.debug("received {}",msg.deliveryTag)

      val json = parse(msg.body.decodeString(StandardCharsets.UTF_8.name))

      log.debug("json {}",json)

      val tweet = json.extract[Tweet]

      next ! tweet

      channel.basicAck(msg.deliveryTag, false);
    }
  }

  override def postStop() = channel.close()
}