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
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition import WidgetPredicate, MessageKeyboardComparisonPredicate
import functools
import jsonpath_ng
import logging
import json


class _SimpleCondition:
    def __init__(self, pred, obj):
        self._pred = pred
        self._obj = obj

    def __call__(self, *args, **kwargs):
        return self._pred(*args, **kwargs)

    def __str__(self):
        return json.dumps(self._obj)


class ElDrinkoConditionInflator:
    def __init__(self):
        self._logger = logging.getLogger(self.__class__.__name__)

    def __call__(self, o):
        return _SimpleCondition(self._call(o), o)

    def _call(self, o):
        if o["tag"] == "TrivialPredicate":
            return lambda x: True
        elif o["tag"] == "IsPhoneNumberPredicate":
            return lambda x: isinstance(x.input_message, TelegramTextInputMessage) and re.match(r"\d{10}", x.input_message.message) is not None
        elif o["tag"] == "NegationPredicate":
            return lambda x: not self(o["value"])(x)
        elif o["tag"] == "IsTextMessagePredicate":
            return lambda x: isinstance(x.input_message, TelegramTextInputMessage)
        elif o["tag"] == "MessageComparisonPredicate":
            return lambda x: x.input_message.message == o["value"]
        elif o["tag"] == "MessageKeyboardComparisonPredicate":
            return MessageKeyboardComparisonPredicate(**{k: v for k, v in o.items() if k != "tag"})
        elif o["tag"] == "ConjunctionPredicate":
            return lambda x: functools.reduce(lambda x_, y_: x_ and y_, [self(o_)(x) for o_ in o["value"]], True)
        elif o["tag"] == "JsonCheckFieldPredicate":
            return lambda x: len(jsonpath_ng.parse(o["value"]["path"]).find(x.input_message)) > 0
        elif o["tag"] == "IsHalfIntegerFloatPredicate":
            return lambda x: int(2*float(x.input_message.message)) == 2*float(x.input_message.message)
        elif o["tag"] == "WidgetPredicate":
            return WidgetPredicate(o.get("value", None))
        else:
            raise NotImplementedError(f"o: {o}")
