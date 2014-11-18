package com.rory.twitterslurper.rabbit

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import akka.util.ByteString
import com.rabbitmq.client._

object RabbitConsumerActor {
  
  def props(binding: RabbitBinding)(implicit connection: Connection): Props =
    Props(new RabbitConsumerActor(binding))
}

class RabbitConsumerActor(binding: RabbitBinding)(implicit connection: Connection)
    extends Actor
    with ActorLogging
    with ChannelInitializer {
  
  val channel = initChannel(binding)

  val consumer = new DefaultConsumer(channel) {
    override def handleDelivery(
        consumerTag: String, 
        envelope: Envelope, 
        properties: AMQP.BasicProperties, 
        body: Array[Byte]) = {
      self ! new RabbitMessage(envelope.getDeliveryTag(), ByteString(body), properties, channel)
    }
  }

  channel.basicConsume(binding.queue, false, consumer)

  override def receive = LoggingReceive {
    case msg: RabbitMessage ⇒ {
      log.debug(s"received ${msg.deliveryTag}")
      channel.basicAck(msg.deliveryTag, false);
    }
  }

  override def postStop() = channel.close()
}