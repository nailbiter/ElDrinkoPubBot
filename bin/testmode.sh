#!/bin/sh

git checkout staging
git pull
mvn clean
mvn install
mvn exec:java -Dexec.mainClass="nl.insomnia247.nailbiter.eldrinkopubbot.App" -Dexec.args="ProtoElDrinkoPubBot" -Dfilename=ProtoElDrinkoPubBot
