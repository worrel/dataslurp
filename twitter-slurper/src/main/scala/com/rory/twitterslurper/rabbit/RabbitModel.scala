package com.rory.twitterslurper.rabbit

import akka.util.ByteString
import com.rabbitmq.client.{AMQP, Channel}
import com.typesafe.scalalogging.slf4j.LazyLogging

class RabbitMessage(val deliveryTag: Long, val body: ByteString,
                        val properties: AMQP.BasicProperties, channel: Channel) extends LazyLogging {

  /**
   * Ackowledge the message.
   */
  def ack(): Unit = {
    logger.debug(s"ack $deliveryTag")
    channel.basicAck(deliveryTag, false)
  }
  
  /**
   * Reject and requeue the message.
   */
  def nack(): Unit = {
    logger.debug(s"nack $deliveryTag")
    channel.basicNack(deliveryTag, false, true)
  }
}

/**
 * Exchange and queue names.
 */
case class RabbitBinding(exchange: String, queue: String, routingKey: String)

