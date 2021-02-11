"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/telegram/__init__.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-07T13:09:19.020902
    REVISION: ---

==============================================================================="""

from collections import namedtuple

TelegramTextInputMessage = namedtuple("TelegramTextInputMessage","message")
TelegramKeyboardAnswer = namedtuple("TelegramKeyboardAnswer", "message")
TelegramKeyboard = namedtuple("TelegramKeyboard","message keyboard columns")
TelegramTextOutputMessage = namedtuple("TelegramTextOutputMessage","message")
TelegramImageOutputMessage = namedtuple("TelegramImageOutputMessage","message url")
