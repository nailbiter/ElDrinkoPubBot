#!/usr/bin/env python
""" generated source for module TelegramKeyboardAnswer """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.telegram
import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer

import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage

# 
#  * @author Alex Leontiev
#  
class TelegramKeyboardAnswer(TelegramInputMessage, KeyboardAnswer):
    """ generated source for class TelegramKeyboardAnswer """
    def __init__(self, ans):
        """ generated source for method __init__ """
        super(TelegramKeyboardAnswer, self).__init__(ans)

    def __str__(self):
        """ generated source for method toString """
        return String.format("TelegramKeyboardAnswer(%s)", _msg)

