#!/usr/bin/env python
""" generated source for module JsonCheckFieldPredicate """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import org.apache.logging.log4j.Logger

import org.apache.logging.log4j.LogManager

import com.jayway.jsonpath.JsonPath

import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString

import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

import org.json.JSONObject

import com.jayway.jsonpath.Configuration

# 
#  * @author Alex Leontiev
#  
class JsonCheckFieldPredicate(ElDrinkoCondition):
    """ generated source for class JsonCheckFieldPredicate """
    _Log = LogManager.getLogger()
    _path = None

    def __init__(self, o):
        """ generated source for method __init__ """
        super(JsonCheckFieldPredicate, self).__init__()
        self._path = o

    def test(self, im):
        """ generated source for method test """
        document = Configuration.defaultConfiguration().jsonProvider().parse(im.right.__str__())
        self._Log.info(SecureString.format("im: %s", im.__str__()))
        self._Log.info(SecureString.format("_path: %s", self._path))
        self._Log.info(SecureString.format("document: %s", document))
        res = None
        try:
            res = JsonPath.read(document, self._path.getString("path"))
        except Exception as e:
            self._Log.error(e)
        self._Log.info(SecureString.format("res: %s", res))
        return res != None

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject(super(JsonCheckFieldPredicate, self).toJsonString()).put("value", self._path).__str__()

