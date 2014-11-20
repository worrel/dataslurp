#!/bin/bash

cd twitter-slurper

sbt "run-main com.rory.twitterslurper.PersistMain" &&
sbt "run-main com.rory.twitterslurper.SlurpMain"