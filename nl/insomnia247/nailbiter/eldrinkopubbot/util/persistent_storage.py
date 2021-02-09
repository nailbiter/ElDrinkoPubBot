"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/util/persistent_storage.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-07T14:42:30.105870
    REVISION: ---

==============================================================================="""


class PersistentStorage:
    def __init__(self, coll, field_value, field_name="id"):
        self._coll = coll
        self._field_value = field_value
        self._field_name = field_name

    def get(self, key, default_value):
        doc = self._coll.find_one({self._field_name: self._field_value})
        if doc is None:
            doc = {}
        return doc.get(key, default_value)

    def set(self, key, val):
        self._coll.update_one({self._field_name: self._field_value}, {
                              key: val}, upsert=True)
