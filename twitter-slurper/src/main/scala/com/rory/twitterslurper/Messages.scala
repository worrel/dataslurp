package com.rory.twitterslurper

import org.json4s.JsonAST.JValue

/**
 * Created by rory on 11/17/14.
 */
object Messages {
  case class Tweet(data: JValue, provenance: String)
  case class RawTweet(data: String)
  case class BadTweet(data: String, error: Throwable)
}
