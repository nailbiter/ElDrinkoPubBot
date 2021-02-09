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
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.action.el_drinko_action_inflator import ElDrinkoActionInflator

class WidgetPredicate:
    def __init__(self,type_):
        self._type = type_
    def __call__(self,im):
        if not instanceof(im,TelegramKeyboardAnswer):
            return False
        i = int(im.message);
#        _Log.info(SecureString.format("i: %d",i));
#        int numProducts = ElDrinkoActionInflator.BOTTLE_TYPES.length;
        numProducts = len(ElDrinkoPubBot.BOTTLE_TYPES)
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
            return i==4*numProducts
        elif self._type=="validButton":
            return i<4*numProducts and ((i%4==1) or (i%4==2))
        elif self._type=="invalidButton":
            return i==4*numProducts or (i%4!=1 and i%4!=2)
#
#        _Log.error(SecureString.format("_type: %s",_type));
#        return false;
        return False
    }
