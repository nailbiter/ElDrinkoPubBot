#!/bin/sh

mvn exec:java -Dexec.mainClass="nl.insomnia247.nailbiter.eldrinkopubbot.App" -Dexec.args="ProtoElDrinkoPubBot" -DAPP_LOG_ROOT=.log/ProtoElDrinkoPubBot
