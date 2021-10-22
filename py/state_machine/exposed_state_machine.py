"""===============================================================================

        FILE: ./py/state_machine/exposed_state_machine.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-02T22:08:09.151139
    REVISION: ---

==============================================================================="""

from py.state_machine.state_machine import StateMachine


class ExposedStateMachine(StateMachine):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    def setState(self, state):
        self._setState(state)

    def getState(self):
        return self._current_state
