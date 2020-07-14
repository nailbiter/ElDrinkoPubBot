#!/bin/sh


BOT_NAME=ProtoElDrinkoPubBot
BRANCH=staging


git checkout $BRANCH
git pull
mvn clean
mvn compile

COMMIT_MSG=`git log -1 --pretty=%B|head -n1`
COMMIT_HASH=`git rev-parse --short HEAD`
COMMIT_DATA="\`$COMMIT_MSG\` (*$COMMIT_HASH*)"

env COMMIT_DATA="$COMMIT_DATA" BOT_NAME="$BOT_NAME" mvn exec:java -Dexec.mainClass="nl.insomnia247.nailbiter.eldrinkopubbot.App" -Dfilename=$BOT_NAME
