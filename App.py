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
@click.option("--template-folder", type=click.Path(), envvar="TEMPLATE_FOLDER")
@click.option("--environment", type=click.Choice(["ElDrinkoPubBot", "ProtoElDrinkoPubBot", "DevElDrinkoPubBot"]), default="DevElDrinkoPubBot")
@click.option("--debug/--no-debug", default=True)
@click.option("--telegram-token", required=True, envvar="TELEGRAM_TOKEN")
def App(environment, debug, template_folder, telegram_token):
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
    keyring = {
        "telegram": {
            "token": telegram_token,
        },
    }
    atexit.register(_AtExitHook(pidfile))
    click.echo("here")

    updater = Updater(keyring["telegram"]["token"], use_context=True)
    bot = updater.bot
    updater.dispatcher.add_handler(
        MessageHandler(filters=Filters.all, callback=_echo))
#    updater.dispatcher.add_handler(
#        CallbackQueryHandler(callback=edbp))
    updater.start_polling()
    updater.idle()


if __name__ == "__main__":
    App()
