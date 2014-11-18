package com.rory.twitterslurper

import java.util.concurrent.BlockingQueue

import akka.actor.{ActorRef, PoisonPill, Actor, ActorLogging}
import akka.event.LoggingReceive
import com.rory.twitterslurper.Messages.RawTweet
import com.rory.twitterslurper.TweetFetcher.{ReportStats, Acquire}
import com.twitter.hbc.httpclient.BasicClient

/**
 * Created by rory on 11/17/14.
 */

object TweetFetcher {
  case class Acquire()
  case class ReportStats()
}

class TweetFetcher(hosebirdClient: BasicClient, tweetQueue: BlockingQueue[String], next: ActorRef)
    extends Actor
    with ActorLogging {

  // start things off
  self ! Acquire()

  override def receive = LoggingReceive {
    case Acquire() ⇒ acquireTweet(next)
    case ReportStats() ⇒
      log.info("Message count: {}",hosebirdClient.getStatsTracker.getNumMessages)
  }

  private def acquireTweet(to: ActorRef) = {
    if(!hosebirdClient.isDone) {
      val tweet = tweetQueue.take()
      to ! RawTweet(tweet)
      self ! Acquire()
    } else {
      self ! PoisonPill
    }
  }

}
