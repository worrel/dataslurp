package com.rory.twitterslurper

import org.json4s.JsonAST.JValue

/**
 * Created by rory on 11/17/14.
 */
object Messages {
  case class JsonAPIMessage(data: JValue, provenance: String)
  case class RawAPIMessage(data: String)
  case class MalformedAPIMessage(data: String)

  case class JsonTweetData(data: JValue)

  sealed abstract trait APICommand
  case class DeleteTweetCommand(id: BigInt, userId: BigInt) extends APICommand
  case class ScrubGeoCommand(upToId: BigInt, userId: BigInt) extends APICommand
  case class ContentWithheldCommand(id: Option[BigInt], userId: BigInt, countries: List[String]) extends APICommand

  case class Tweet(id: Long, tweet: String, userId: Long, userScreenName: String,
                            userFullName: String, followerCount: Long, isRetweet: Boolean,
                            origId: Option[Long], origUserId: Option[Long], origFollowerCount: Option[Long])
}
