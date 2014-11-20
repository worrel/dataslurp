package com.rory.twitterslurper

import com.rory.twitterslurper.rabbit.RabbitBinding

/**
 * Created by rory on 11/17/14.
 */
object Queues {
  val tweetInputBinding = RabbitBinding("twitter", "inbound.tweets", "tweet")
  val commandBinding = RabbitBinding("twiter", "inbound.commands", "command")
}
