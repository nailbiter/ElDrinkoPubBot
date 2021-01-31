#!/usr/bin/env python
""" generated source for module ExposedStateMachine """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.state_machine
class ExposedStateMachine(StateMachine, InputMessage, OutputMessage):
    """ generated source for class ExposedStateMachine """
    def __init__(self, state):
        """ generated source for method __init__ """
        super(ExposedStateMachine, self).__init__(state)

    def setState(self, state):
        """ generated source for method setState """
        _setState(state)

    def getState(self):
        """ generated source for method getState """
        return _currentState

