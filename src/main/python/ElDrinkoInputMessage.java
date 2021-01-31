#!/usr/bin/env python
""" generated source for module ElDrinkoInputMessage """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko
import org.apache.commons.lang3.tuple_.ImmutablePair

import org.json.JSONObject

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage

import org.json.JSONObject

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData

import nl.insomnia247.nailbiter.eldrinkopubbot.util.Tsv

import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils

import com.mongodb.client.MongoCollection

import org.bson.Document

# 
#  * @author Alex Leontiev
#  
class ElDrinkoInputMessage(object):
    """ generated source for class ElDrinkoInputMessage """
    left = None
    BEERLIST = "https://docs.google.com/spreadsheets/d/e/2PACX-1vRGSUiAeapo7eHNfA1v9ov_Cc2oCjWNsmcpadN6crtxJ236uDOKt_C_cR1hsXCyqZucp_lQoeRHlu0k/pub?gid=0&single=true&output=tsv"
    right = None
    userData = UserData()
    beerlist = None

    @overloaded
    def __init__(self, i, o, u, b):
        """ generated source for method __init__ """
        self.left = i
        self.right = o
        self.userData = u
        self.beerlist = b

    @__init__.register(object, TelegramInputMessage, JSONObject, UserData, MongoCollection)
    def __init___0(self, i, o, u, coll):
        """ generated source for method __init___0 """
        self.left = i
        self.right = o
        self.userData = u
        self.beerlist = Tsv(coll)

    def __str__(self):
        """ generated source for method toString """
        return ImmutablePair(self.left, self.right).__str__()

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject().put("left", JSONObject(self.left.toJsonString())).put("right", self.right).put("userData", JSONObject(self.userData.toJsonString())).put("beerlist", JSONObject(self.beerlist.toJsonString())).__str__()

