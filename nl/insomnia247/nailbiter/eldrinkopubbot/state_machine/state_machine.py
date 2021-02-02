"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/state_machine/StateMachine.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-02T18:45:16.490735
    REVISION: ---

==============================================================================="""

import logging
from collections import namedtuple


class StateMachine:
    def __init__(self, state):
        self._current_state = state
        self._states = {state}
        self._transitions = {}
        self._logger = logging.getLogger(self.__class__.__name__)

    def inflateTransitionsFromJSON(self,conditionInflator, actionInflator, s):
        pass
    def addTransition(self,from_,to,transition_condition, transition_action):
        self._states.add(from_)
        self._states.add(to)
        if (from_,to) not in self._transitions:
            self._transitions[(from_,to)] = []
        self._transitions[(from_,to)].append((transition_condition, transition_action))
    def _setState(self,state):    
        assert state in self._states, f"{state} not in {self._states}"
        self._current_state = state
        self._onSetStateCallback(state)
    def _onSetStateCallback(self,state):
        self._logger.info(f"state: {state}")
