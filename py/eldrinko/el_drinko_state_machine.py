"""===============================================================================

        FILE: ./py/eldrinko/el_drinko_state_machine.py

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

from py.state_machine.exposed_state_machine import ExposedStateMachine
from py.util.download_cache import DownloadCache
from jinja2 import Template
from time import time
from os import path
import logging
import traceback


class ElDrinkoStateMachine(ExposedStateMachine):
    def __init__(self, sendOrderCallback, template_folder):
        super().__init__("_")
        self._sendOrderCallback = sendOrderCallback
        self._logger = logging.getLogger(self.__class__.__name__)
        with open(path.join(template_folder, "user_error_message.txt")) as f:
            self._tpl = Template(f.read())

    @staticmethod
    def PreloadImages(coll):
        dc = DownloadCache(".png")
        for r in coll.find():
            print(r)
            dc(r["image link"])

    def _exception_handler(self, exception, input_message):
        self._eldrinko_exception_handler(input_message, exception=exception)

    def _eldrinko_exception_handler(self, input_message, exception=None):
        error_code = int(time())
        if exception is not None:
            self._logger.info(traceback.format_exc())
            self._logger.info(f"exception: {exception}")
            error_code = -error_code
        self._logger.error(f"error code: {error_code}")
        self._sendOrderCallback.send_message(self._tpl.render(
            {"error_code": error_code}), str(input_message.user_data))

    def _didNotFoundSuitableTransition(self, im):
        self._eldrinko_exception_handler(im)
