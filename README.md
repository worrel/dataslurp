# dataslurp

A sandbox project to explore social feed data acquisition.  Right now it only works with Twitter.

Written in Scala, utilizing [Twitter Hosebird][1], [RabbitMQ][2] and [Akka][3]

[1]: https://github.com/twitter/hbc
[2]: http://rabbitmq.com
[3]: http://akka.io

## Install

Development environment is based on [Fig][1] and [Docker][2].  

1. Install both according to the instructions on their respective websites
2. Modify twitter-slurper/env.sh to provide your own Twitter API keys
3. Source env.sh

	 . twitter-slurper/env.sh

4. Run

    fig up
