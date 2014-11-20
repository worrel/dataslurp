package com.rory.twitterslurper.sql

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akka.routing.RoundRobinPool
import com.rory.twitterslurper.Messages.Tweet
import com.rory.twitterslurper.sql.PersistWorker.SaveTweet

import scala.slick.driver.MySQLDriver

/**
 * Created by rory on 11/19/14.
 */

class DatabaseActor(db: MySQLDriver.backend.DatabaseDef)
    extends Actor
    with ActorLogging
    with TweetDBPersistence {

  db.withSession { implicit session ⇒ ensureSchema }

  val workerPool = context.actorOf(
    RoundRobinPool(5).props(Props(new PersistWorker(db))),
    "db-worker-pool")

  override def receive = LoggingReceive {
    case tweet: Tweet ⇒ {
      workerPool ! SaveTweet(tweet)
    }
  }
}
