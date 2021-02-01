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
from os import environ
import click
import json

@click.command()
@click.option("--mongo-url",envvar="MONGO_URL")
@click.option("--mongo-url",envvar="MONGO_URL")
@click.option("--environment",type=click.Choice(["ElDrinkoPubBot","ProtoElDrinkoPubBot","DevElDrinkoPubBot"]),default="DevElDrinkoPubBot")
def App(mongo_url,environment):
    mongo_client = MongoClient(mongo_url)
    settings,keyring = [
            {k:v for k,v in mongo_client.beerbot[kk].find_one({"id":environment}).items() if k not in ["_id","id"]} 
            for kk
            in ["_settings","_keyring"]
    ]
#    print(json.dumps(settings,indent=2))
#    print(json.dumps(keyring,indent=2))
    telegram_token = keyring["telegram"]["token"]
    updater = Updater(token, use_context=True)
    bot = updater.bot
    updater.dispatcher.add_handler(MessageHandler(
        filters=Filters.all, callback=_message_handler))

if __name__=="__main__":
    App()
