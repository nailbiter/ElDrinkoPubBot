#!/usr/bin/env python
""" generated source for module UserData """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.telegram
import org.telegram.telegrambots.meta.api.objects.Update

import org.json.JSONObject

import org.telegram.telegrambots.meta.api.objects.Message

import org.apache.logging.log4j.Logger

import org.apache.logging.log4j.LogManager

# 
#  * @author Alex Leontiev
#  
class UserData(object):
    """ generated source for class UserData """
    _chatId = 0
    _username = None
    _firstName = None
    _lastName = None
    _Log = LogManager.getLogger()

    @overloaded
    def __init__(self):
        """ generated source for method __init__ """

    @__init__.register(object, Update)
    def __init___0(self, update):
        """ generated source for method __init___0 """
        m = None
        if update.hasMessage():
            m = update.getMessage()
        elif update.hasCallbackQuery():
            m = update.getCallbackQuery().getMessage()
        else:
            self._Log.error(update)
        self._Log.info(m)
        self._chatId = m.getChatId()
        self._username = m.getChat().getUserName()
        self._firstName = m.getChat().getFirstName()
        self._lastName = m.getChat().getLastName()

    def getChatId(self):
        """ generated source for method getChatId """
        return self._chatId

    def getUserName(self):
        """ generated source for method getUserName """
        return "@" + self._username

    def getLastName(self):
        """ generated source for method getLastName """
        return self._lastName

    def getFirstName(self):
        """ generated source for method getFirstName """
        return self._firstName

    def __str__(self):
        """ generated source for method toString """
        return Long.toString(self._chatId)

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject().put("chatId", self._chatId).put("username", self._username).__str__()

