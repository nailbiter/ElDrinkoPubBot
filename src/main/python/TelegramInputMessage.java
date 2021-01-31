#!/usr/bin/env python
""" generated source for module TelegramInputMessage """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.telegram
import nl.insomnia247.nailbiter.eldrinkopubbot.model.InputMessage

import org.telegram.telegrambots.meta.api.objects.Message

import org.json.JSONObject

import org.telegram.telegrambots.meta.api.objects.Update

# 
#  * @author Alex Leontiev
#  
class TelegramInputMessage(InputMessage):
    """ generated source for class TelegramInputMessage """
    _msg = None

    def __init__(self, msg):
        """ generated source for method __init__ """
        super(TelegramInputMessage, self).__init__()
        self._msg = msg

    def getMsg(self):
        """ generated source for method getMsg """
        return self._msg

    def __str__(self):
        """ generated source for method toString """
        return String.format("TelegramInputMessage(%s)", self._msg)

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject().put("tag", getClass().getSimpleName()).put("msg", self._msg).__str__()

