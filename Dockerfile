# VERSION		1.0
FROM java

# Install Scala 2.11.4
WORKDIR /tmp
ADD http://downloads.typesafe.com/scala/2.11.4/scala-2.11.4.tgz /tmp/
RUN tar -xzf scala-2.11.4.tgz -C /usr/share
RUN rm scala-2.11.4.tgz

# Link scala executables
WORKDIR /usr/bin
RUN ln -s /usr/share/scala-2.11.4/bin/scala
RUN ln -s /usr/share/scala-2.11.4/bin/scalac
RUN ln -s /usr/share/scala-2.11.4/bin/scalap
RUN ln -s /usr/share/scala-2.11.4/bin/scaladoc
RUN ln -s /usr/share/scala-2.11.4/bin/fsc

# Install SBT via .deb file
WORKDIR /tmp
ADD http://dl.bintray.com/sbt/debian/sbt-0.13.6.deb /tmp/
RUN dpkg -i sbt-0.13.6.deb
RUN rm sbt-0.13.6.deb

# Setup work directory
RUN mkdir /dataslurp-app
WORKDIR /dataslurp-app
ADD . /dataslurp-app
