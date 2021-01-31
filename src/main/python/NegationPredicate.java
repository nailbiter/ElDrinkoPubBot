#!/usr/bin/env python
""" generated source for module NegationPredicate """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import org.json.JSONObject

import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

# 
#  * @author Alex Leontiev
#  
class NegationPredicate(ElDrinkoCondition):
    """ generated source for class NegationPredicate """
    _pred = ElDrinkoCondition()

    def __init__(self, o):
        """ generated source for method __init__ """
        super(NegationPredicate, self).__init__()
        self._pred = ElDrinkoConditionInflator.ParseElDrinkoCondition(o)

    def test(self, im):
        """ generated source for method test """
        return not self._pred.test(im)

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject(super(NegationPredicate, self).toJsonString()).put("value", JSONObject(self._pred.toJsonString())).__str__()

