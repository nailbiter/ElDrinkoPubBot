"""===============================================================================

        FILE: py/eldrinko/action/../../util/ukrainian_floats.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-03-04T22:28:00.368958
    REVISION: ---

==============================================================================="""
from py.util import add_logger
import re


@add_logger
def print(x, width=2, logger=None):
    logger.info(f"x: {x}")
    res = f"{x:.{width}f}".replace(".", ",")
    logger.info(f"res: {res}")
    return res


def parse(s):
    m = re.match(r"(-?\d+)(,\d+)?", s)
    assert m is not None
    return float(s.replace(",", "."))
