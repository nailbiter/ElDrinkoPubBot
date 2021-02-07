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
from telegram import InlineKeyboardButton, InlineKeyboardMarkup, KeyboardButton, ReplyKeyboardMarkup
from pymongo import MongoClient
import os
import subprocess


class ElDrinkoPubBot:
    def __init__(self, settings, bot, mongo_url):
        self._logger = logging.getLogger(self.__class__.__name__)
        # print(settings)
        self._settings = settings
        self._bot = bot
        mongo_client = MongoClient(mongo_url)
        self._mongo_client = mongo_client

        ElDrinkoStateMachine.PreloadImages(
            mongo_client.beerbot[settings["mongodb"]["beerlist"]])
        self._edsm = ElDrinkoStateMachine(self)

        commit = subprocess.getoutput("git rev-parse HEAD")
        commit = commit[:7]
        self.send_message(
            f"updated! now at {commit}", "developerChatIds", is_markdown=True)

    def _createInputMessage(self, update):
        self._logger.info(f"msg: {update.message}")
        self._logger.info(f"cc: {update.callback_query}")

    def __call__(self, update, context):
        self._logger.info(f"message> {update.message}")
        # update.message.reply_text(text="text")
        im = self._createInputMessage(update)
        keyboard = [
            [InlineKeyboardButton("3", callback_data=str("three")),
             InlineKeyboardButton("4", callback_data=str("four"))]
        ]
        if update.message is not None:
            update.message.reply_text(text="text", reply_markup=InlineKeyboardMarkup(keyboard))

    def send_message(self, message, recipient, is_markdown=False):
        if recipient == "developerChatIds":
            is_markdown = True
            message = f"`(> {message} <)`"
        if recipient in self._settings["telegram"]:
            recipient_list = self._settings["telegram"][recipient]
        else:
            recipient_list = [int(recipient)]
        send_message_kwargs = {}
        if is_markdown:
            send_message_kwargs["parse_mode"] = "Markdown"
        for r in recipient_list:
            self._logger.info(f"chat_id={r}")
            self._bot.sendMessage(chat_id=r, text=message,
                                  **send_message_kwargs)
        # HERE:    https://python-telegram-bot.readthedocs.io/en/latest/telegram.bot.html#telegram.Bot.sendMessage
