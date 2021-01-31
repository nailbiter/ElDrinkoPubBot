#!/usr/bin/env python
""" generated source for module StateMachineException """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.state_machine
# 
#  * @author Alex Leontiev
#  
class StateMachineException(Exception):
    """ generated source for class StateMachineException """
    @overloaded
    def __init__(self):
        """ generated source for method __init__ """
        super(StateMachineException, self).__init__()

    @__init__.register(object, str)
    def __init___0(self, msg):
        """ generated source for method __init___0 """
        super(StateMachineException, self).__init__(msg)

