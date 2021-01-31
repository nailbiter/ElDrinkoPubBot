#!/usr/bin/env python
""" generated source for module TelegramImageOutputMessage """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.telegram
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage

import java.net.URL

import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage

import nl.insomnia247.nailbiter.eldrinkopubbot.util.DownloadCache

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto

import org.json.JSONObject

import java.io.File

# 
#  * @author Alex Leontiev
#  
class TelegramImageOutputMessage(SendPhoto, OutputMessage):
    """ generated source for class TelegramImageOutputMessage """
    _msg = None
    _image = None

    def __init__(self, msg, image):
        """ generated source for method __init__ """
        super(TelegramImageOutputMessage, self).__init__()
        filePath = DownloadCache(".png").get(image)
        self.setPhoto(File(filePath))
        self.setCaption(msg)
        self._msg = msg
        self._image = image

    def __str__(self):
        """ generated source for method toString """
        return String.format("TelegramImageOutputMessage(%s,%s)", self._msg, self._image.__str__())

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject().put("tag", getClass().getSimpleName()).put("value", JSONObject().put("msg", self._msg).put("image", self._image.__str__())).toString()

