"""===============================================================================

        FILE: nl/insomnia247/nailbiter/eldrinkopubbot/telegram/user_data.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-09T21:56:08.940265
    REVISION: ---

==============================================================================="""

class UserData:
    def __init__(self,update):
        self._chat_id = update.effective_message.chat_id
        effective_user = update.effective_user
        self._username = effective_user.username
        self._first_name = effective_user.first_name
        self._last_name = effective_user.last_name
    def __str__(self):
        return str(self._chat_id)
    @property
    def username(self):
        return self._username
    @property
    def first_name(self):
        return self._first_name
    @property
    def last_name(self):
        return self._last_name
