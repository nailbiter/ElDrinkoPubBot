"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/eldrinko/action/el_drinko_action_inflator.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-07T14:32:53.623252
    REVISION: ---

==============================================================================="""

class ElDrinkoActionInflator:
    def __init__(self,send_message_callback,persistent_storage,insert_order_callback):
        self._send_message_callback = send_message_callback
        self._persistent_storage = persistent_storage
        self._insert_order_callback = insert_order_callback
    def _call(self,obj,eim):
        """return (outmessage, user_data_update)"""
        pass
    def __call__(self, obj):
        return lambda eim:self._call(obj,eim)
