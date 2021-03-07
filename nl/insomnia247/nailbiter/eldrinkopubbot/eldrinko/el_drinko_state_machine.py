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
from jinja2 import Template
from time import time
from os import path
import logging

class ElDrinkoStateMachine(ExposedStateMachine):
    def __init__(self,sendOrderCallback,template_folder):
        super().__init__("_")
        self._sendOrderCallback = sendOrderCallback
        self._logger = logging.getLogger(self.__class__.__name__)
        with open(path.join(template_folder,"user_error_message.txt")) as f:
            self._tpl = Template(f.read())
    @staticmethod
    def PreloadImages(coll):
        dc = DownloadCache(".png")
        for r in coll.find():
            print(r)
            dc(r["image link"])
    def _didNotFoundSuitableTransition(self,im):
        error_code = int(time())
        self._logger.info(f"error code: {error_code}")
        self._sendOrderCallback.send_message(self._tpl.render({"error_code":error_code}),str(im.user_data))
