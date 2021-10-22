"""===============================================================================

        FILE: ./test_autopep8.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-10-13T17:22:50.042730
    REVISION: ---

==============================================================================="""

import os
import subprocess


def test_autopep8():
    ec, out = subprocess.getstatusoutput(
        "autopep8 --recursive --diff --exit-code . --exclude '**/venv/**,**/venv_test/**'")
    assert ec == 0, out
