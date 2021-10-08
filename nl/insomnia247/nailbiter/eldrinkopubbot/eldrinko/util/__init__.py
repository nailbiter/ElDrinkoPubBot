"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/eldrinko/util/__init__.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-06-03T16:58:20.295979
    REVISION: ---

==============================================================================="""
import copy
import logging
import json


class UserDbEntry:
    def __init__(self, data):
        #        self._data = copy.deepcopy(data)
        self._data = data
        self._logger = logging.getLogger(self.__class__.__name__)

    def __getitem__(self, key):
        assert key != "order"
        return self._data[key]

    def get(self, key, default_value):
        assert key != "order"
        return self._data.get(key, default_value)

    def __setitem__(self, key, val):
        self._data[key] = val

    def as_dict(self,):
        return self._data

    @property
    def order(self,):
        obj = self._data
        logger = self._logger

        logger.info(f"obj: {obj}")
        order = obj.get("order", None)
        order = order if order is not None else {}
        order["cart"] = order.get("cart", [])
        obj["order"] = order
        return order

    def reset_order(self):
        self._data["order"] = {}

    def get_order_pretty(self,):
        obj = self._data
        return {
            **json.loads(json.dumps(self.order)),
            **{k: str(obj[k]) for k in ["address"] if k in obj}
        }

    def normalize_order(self):
        self.order["cart"] = {k: v for k,
                              v in self.order["cart"].items() if v > 0}

    def __str__(self,):
        return str(self.as_dict())
