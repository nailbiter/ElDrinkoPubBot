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

class ElDrinkoPubBot:
    def __init__(self, settings):
        self._logger = logging.getLogger(self.__class__.__name__)

    def __call__(self, update, context):
        self._logger.info(f"message> {update.message}")
        update.message.reply_text(text="text")
