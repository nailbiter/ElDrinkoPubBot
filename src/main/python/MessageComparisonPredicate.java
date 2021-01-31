#!/usr/bin/env python
""" generated source for module MessageComparisonPredicate """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

import org.json.JSONObject

# 
#  * @author Alex Leontiev
#  
class MessageComparisonPredicate(ElDrinkoCondition):
    """ generated source for class MessageComparisonPredicate """
    _what = str()

    def __init__(self, o):
        """ generated source for method __init__ """
        super(MessageComparisonPredicate, self).__init__()
        self._what = str(o)

    def test(self, im):
        """ generated source for method test """
        return im.left.getMsg() == self._what

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject(super(MessageComparisonPredicate, self).toJsonString()).put("value", self._what).__str__()

