package com.rory.twitterslurper.rabbit

import com.rabbitmq.client.{Channel, Connection}

import scala.collection.JavaConversions._

/**
 * Utility trait exposing the logic to initiate new channel and bindings.
 */
trait ChannelInitializer {
  
  def initChannel(connection: Connection, binding: RabbitBinding): Channel = {
    val ch = connection.createChannel()
    ch.exchangeDeclare(binding.exchange, "direct", true)
    ch.queueDeclare(binding.queue, true, false, false, Map[String, java.lang.Object]())
    ch.queueBind(binding.queue, binding.exchange, binding.routingKey)
    ch
  }
}