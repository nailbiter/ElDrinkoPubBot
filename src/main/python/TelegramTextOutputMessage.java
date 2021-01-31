#!/usr/bin/env python
""" generated source for module TelegramTextOutputMessage """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.telegram
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage

import org.telegram.telegrambots.meta.api.methods.send.SendMessage

import org.json.JSONObject

import org.json.JSONArray

# 
#  * @author Alex Leontiev
#  
class TelegramTextOutputMessage(TelegramOutputMessage):
    """ generated source for class TelegramTextOutputMessage """
    _msg = None

    def __init__(self, msg):
        """ generated source for method __init__ """
        super(TelegramTextOutputMessage, self).__init__()
        setText(msg)
        self._msg = msg

    def __str__(self):
        """ generated source for method toString """
        return String.format("TelegramOutputMessage(%s)", self._msg)

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject().put("tag", getClass().getSimpleName()).put("value", JSONObject().put("msg", self._msg)).__str__()

