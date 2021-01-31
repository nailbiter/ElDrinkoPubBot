#!/usr/bin/env python
""" generated source for module ElDrinkoConditionInflator """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage

import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils

import java.util.regex.Pattern

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage

import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer

import java.util.function_.Function

import org.json.JSONObject

import org.apache.commons.lang3.tuple_.ImmutablePair

import java.util.function_.Predicate

import org.apache.logging.log4j.LogManager

import org.apache.logging.log4j.Logger

import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

# 
#  * @author Alex Leontiev
#  
class ElDrinkoConditionInflator(Function, object, Predicate, ElDrinkoInputMessage):
    """ generated source for class ElDrinkoConditionInflator """
    _Log = LogManager.getLogger()

    @classmethod
    def ParseElDrinkoCondition(cls, oo):
        """ generated source for method ParseElDrinkoCondition """
        o = oo
        # FIXME: automatize (change to loop)
        if o.getString("tag") == "TrivialPredicate":
            return TrivialPredicate(o.opt("value"))
        elif o.getString("tag") == "IsPhoneNumberPredicate":
            return IsPhoneNumberPredicate(o.opt("value"))
        elif o.optString("tag") == "NegationPredicate":
            return NegationPredicate(o.opt("value"))
        elif o.optString("tag") == "IsTextMessagePredicate":
            return IsTextMessagePredicate(o.opt("value"))
        elif o.optString("tag") == "MessageComparisonPredicate":
            return MessageComparisonPredicate(o.opt("value"))
        elif o.optString("tag") == "MessageKeyboardComparisonPredicate":
            return MessageKeyboardComparisonPredicate(o.opt("value"))
        elif o.optString("tag") == "ConjunctionPredicate":
            return ConjunctionPredicate(o.opt("value"))
        elif o.optString("tag") == "JsonCheckFieldPredicate":
            return JsonCheckFieldPredicate(o.opt("value"))
        elif o.optString("tag") == "IsHalfIntegerFloatPredicate":
            return IsHalfIntegerFloatPredicate(o.opt("value"))
        elif o.optString("tag") == "WidgetPredicate":
            return WidgetPredicate(o.opt("value"))
        else:
            cls._Log.error("8c817a6ca72e77d2c42e58aa")
            return None

    def apply(self, o):
        """ generated source for method apply """
        self._Log.info(SecureString.format("o: %s", o))
        return self.ParseElDrinkoCondition(o)

