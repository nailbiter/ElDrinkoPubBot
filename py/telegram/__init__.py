"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/py/telegram/__init__.py

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

# input message types
TelegramTextInputMessage = namedtuple("TelegramTextInputMessage", "message")
TelegramKeyboardAnswer = namedtuple(
    "TelegramKeyboardAnswer", "message button_title keyboard")
# output message types
TelegramArrayOutputMessage = namedtuple(
    "TelegramArrayOutputMessage", "messages")
TelegramKeyboard = namedtuple("TelegramKeyboard", "message keyboard columns")
TelegramTextOutputMessage = namedtuple("TelegramTextOutputMessage", "message")
TelegramImageOutputMessage = namedtuple(
    "TelegramImageOutputMessage", "message url")
