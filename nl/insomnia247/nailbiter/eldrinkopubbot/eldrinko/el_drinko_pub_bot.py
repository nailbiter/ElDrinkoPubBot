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
from nl.insomnia247.nailbiter.eldrinkopubbot.telegram import TelegramTextInputMessage, TelegramKeyboardAnswer, TelegramArrayOutputMessage, TelegramKeyboard, TelegramTextOutputMessage, TelegramImageOutputMessage
from nl.insomnia247.nailbiter.eldrinkopubbot.telegram.user_data import UserData
from telegram import InlineKeyboardButton, InlineKeyboardMarkup, KeyboardButton, ReplyKeyboardMarkup
from pymongo import MongoClient
import os
import subprocess
import pandas as pd
import json
import itertools


class ElDrinkoPubBot:
    def __init__(self, settings, bot, mongo_url, template_folder):
        self._logger = logging.getLogger(self.__class__.__name__)
        # print(settings)
        self._settings = settings
        self._bot = bot
        mongo_client = MongoClient(mongo_url)
        self._mongo_client = mongo_client

        # FIXME: actually, preloading is not necessary => remove `PreloadImages` method and `DownloadCache` class
        # ElDrinkoStateMachine.PreloadImages(self._get_collection("beerlist"))
        self._edsm = ElDrinkoStateMachine(self)
        _actionInflator = ElDrinkoActionInflator(
            self.send_message,
            PersistentStorage(mongo_client.beerbot.var, settings["id"]),
            self.insert_order,
            template_folder=template_folder
        )
        _conditionInflator = ElDrinkoConditionInflator()
        transitions = _actionInflator.transitions
        self._edsm.inflateTransitionsFromJSON(
            _conditionInflator, _actionInflator, json.dumps(transitions["correspondence"]))

        commit = subprocess.getoutput("git rev-parse HEAD")
        commit = commit[:7]
        self.send_message(
            f"updated! now at {commit}", "developerChatIds", is_markdown=True)

#        public void accept(Document doc) {
#            MongoCollection<Document> statesColl = _mongoClient
#                .getDatabase("beerbot")
#                .getCollection(_config.getJSONObject("mongodb").getString("order_history"));
#            statesColl.insertOne(doc);
#        }
    def insert_order(self,order):
        self._logger.info(f"order: {order}")
        self._get_collection("order_history").insert_one(order)

    def _get_collection(self, key):
        return self._mongo_client.beerbot[self._settings["mongodb"][key]]

    def _createInputMessage(self, update):
        if update.message is not None:
            return TelegramTextInputMessage(update.message.text)
        elif update.callback_query is not None:
            self._logger.info(f"callback_query: {update.callback_query}")
            self._logger.info(update.effective_message.chat_id)

            message_id = update.effective_message.message_id
            self._logger.info(f"going to delete {message_id}")
            self._bot.delete_message(
                update.effective_message.chat_id,
                message_id
            )
            em = update.effective_message
            self._logger.info(f"em: {em}")
            keyboard = em.reply_markup.inline_keyboard
            self._logger.info(f"keyboard: {keyboard}")
            keyboard = list(itertools.chain(*keyboard))
            self._logger.info(f"callback_query: {update.callback_query}")
            button_title = keyboard[int(update.callback_query.data)].text
            self._send_message(update.effective_message.chat_id, {
                               "text": button_title,
                               })
            return TelegramKeyboardAnswer(message=update.callback_query.data, button_title=button_title,
                                          keyboard=keyboard)
        else:
            return None

    def __call__(self, update, context):
        self._logger.info(f"message> {update.message}")
        # update.message.reply_text(text="text")
        im = self._createInputMessage(update)
        chat_id = UserData(update)
        if im is not None:
            states_coll = self._get_collection("state_machine_states")
            doc = states_coll.find_one({"id": str(chat_id)})
            state = doc["state"] if doc is not None else "_"
            self._logger.info(f"state: {state}")
            self._edsm.setState(state)
            obj = self._get_collection("data").find_one({"id": str(chat_id)})
            if obj is None:
                data = {}
            else:
                data = obj["data"]
            eim = ElDrinkoInputMessage(
                input_message=im,
                data=data,
                user_data=chat_id,
                beerlist=pd.DataFrame(self._get_collection("beerlist").find())
            )
            self._logger.info(f"eim: {eim}")
            om, data = self._edsm(eim)
            self._logger.info(f"om: {om}")
#            _updateUserData(om.right,ud.toString());
            self._updateUserData(data, str(chat_id))
#            statesColl.updateOne(Filters.eq("id",ud.toString()),Updates.set("state",_edsm.getState()),new UpdateOptions().upsert(true));
            states_coll.update_one({"id": str(chat_id)}, {
                                   "$set": {"state": self._edsm.getState()}}, upsert=True)
#            _execute(om.left,ud);
            self._execute(om, chat_id)

    def _execute(self, om, user_data):
        self._logger.info(om)
        if isinstance(om, TelegramArrayOutputMessage):
            for om_ in om.messages:
                self._execute(om_, user_data)
        elif isinstance(om, TelegramTextOutputMessage):
            self._send_message(user_data.chat_id, {
                               "text": om.message, "parse_mode": "Markdown"})
        elif isinstance(om, TelegramKeyboard):
            self._send_message(
                user_data.chat_id,
                {
                    "text": om.message,
                    "parse_mode": "Markdown",
                    "reply_markup": InlineKeyboardMarkup([
                        [
                            InlineKeyboardButton(
                                om.keyboard[i+j], callback_data=str(i+j))
                            for j in
                            range(om.columns)
                            if i+j < len(om.keyboard)
                        ]
                        for i
                        in range(0, len(om.keyboard), om.columns)
                    ])
                }
            )
        elif isinstance(om, TelegramImageOutputMessage):
            self._bot.send_photo(chat_id=str(user_data),
                                 photo=om.url, caption=om.message)
        else:
            raise NotImplementedError(str((om, user_data)))

#    void _updateUserData(JSONObject userData, String id) {
#        String data_db_name = _config.getJSONObject("mongodb").getString("data");
#        _Log.info(SecureString.format("userData: %s",userData));
#        _Log.info(SecureString.format("id: %s",id));
#        _Log.info(SecureString.format("data_db_name: %s",data_db_name));
#        _mongoClient
#            .getDatabase("beerbot")
#            .getCollection(data_db_name)
#            .updateOne(Filters.eq("id",id),Updates.set("data",Document.parse(userData.toString())),new UpdateOptions().upsert(true));
#    }
    def _updateUserData(self, data, id_):
        self._get_collection("data").update_one(
            {"id": id_}, {"$set": {"data": data}}, upsert=True)

    def _send_message(self, chat_id, msg):
        sent_msg = self._bot.sendMessage(chat_id=chat_id, **msg)

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
