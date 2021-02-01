#!/usr/bin/env python3
"""===============================================================================

        FILE: app.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-01T22:21:46.342576
    REVISION: ---

==============================================================================="""

from telegram import InlineKeyboardButton, InlineKeyboardMarkup, KeyboardButton, ReplyKeyboardMarkup
from telegram.ext import Updater, CommandHandler, CallbackQueryHandler, MessageHandler, Filters
from pymongo import MongoClient
import click
import json
import os
from os import path
import atexit
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoPubBot import ElDrinkoPubBot

class _AtExitHook:
    def __init__(self,pidfile):
        self._pidfile = pidfile
    def __call__(self):
        click.echo("_atexit_hook")
        os.system(f"rm -rf {self._pidfile}")

@click.command()
@click.option("--mongo-url",envvar="MONGO_URL")
@click.option("--mongo-url",envvar="MONGO_URL")
@click.option("--environment",type=click.Choice(["ElDrinkoPubBot","ProtoElDrinkoPubBot","DevElDrinkoPubBot"]),default="DevElDrinkoPubBot")
def App(mongo_url,environment):
    pidfile = f".tmp/{environment}.txt"
    assert not path.isfile(pidfile), "only one instance of ElDrinkoPubBot allowed to run"
    with open(pidfile,"w") as f:
        f.write(str(os.getpid()))
    mongo_client = MongoClient(mongo_url)
    settings,keyring = [
            {k:v for k,v in mongo_client.beerbot[kk].find_one({"id":environment}).items() if k not in ["_id","id"]} 
            for kk
            in ["_settings","_keyring"]
    ]
    click.echo(json.dumps(settings,indent=2))
    click.echo(json.dumps(keyring,indent=2))
    atexit.register(_AtExitHook(pidfile))
    click.echo("here")

#    telegram_token = keyring["telegram"]["token"]
#    updater = Updater(token, use_context=True)
#    bot = updater.bot
#    updater.dispatcher.add_handler(MessageHandler(
#        filters=Filters.all, callback=ElDrinkoPubBot()))

if __name__=="__main__":
    App()
