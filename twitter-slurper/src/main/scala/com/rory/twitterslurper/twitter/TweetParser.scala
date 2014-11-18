package com.rory.twitterslurper

import akka.actor.{ActorRef, ActorLogging, Actor}
import akka.event.LoggingReceive
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.rory.twitterslurper.Messages.{BadTweet, Tweet, RawTweet}
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created by rory on 11/17/14.
 */
class TweetParser(next: ActorRef)
    extends Actor
    with ActorLogging {
  override def receive = LoggingReceive {
    case RawTweet(data) ⇒ {
      try {
        val tweetJson = parse(data)
        next ! Tweet(tweetJson,"twitter-slurper")
      } catch {
        case jpe: JsonParseException ⇒ next ! BadTweet(data,jpe)
        case jme: JsonMappingException ⇒ next ! BadTweet(data,jme)
      }
    }
  }
}
