#!/usr/bin/env python3
"""===============================================================================

        FILE: App.py
       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: --
     CREATED: 2021-01-31T17:12:33.975745
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
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.el_drinko_pub_bot import ElDrinkoPubBot
import logging
from datetime import datetime


class _AtExitHook:
    def __init__(self, pidfile):
        self._pidfile = pidfile

    def __call__(self):
        click.echo("_atexit_hook")
        os.system(f"rm -rf {self._pidfile}")


def _echo(update, context):
    update.message.reply_text(text="""
На жаль, ми пішли з Кварталу і більше не обробляємо замовлення, але далі буде.
Шукайте наші напої у мережi Craft Beer Shop та слідкуйте за новинами на: 

https://t.me/ElDrinko_Channel
https://www.instagram.com/El_Drinko_Beer
http://ElDrinko.Beer

Не втрачайте нас з поля зору! :)
    """)


@click.command()
@click.option("--mongo-url", envvar="MONGO_URL")
@click.option("--template-folder", type=click.Path(), envvar="TEMPLATE_FOLDER")
@click.option("--environment", type=click.Choice(["ElDrinkoPubBot", "ProtoElDrinkoPubBot", "DevElDrinkoPubBot"]), default="DevElDrinkoPubBot")
@click.option("--debug/--no-debug", default=True)
def App(mongo_url, environment, debug, template_folder):
    pidfile = f".tmp/{environment}.txt"
    if debug:
        logging.basicConfig(level=logging.INFO,
                            filename=f".log/{environment}_{datetime.now().strftime('%Y%m%d%H%M%S')}.log.txt",
                            format='%(asctime)s,%(msecs)d %(levelname)-8s %(name)s [%(filename)s:%(lineno)d] %(message)s',
                            datefmt='%Y-%m-%d:%H:%M:%S',
                            )
    assert not path.isfile(
        pidfile), "only one instance of ElDrinkoPubBot allowed to run"
    with open(pidfile, "w") as f:
        f.write(str(os.getpid()))
    mongo_client = MongoClient(mongo_url)
    settings, keyring = [
        {k: v for k, v in mongo_client.beerbot[kk].find_one(
            {"id": environment}).items() if k not in ["_id", "id"]}
        for kk
        in ["_settings", "_keyring"]
    ]
    atexit.register(_AtExitHook(pidfile))
    click.echo("here")

    updater = Updater(keyring["telegram"]["token"], use_context=True)
    bot = updater.bot
#    edbp = ElDrinkoPubBot(
#        settings={**settings, "id": environment},
#        bot=bot,
#        mongo_url=mongo_url,
#        template_folder=template_folder
#    )
    updater.dispatcher.add_handler(
        MessageHandler(filters=Filters.all, callback=_echo))
#    updater.dispatcher.add_handler(
#        CallbackQueryHandler(callback=edbp))
    updater.start_polling()
    updater.idle()


if __name__ == "__main__":
    App()
