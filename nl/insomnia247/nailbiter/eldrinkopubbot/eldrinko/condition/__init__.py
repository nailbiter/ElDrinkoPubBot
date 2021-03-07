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
from nl.insomnia247.nailbiter.eldrinkopubbot.util.el_drinko_jinja_environment import BOTTLE_TYPES
import re
import logging


class MessageKeyboardComparisonPredicate:
    def __init__(self, value=None):
        self._value = value if value is None else int(value)
        self._logger = logging.getLogger(self.__class__.__name__)

    def __call__(self, eim):
        im = eim.input_message
        if not isinstance(im, TelegramKeyboardAnswer):
            return False
        if self._value is not None and re.match(r"\d+", im.message) is not None:
            self._logger.info(f"message: {im.message}")
            self._logger.info(f"len: {len(im.keyboard)}")
            self._logger.info(self._value)
            return (int(im.message)-self._value) % len(im.keyboard) == 0
        return True


class WidgetPredicate(MessageKeyboardComparisonPredicate):
    def __init__(self, type_):
        super().__init__()
        self._type = type_

    def __call__(self, eim):
        if not super().__call__(eim):
            return False
        im = eim.input_message
        i = int(im.message)
#        _Log.info(SecureString.format("i: %d",i));
#        int numProducts = ElDrinkoActionInflator.BOTTLE_TYPES.length;
        if "name" in eim.data["order"]["cart"][-1]:
            numProducts = len(BOTTLE_TYPES)
        elif "category" in eim.data["order"]["cart"][-1]:
            category = eim.data["order"]["cart"][-1]["category"]
            numProducts = len(eim.beerlist.query(f"category==\"{category}\""))
        else:
            raise NotImplementedError
#        _Log.info(SecureString.format("numProducts: %d",numProducts));
#        _Log.info(SecureString.format("type: %s",_type));
#
#        if (_type.equals("finishButton")) {
#            return i==4*numProducts;
#        } else if (_type.equals("validButton")) {
#            return i<4*numProducts && ((i%4==1) || (i%4==2));
#        } else if (_type.equals("invalidButton")) {
#            return (i==4*numProducts) || ((i%4!=1) && (i%4!=2));
#        }
        if self._type == "finishButton":
            return i == 4*numProducts
        elif self._type == "validButton":
            return i < 4*numProducts and ((i % 4 == 1) or (i % 4 == 2))
        elif self._type == "invalidButton":
            return i != 4*numProducts and i % 4 != 1 and i % 4 != 2
#
#        _Log.error(SecureString.format("_type: %s",_type));
#        return false;
        return False
