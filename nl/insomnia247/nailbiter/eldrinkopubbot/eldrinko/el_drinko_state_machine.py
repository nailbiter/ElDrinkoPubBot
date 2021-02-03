"""===============================================================================

        FILE: ./nl/insomnia247/nailbiter/eldrinkopubbot/eldrinko/el_drinko_state_machine.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-02T22:17:04.959323
    REVISION: ---

==============================================================================="""

from nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.exposed_state_machine import ExposedStateMachine
from nl.insomnia247.nailbiter.eldrinkopubbot.util.download_cache import DownloadCache

class ElDrinkoStateMachine(ExposedStateMachine):
    def __init__(self,sendOrderCallback):
        super().__init__("_")
        self._sendOrderCallback = sendOrderCallback
    @staticmethod
    def PreloadImages(coll):
        dc = DownloadCache(".png")
        for r in coll.find():
            print(r)
            dc(r["image link"])
