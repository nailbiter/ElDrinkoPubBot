#!/usr/bin/env python
""" generated source for module OutputArrayMessage """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.model
import org.json.JSONObject

import org.json.JSONArray

# 
#  * @author Alex Leontiev
#  
class OutputArrayMessage(OutputMessage):
    """ generated source for class OutputArrayMessage """
    _msgs = []

    def __init__(self, msgs):
        """ generated source for method __init__ """
        super(OutputArrayMessage, self).__init__()
        self._msgs = msgs

    def getMessages(self):
        """ generated source for method getMessages """
        return self._msgs

    def __str__(self):
        """ generated source for method toString """
        res = ""
        for msg in _msgs:
            res += msg.__str__() + ","
        return String.format("[%s]", res)

    def toJsonString(self):
        """ generated source for method toJsonString """
        arr = JSONArray()
        for om in _msgs:
            arr.put(JSONObject(om.toJsonString()))
        return JSONObject().put("tag", self.__class__.getSimpleName()).put("value", arr).__str__()

