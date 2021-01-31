#!/usr/bin/env python
""" generated source for module TelegramKeyboard """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.telegram
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

import java.util.ArrayList

import java.util.List

import java.util.Arrays

import org.json.JSONObject

import org.json.JSONArray

# 
#  * @author Alex Leontiev
#  
class TelegramKeyboard(TelegramOutputMessage):
    """ generated source for class TelegramKeyboard """
    _msg = str()
    _categories = List()
    _columns = int()

    def __init__(self, msg, categories, columns):
        """ generated source for method __init__ """
        super(TelegramKeyboard, self).__init__()
        self._msg = msg
        self._columns = columns
        self._categories = Arrays.asList(categories)
        setText(msg)
        buttons = ArrayList()
        i = 0
        while len(categories):
            buttons.add(ArrayList())
            while j < columns and len(categories):
                buttons.get(len(buttons) - 1).add(InlineKeyboardButton().setText(categories[i]).setCallbackData(Integer.toString(i)))
                i += 1
                j += 1

        markupInline = InlineKeyboardMarkup()
        markupInline.setKeyboard(buttons)
        self.setReplyMarkup(markupInline)

    def __str__(self):
        """ generated source for method toString """
        res = super(TelegramKeyboard, self).__str__()
        return String.format("%s(%s,%s)", self.__class__.getSimpleName(), self._msg, self._categories)

    def toJsonString(self):
        """ generated source for method toJsonString """
        return JSONObject().put("tag", getClass().getSimpleName()).put("value", JSONObject().put("msg", self._msg).put("categories", JSONArray(Arrays.asList(self._categories)))).__str__()

