package com.rory.twitterslurper.sql

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive
import com.rory.twitterslurper.Messages.Tweet
import com.rory.twitterslurper.sql.PersistWorker.SaveTweet

import scala.slick.driver.MySQLDriver
import slick.driver.MySQLDriver.simple._

/**
 * Created by rory on 11/19/14.
 */

object PersistWorker {
  case class SaveTweet(tweet: Tweet)
}

class PersistWorker(db: MySQLDriver.backend.DatabaseDef)
    extends Actor
    with ActorLogging
    with TweetDBPersistence {

  override def receive = LoggingReceive {
    case SaveTweet(tweet) ⇒ db.withSession {
      implicit session ⇒
        tweets.insert(tweet)
    }
  }
}
