#!/bin/sh

BOT_NAME=DevElDrinkoPubBot

mvn clean
mvn install
mvn exec:java -Dexec.mainClass="nl.insomnia247.nailbiter.eldrinkopubbot.App" -Dexec.args="$BOT_NAME" -Dfilename=$BOT_NAME
