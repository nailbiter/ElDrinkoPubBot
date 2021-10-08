"""===============================================================================

        FILE: ./py/eldrinko/__init__.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-01T23:36:09.964833
    REVISION: ---

==============================================================================="""

from collections import namedtuple
from py.eldrinko.util import UserDbEntry

ElDrinkoInputMessage = namedtuple(
    "ElDrinkoInputMessage", "input_message data user_data beerlist")
