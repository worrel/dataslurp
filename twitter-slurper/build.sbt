name := "twitter-slurper"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= {
  val akkaVersion = "2.3.7"
  val rabbitVersion = "3.4.1"
  val logbackVersion = "1.1.2"
  Seq(
    "com.rabbitmq"                 %    "amqp-client"             % rabbitVersion,
    "com.typesafe.scala-logging"  %%  "scala-logging-slf4j"     % "2.1.2",
    "ch.qos.logback"                %   "logback-core"             % logbackVersion,
    "ch.qos.logback"                %   "logback-classic"          % logbackVersion,
    "com.typesafe.akka"            %%  "akka-actor"              % akkaVersion,
    "com.typesafe.akka"            %%  "akka-slf4j"               % akkaVersion,
    "com.typesafe.slick"            %%  "slick"                     % "2.1.0",
    "mysql"                          %    "mysql-connector-java"   % "5.1.34",
    "com.mchange"                  %    "c3p0"                     % "0.9.5-pre10",
    "com.twitter"                   %    "hbc-core"                 % "2.2.0",
    "org.json4s"                    %%  "json4s-jackson"           % "3.2.11",
    "com.typesafe.akka"            %%  "akka-testkit"             % akkaVersion % "test",
    "org.scalatest"                 %%  "scalatest"                % "2.2.1" % "test"
  )
}

    