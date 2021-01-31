#!/usr/bin/env python
""" generated source for module ElDrinkoCondition """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import java.util.function_.Predicate

import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

import org.json.JSONObject

# 
#  * @author Alex Leontiev
#  
class ElDrinkoCondition(Predicate, ElDrinkoInputMessage):
    """ generated source for class ElDrinkoCondition """
    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject().put("tag", getClass().getSimpleName()).put("value", JSONObject.NULL).__str__()

