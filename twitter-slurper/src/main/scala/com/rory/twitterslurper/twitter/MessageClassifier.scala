package com.rory.twitterslurper.twitter

import akka.actor.{ActorRef, Actor, ActorLogging}
import akka.event.LoggingReceive
import com.rory.twitterslurper.Messages._
import org.json4s._
import org.json4s.JsonAST.JValue

/**
 * Created by rory on 11/19/14.
 */
class MessageClassifier(next: ActorRef) extends Actor with ActorLogging {

  implicit val formats = DefaultFormats

  val commands = Set("delete", "scrub_geo","status_withheld","user_withheld")

  override def receive = LoggingReceive {
    case JsonAPIMessage(json,prov) ⇒ {
      val commandMsg = json.filterField { case (f,jv) ⇒ commands.contains(f) }
      val result = commandMsg match {
        case Nil ⇒ JsonTweetData(minimal(json))
        case (field, j) :: rest ⇒ classifyCommand(field, j)
      }
      next ! result
    }
  }


  private def identifier(name: String, json: JValue) = (json \ name).extract[BigInt]
  private def tweetId(json: JValue) = identifier("id", json)
  private def userId(json: JValue) = identifier("user_id", json)
  private def countryList(json: JValue) = (json \ "withheld_in_countries").extract[List[String]]

  def classifyCommand(field: String, json: JValue) = field match {
    case "delete" ⇒ DeleteTweetCommand(tweetId(json \ "status"), userId(json \ "status"))
    case "scrub_geo" ⇒ ScrubGeoCommand(identifier("up_to_status_id",json), userId(json))
    case "status_withheld" ⇒ ContentWithheldCommand(Some(tweetId(json)),userId(json),countryList(json))
    case "user_withheld" ⇒ ContentWithheldCommand(None,userId(json),countryList(json))
    case _ ⇒ log.warning("Unknown command: {}, contents: {}", field, json)
  }

  def minimal(json: JValue) = {
    import org.json4s.JsonDSL._

    ("id" → (json \ "id")) ~
        ("tweet" → (json \ "text")) ~
        ("userId" → (json \ "user" \ "id")) ~
        ("userScreenName" → (json \ "user" \ "screen_name")) ~
        ("userFullName" → (json \ "user" \ "name")) ~
        ("followerCount" → (json \ "user" \ "followers_count")) ~
        ("origId" → (json \ "retweeted_status" \ "id")) ~
        ("origUserId" → (json \ "retweeted_status" \ "user" \ "id")) ~
        ("origFollowerCount" → (json \ "retweeted_status" \ "user" \ "followers_count")) ~
        ("isRetweet" → {
          (json \ "retweeted_status") match {
            case JNothing ⇒ false
            case _ ⇒ true
          }})

  }
}
