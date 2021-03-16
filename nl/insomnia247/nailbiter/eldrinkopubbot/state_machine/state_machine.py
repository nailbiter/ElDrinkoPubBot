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
from collections import namedtuple, OrderedDict
import json
import traceback
import uuid


class StateMachine:
    def __init__(self, state):
        self._current_state = state
        self._states = {state}
        self._transitions = []
        self._logger = logging.getLogger(self.__class__.__name__)

    def addTransition(self, from_, to, transition_condition, transition_action):
        self._states.add(from_)
        self._states.add(to)
        self._logger.info((from_, to, transition_condition))
        self._transitions.append(
            (from_, to, transition_condition, transition_action))

    def _setState(self, state):
        assert state in self._states, f"{state} not in {self._states}"
        self._current_state = state
        self._onSetStateCallback(state)

    def _onSetStateCallback(self, state):
        self._logger.info(f"set state: {state}")

    def __call__(self, input_message):
        try:
#            x = 1/0
#            self._logger.info(x)
            for k, state, transition_condition, transition_action in self._transitions:
                if k != self._current_state:
                    self._logger.info(f"{k}!={self._current_state}")
                    continue
                self._logger.info(f"checking condition {transition_condition}")
                if transition_condition(input_message):
                    self._logger.info("take")
                    self._setState(state)
                    res = transition_action(input_message)
                    self._logger.info(f"res: {res}")
                    return res
                else:
                    self._logger.info("pass")
        except Exception as exception:
#            self._logger.info("\n".join(traceback.format_stack()))
#            exc_fn = f"/tmp/{uuid.uuid4()}.log.txt"
#            with open(exc_fn,"w") as f:
#                traceback.print_tb(exception.__traceback__,file=f)
#            self._logger.info(f"exception data saved to {exc_fn}")
            self._exception_handler(exception, input_message)

        self._didNotFoundSuitableTransition(input_message)
        return None

    def _exception_handler(self, exception, input_message):
        self._logger.error(f"exception: {exception}")
        raise exception

    def _didNotFoundSuitableTransition(self, input_message):
        self._logger(
            f"{input_message}: did not found suitable transition -- returning None")

    def __str__(self):
        return "\n".join([f"{s} => {e}"for s, e, *_ in self._transitions])

#    def toJsonString(self, predicatePrinter=lambda _: None, transitionPrinter=lambda _: None):
#        return json.dumps({
#            "currentState": self._current_state,
#            "states": {s: 1 for s in self._states},
#            "transitions": {
#                json.dumps([s, e]): [[predicatePrinter(tc), transitionPrinter(ta)] for tc, ta in l]
#                for ((s, e), l)
#                in self._transitions.items()
#            },
#        })

    def inflateTransitionsFromJSON(self, conditionInflator, actionInflator, s):
        transitions = json.loads(s)
        for ss, es, *_ in transitions:
            self._states.update([x for x in [ss, es] if x is not None])
        self._logger.info(self._states)
        for ss, es, c, a in transitions:
            condition = conditionInflator(c)
            action = actionInflator(a, src_state=ss, dst_state=ss)
            if ss is not None and es is not None:
                self.addTransition(ss, es, condition, action)
            elif ss is None and es is not None:
                for s in self._states:
                    self.addTransition(s, es, condition, action)
            else:
                raise NotImplementedError(f"{ss,es}")
        self._logger.info([(ss, es, str(c))
                           for ss, es, c, a in self._transitions])
