"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/eldrinko/condition/el_drinko_condition_inflator.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-07T15:27:55.088859
    REVISION: ---

==============================================================================="""
import re
from nl.insomnia247.nailbiter.eldrinkopubbot.telegram import TelegramTextInputMessage, TelegramKeyboardAnswer
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition import WidgetPredicate
import functools
import jsonpath_ng
import logging


class ElDrinkoConditionInflator:
    def __init__(self):
        self._logger = logging.getLogger(self.__class__.__name__)

    def __call__(self, o):
        if o["tag"] == "TrivialPredicate":
            return lambda x: True
        elif o["tag"] == "IsPhoneNumberPredicate":
            return lambda x: instanceof(x, TelegramTextInputMessage) and re.match(r"\d{10}", x.message) is not None
        elif o["tag"] == "NegationPredicate":
            return lambda x: not self(o["value"])(x)
        elif o["tag"] == "IsTextMessagePredicate":
            return lambda x: instanceof(x, TelegramTextInputMessage)
        elif o["tag"] == "MessageComparisonPredicate":
            return lambda x: x.message == o["value"]
        elif o["tag"] == "MessageKeyboardComparisonPredicate":
            return lambda x: instanceof(x, TelegramKeyboardAnswer) and (o.get("value", None) is None or x.message == o.get("value", None))
        elif o["tag"] == "ConjunctionPredicate":
            return lambda x: functools.reduce(lambda x_, y_: x_ and y_, [self(o_)(x) for o_ in o["value"]], True)
        elif o["tag"] == "JsonCheckFieldPredicate":
            return lambda x:len(jsonpath_ng.parse(o["value"]["path"]).find(x))>0
        elif o["tag"] == "IsHalfIntegerFloatPredicate":
            return lambda x:int(2*float(x.message))==2*float(x.message)
        elif o["tag"] == "WidgetPredicate":
            return WidgetPredicate(o.get("value",None))
        else:
            raise NotImplementedError(f"o: {o}")
