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


class ElDrinkoPubBot:
    def __init__(self, settings):
        self._logger = logging.getLogger(self.__class__.__name__)
        print(settings)
        mongo_client = MongoClient(os.environ["MONGO_URL"])
        self._mongo_client = mongo_client
        ElDrinkoStateMachine.PreloadImages(
            mongo_client.beerbot[settings["mongodb"]["beerlist"]])
        exit(0)

    def __call__(self, update, context):
        self._logger.info(f"message> {update.message}")
        update.message.reply_text(text="text")

    def send_message(self):
        pass
