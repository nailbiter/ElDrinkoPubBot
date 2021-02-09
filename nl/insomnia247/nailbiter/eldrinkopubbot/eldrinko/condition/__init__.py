"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/eldrinko/condition/__init__.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-07T15:26:34.573066
    REVISION: ---

==============================================================================="""

from nl.insomnia247.nailbiter.eldrinkopubbot.telegram import TelegramTextInputMessage, TelegramKeyboardAnswer

class WidgetPredicate:
    def __init__(self,o):
        pass
    def __call__(self,im):
        if not instanceof(im,TelegramKeyboardAnswer):
            return False
