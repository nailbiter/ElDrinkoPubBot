#!/bin/sh


BOT_NAME=ProtoElDrinkoPubBot
BRANCH=staging


git checkout $BRANCH
git pull
mvn clean
mvn install

COMMIT_MSG=`git log -1 --pretty=%B|head -n1`
COMMIT_HASH=`git rev-parse --short HEAD`
COMMIT_DATA="\`$COMMIT_MSG\` (*$COMMIT_HASH*)"

mvn exec:java -Dexec.mainClass="nl.insomnia247.nailbiter.eldrinkopubbot.App" -Dexec.args="\"$COMMIT_DATA\" $BOT_NAME" -Dfilename=$BOT_NAME
