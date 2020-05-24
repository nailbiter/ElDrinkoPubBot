#!/bin/sh

git checkout master
git pull
mvn clean
mvn install
mvn exec:java -Dexec.mainClass="nl.insomnia247.nailbiter.eldrinkopubbot.App" -Dfilename=eldrinkopubbot
