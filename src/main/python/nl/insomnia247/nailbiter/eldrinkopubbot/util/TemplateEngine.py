#!/usr/bin/env python
""" generated source for module TemplateEngine """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.util
import com.hubspot.jinjava.Jinjava

import java.util.Map

import com.hubspot.jinjava.lib.filter.Filter

import org.apache.logging.log4j.Logger

import org.apache.logging.log4j.LogManager

import com.hubspot.jinjava.interpret.JinjavaInterpreter

# 
#  * @author Alex Leontiev
#  
class TemplateEngine(object):
    """ generated source for class TemplateEngine """
    _jinjava = Jinjava()

    def __init__(self):
        """ generated source for method __init__ """
        self._jinjava.getGlobalContext().registerFilter(Filter())
        self._jinjava.getGlobalContext().registerFilter(Filter())

    def render(self, template, context):
        """ generated source for method render """
        self._Log.info(template)
        self._Log.info(context)
        res = ""
        try:
            res = self._jinjava.render(template, context)
        except Exception as e:
            self._Log.error(e)
            self._Log.error(e.getMessage())
        self._Log.info(res)
        return res

TemplateEngine._Log = LogManager.getLogger(TemplateEngine.__class__)

