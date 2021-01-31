#!/usr/bin/env python
""" generated source for module IsPhoneNumberPredicate """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

import java.util.regex.Pattern

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage

import org.apache.logging.log4j.LogManager

import org.apache.logging.log4j.Logger

import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString

# 
#  * @author Alex Leontiev
#  
class IsPhoneNumberPredicate(ElDrinkoCondition):
    """ generated source for class IsPhoneNumberPredicate """
    _Log = LogManager.getLogger()

    def __init__(self, o):
        """ generated source for method __init__ """
        super(IsPhoneNumberPredicate, self).__init__()

    def test(self, tim):
        """ generated source for method test """
        self._Log.info(tim)
        if not (isinstance(, (TelegramTextInputMessage, ))):
            self._Log.info("in if")
            return False
        ttim = tim.left
        res = Pattern.matches("\\d{10}", ttim.getMsg())
        self._Log.info(SecureString.format("res: %s", res))
        return res

