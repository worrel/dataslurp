package com.rory.twitterslurper

import akka.actor.{ActorRef, ActorLogging, Actor}
import akka.event.LoggingReceive
import com.rory.twitterslurper.Messages.{MalformedAPIMessage, JsonAPIMessage, RawAPIMessage}
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created by rory on 11/17/14.
 */
class TweetParser(next: ActorRef)
    extends Actor
    with ActorLogging {

  override def receive = LoggingReceive {
    case RawAPIMessage(data) ⇒ {
      val tweetJson = parseOpt(data)

      tweetJson match {
        case Some(jv) ⇒ next ! JsonAPIMessage(jv,"twitter-slurper")
        case None ⇒ next ! MalformedAPIMessage(data)
      }
    }
  }


}
