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
from nl.insomnia247.nailbiter.eldrinkopubbot.util.persistent_storage import PersistentStorage
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko import ElDrinkoInputMessage
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.action.el_drinko_action_inflator import ElDrinkoActionInflator
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition.el_drinko_condition_inflator import ElDrinkoConditionInflator
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.el_drinko_state_machine import ElDrinkoStateMachine
from nl.insomnia247.nailbiter.eldrinkopubbot.telegram import TelegramTextInputMessage, TelegramKeyboardAnswer
from telegram import InlineKeyboardButton, InlineKeyboardMarkup, KeyboardButton, ReplyKeyboardMarkup
from pymongo import MongoClient
import os
import subprocess
import pandas as pd


class ElDrinkoPubBot:
    def __init__(self, settings, bot, mongo_url):
        self._logger = logging.getLogger(self.__class__.__name__)
        # print(settings)
        self._settings = settings
        self._bot = bot
        mongo_client = MongoClient(mongo_url)
        self._mongo_client = mongo_client
        self._lastSentKeyboardHash = {}

        ElDrinkoStateMachine.PreloadImages(self._get_collection("beerlist"))
        self._edsm = ElDrinkoStateMachine(self)
        _actionInflator = ElDrinkoActionInflator(
            self.send_message,
            PersistentStorage(mongo_client.beerbot.var,settings["id"]),
            self.insert_order
        )
        _conditionInflator = ElDrinkoConditionInflator()

        commit = subprocess.getoutput("git rev-parse HEAD")
        commit = commit[:7]
        self.send_message(
            f"updated! now at {commit}", "developerChatIds", is_markdown=True)

    def insert_order(self):
        pass

    def _get_collection(self, key):
        return self._mongo_client.beerbot[self._settings["mongodb"][key]]

    def _createInputMessage(self, update):
        if update.message is not None:
            return TelegramTextInputMessage(update.message.text)
        elif update.callback_query is not None:
            self._logger.info(f"callback_query: {update.callback_query}")
            self._logger.info(update.effective_message.chat_id)
            self._logger.info(self._lastSentKeyboardHash)

            if update.effective_message.chat_id in self._lastSentKeyboardHash:
                self._bot.delete_message(
                    update.effective_message.chat_id,
                    self._lastSentKeyboardHash[update.effective_message.chat_id]
                )
            #FIXME: send button name here, not `update.callback_query.data` (which will be a number)
            self._send_message(update.effective_message.chat_id, {
                               "text": update.callback_query.data})
            return TelegramKeyboardAnswer(update.callback_query.data)
        else:
            return None

    def __call__(self, update, context):
        self._logger.info(f"message> {update.message}")
        # update.message.reply_text(text="text")
        im = self._createInputMessage(update)
        chat_id = update.effective_message.chat_id
        if im is not None:
            doc = self._get_collection(
                "state_machine_states").find_one({"id": str(chat_id)})
            state = doc["state"] if doc is not None else "_"
            self._logger.info(f"state: {state}")
            self._edsm.setState(state)
            eim = ElDrinkoInputMessage(
                input_message=im, 
                data=self._get_collection("data").find_one({"id":str(chat_id)}),
                user_data=str(chat_id),
                beerlist=pd.DataFrame(self._get_collection("beerlist").find())
            )
            self._logger.info(f"eim: {eim}")
            om = self._edsm(eim)
            self._logger.info(f"om: {om}")

#        keyboard = [
#            [InlineKeyboardButton("3", callback_data=str("three")),
#             InlineKeyboardButton("4", callback_data=str("four"))]
#        ]
#        if update.message is not None:
#            self._send_message(update.effective_message.chat_id, {
#                               "text": "text", "reply_markup": InlineKeyboardMarkup(keyboard)})

    def _send_message(self, chat_id, msg):
        sent_msg = self._bot.sendMessage(chat_id=chat_id, **msg)
        if "reply_markup" in msg:
            k, v = chat_id, sent_msg.message_id
            self._logger.info(f"add {k,v} to _lastSentKeyboardHash")
            self._lastSentKeyboardHash[k] = v

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
            self._send_message(r, {"text": message, **send_message_kwargs})
