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
import json


class StateMachine:
    def __init__(self, state):
        self._current_state = state
        self._states = {state}
        self._transitions = {}
        self._logger = logging.getLogger(self.__class__.__name__)

    def inflateTransitionsFromJSON(self, conditionInflator, actionInflator, s):
        pass

    def addTransition(self, from_, to, transition_condition, transition_action):
        self._states.add(from_)
        self._states.add(to)
        if (from_, to) not in self._transitions:
            self._transitions[(from_, to)] = []
        self._transitions[(from_, to)].append(
            (transition_condition, transition_action))

    def _setState(self, state):
        assert state in self._states, f"{state} not in {self._states}"
        self._current_state = state
        self._onSetStateCallback(state)

    def _onSetStateCallback(self, state):
        self._logger.info(f"state: {state}")

    def __call__(self, input_message):
        for state in self._states:
            if (self._current_state, state) not in self._transitions:
                continue
            for transition_condition, transition_action in self._transitions[(self._current_state, state)]:
                if transition_condition(im):
                    self._setState(state)
                    return transition_action(input_message)
        self._didNotFoundSuitableTransition(input_message)
        return None

    def _didNotFoundSuitableTransition(self, input_message):
        self._logger(
            f"{input_message}: did not found suitable transition -- returning None")

    def __str__(self):
        return "\n".join([f"{s} => {e}"for s, e in self._transitions])

    def toJsonString(self,predicatePrinter=lambda _:None, transitionPrinter=lambda _:None):
        return json.dumps({
            "currentState": self._current_state,
            "states": {s: 1 for s in self._states},
            "transitions": {
                json.dumps([s, e]): [[predicatePrinter(tc), transitionPrinter(ta)] for tc, ta in l]
                for ((s, e), l)
                in self._transitions.items()
            },
        })
    def inflateTransitionsFromJSON(self,conditionInflator, actionInflator, s):
        for ss,es,c,a in json.loads(s):
            condition = conditionInflator(c)
            action = actionInflator(a)
            if ss is not None and es in not None:
                self.addTransition(ss,es,condition,action)
            elif ss is None and es is not None:
                for s in self._states:
                    self.addTransition(s,es,condition,action)
            else:
                raise NotImplementedError(f"{ss,es}")
