"""===============================================================================

        FILE: /home/pi/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/util/_init__.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-02T22:47:20.235202
    REVISION: ---

==============================================================================="""

import re
def parse_ukrainian_float(s):
    m = re.match(r"(-?\d+)(,\d+)?",s)
    assert m is not None
    return float(s.replace(",","."))
