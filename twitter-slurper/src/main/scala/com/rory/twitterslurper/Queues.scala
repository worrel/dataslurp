package com.rory.twitterslurper

import com.rory.twitterslurper.rabbit.RabbitBinding

/**
 * Created by rory on 11/17/14.
 */
object Queues {
  val FROM_TWITTER_EX = "twitter"
  val INCOMING_TWEETS_Q = "inbound.tweets"
  val TO_CASS_EX = "cass"

  val tweetInputBinding = RabbitBinding(FROM_TWITTER_EX, INCOMING_TWEETS_Q)
}
