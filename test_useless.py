"""===============================================================================

        FILE: ./test_useless_resources.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-10-13T17:35:25.527903
    REVISION: ---

==============================================================================="""

import json
from os import path

_RES_FOLDER = "./src/main/resources"


def test_useless_resources():
    with open(path.join(_RES_FOLDER, "correspondence.json")) as f:
        correspondence = json.load(f)
    with open(path.join(_RES_FOLDER, "transitions.json")) as f:
        transitions = json.load(f)
    used_correspondences = {o["correspondence"]
                            for _, _, _, o in correspondence}
    unused_correspondences = set(transitions)-used_correspondences
    assert len(unused_correspondences) == 0, unused_correspondences
