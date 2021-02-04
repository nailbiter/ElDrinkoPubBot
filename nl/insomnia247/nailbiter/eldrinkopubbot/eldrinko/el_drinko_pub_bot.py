"""===============================================================================

        FILE: nl/insomnia247/nailbiter/eldrinkopubbot/ElDrinkoPubBot.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-01T23:19:03.703452
    REVISION: ---

==============================================================================="""

import logging
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.el_drinko_state_machine import ElDrinkoStateMachine
from pymongo import MongoClient
import os
import subprocess


class ElDrinkoPubBot:
    def __init__(self, settings, bot, mongo_url):
        self._logger = logging.getLogger(self.__class__.__name__)
        print(settings)
        mongo_client = MongoClient(mongo_url)
        self._mongo_client = mongo_client

        ElDrinkoStateMachine.PreloadImages(
            mongo_client.beerbot[settings["mongodb"]["beerlist"]])

        commit = subprocess.getoutput("git rev-parse HEAD")
        commit = commit[:7]
        self.send_message(f"updated! now at {commit}","developerChatIds",is_markdown=True)

    def __call__(self, update, context):
        self._logger.info(f"message> {update.message}")
        update.message.reply_text(text="text")

    def send_message(self,message,recipient,is_markdown=False):
        if recipient=="developerChatIds":
            is_markdown = True
            message = f"`(> {message} <)`"
        #HERE:    https://python-telegram-bot.readthedocs.io/en/latest/telegram.bot.html#telegram.Bot.sendMessage
