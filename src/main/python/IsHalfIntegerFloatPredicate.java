#!/usr/bin/env python
""" generated source for module IsHalfIntegerFloatPredicate """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage

# 
#  * @author Alex Leontiev
#  
class IsHalfIntegerFloatPredicate(ElDrinkoCondition):
    """ generated source for class IsHalfIntegerFloatPredicate """
    def __init__(self, o):
        """ generated source for method __init__ """
        super(IsHalfIntegerFloatPredicate, self).__init__()

    def test(self, tim):
        """ generated source for method test """
        if not (isinstance(, (TelegramTextInputMessage, ))):
            return False
        ttim = tim.left
        amount = 0
        try:
            amount = MiscUtils.ParseFloat(ttim.getMsg())
        except Exception as e:
            return False
        if not MiscUtils.IsFloatInteger(2 * amount):
            return False
        return True

