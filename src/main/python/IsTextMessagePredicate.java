#!/usr/bin/env python
""" generated source for module IsTextMessagePredicate """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage

# 
#  * @author Alex Leontiev
#  
class IsTextMessagePredicate(ElDrinkoCondition):
    """ generated source for class IsTextMessagePredicate """
    def __init__(self, o):
        """ generated source for method __init__ """
        super(IsTextMessagePredicate, self).__init__()

    def test(self, im):
        """ generated source for method test """
        return isinstance(, (TelegramTextInputMessage, ))

