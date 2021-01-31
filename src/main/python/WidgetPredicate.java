#!/usr/bin/env python
""" generated source for module WidgetPredicate """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.action.ElDrinkoActionInflator

import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage

import org.apache.logging.log4j.LogManager

import org.apache.logging.log4j.Logger

import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString

import org.json.JSONObject

import org.json.JSONArray

# 
#  * @author Alex Leontiev
#  * FIXME: eliminate
#  
class WidgetPredicate(MessageKeyboardComparisonPredicate):
    """ generated source for class WidgetPredicate """
    _Log = LogManager.getLogger()
    _type = str()

    def __init__(self, o):
        """ generated source for method __init__ """
        super(WidgetPredicate, self).__init__(None)
        self._type = str(o)

    def test(self, tim):
        """ generated source for method test """
        if not super(WidgetPredicate, self).test(tim):
            return False
        i = Integer.parseInt(tim.left.getMsg())
        self._Log.info(SecureString.format("i: %d", i))
        numProducts = int()
        self._Log.info(SecureString.format("numProducts: %d", numProducts))
        self._Log.info(SecureString.format("type: %s", self._type))
        if self._type == "finishButton":
            return i == 4 * numProducts
        elif self._type == "validButton":
            return i < 4 * numProducts and ((i % 4 == 1) or (i % 4 == 2))
        elif self._type == "invalidButton":
            return (i == 4 * numProducts) or ((i % 4 != 1) and (i % 4 != 2))
        self._Log.error(SecureString.format("_type: %s", self._type))
        return False

