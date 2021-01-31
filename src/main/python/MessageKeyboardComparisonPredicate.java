#!/usr/bin/env python
""" generated source for module MessageKeyboardComparisonPredicate """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

import org.json.JSONObject

import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer

# 
#  * @author Alex Leontiev
#  
class MessageKeyboardComparisonPredicate(ElDrinkoCondition):
    """ generated source for class MessageKeyboardComparisonPredicate """
    _what = None

    def __init__(self, o):
        """ generated source for method __init__ """
        super(MessageKeyboardComparisonPredicate, self).__init__()
        if o != None:
            self._what = str(o)

    def test(self, im):
        """ generated source for method test """
        return (isinstance(, (KeyboardAnswer, ))) and (self._what == None or im.left.getMsg() == self._what)

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject(super(MessageKeyboardComparisonPredicate, self).toJsonString()).put("value", self._what).__str__()

