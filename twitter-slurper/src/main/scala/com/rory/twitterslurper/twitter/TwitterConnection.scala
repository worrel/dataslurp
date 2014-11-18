package com.rory.twitterslurper.twitter

import java.util.concurrent.{LinkedBlockingQueue, BlockingQueue}
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.endpoint.{StatusesSampleEndpoint, StatusesFilterEndpoint}
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.core.{Constants, Hosts, HttpHosts}
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.httpclient.auth.{OAuth1, Authentication}
import collection.JavaConversions._

/**
 * Created by rory on 11/17/14.
 */
class TwitterConnection(val consumerKey: String, val consumerSecret: String, val token: String, val secret: String) {
  val msgQueue = new LinkedBlockingQueue[String](100000)
  val eventQueue = new LinkedBlockingQueue[Event](1000)

  private val hosebirdHosts = new HttpHosts(Constants.STREAM_HOST)

  //private val hosebirdEndpoint = new StatusesFilterEndpoint
  private val hosebirdEndpoint = new StatusesSampleEndpoint

  // Optional: set up some followings and track terms
  // only for status filter
  //private val followings : List[java.lang.Long] = List(1234L, 566788L)
  //private val terms : List[String] = List("twitter", "api")
  //hosebirdEndpoint.followings(followings)
  //hosebirdEndpoint.trackTerms(terms)

  private val hosebirdAuth : Authentication =
    new OAuth1(consumerKey, consumerSecret, token, secret)

  private val builder = new ClientBuilder()
      .name("Hosebird-Client-01")                              // optional: mainly for the logs
      .hosts(hosebirdHosts)
      .authentication(hosebirdAuth)
      .endpoint(hosebirdEndpoint)
      .processor(new StringDelimitedProcessor(msgQueue))
      .eventMessageQueue(eventQueue)                          // optional: use this if you want to process client events

  val hosebirdClient = builder.build()
  hosebirdClient.connect()
}
