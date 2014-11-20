#!/bin/bash

cd twitter-slurper

sbt "run-main com.rory.twitterslurper.PersistMain" & \
sleep 10s && \
sbt "run-main com.rory.twitterslurper.SlurpMain" 