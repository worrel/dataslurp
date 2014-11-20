package com.rory.twitterslurper.sql

import com.rory.twitterslurper.Messages.Tweet

import slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.meta.MTable

/**
 * Created by rory on 11/19/14.
 */
trait TweetDBPersistence {

  class Tweets(tag: Tag) extends Table[Tweet](tag, "TWEETS") {

    def id = column[Long]("ID", O.PrimaryKey) // This is the primary key column
    def text = column[String]("TEXT")
    def userId = column[Long]("USER_ID")
    def screenName = column[String]("SCREEN_NAME")
    def fullName = column[String]("FULL_NAME")
    def followerCount = column[Long]("FOLLOWER_COUNT")
    def retweet = column[Boolean]("RETWEETED")
    def retweetId = column[Option[Long]]("RETWEET_ID")
    def retweetUserId = column[Option[Long]]("RETWEET_USER_ID")
    def retweetFollowerCount = column[Option[Long]]("RETWEET_FOLLOWER_COUNT")

    def * = (id, text, userId, screenName, fullName, followerCount,
        retweet, retweetId, retweetUserId, retweetFollowerCount) <>( Tweet.tupled, Tweet.unapply)
  }
  val tweets = TableQuery[Tweets]

  def ensureSchema(implicit session: Session) = {
    if (MTable.getTables("TWEETS").list.isEmpty) {
      tweets.ddl.create
    }
  }
}
