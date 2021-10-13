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
import os

_RES_FOLDER = "./src/main/resources"
_SPECIAL_NAMES = [
    "user_error_message.txt",
    "made_order_notification.txt",
    "_order_line.jinja.txt",
]


def test_useless_correspondences():
    used_correspondences = {o["correspondence"]
                            for _, _, _, o in correspondence}
    unused_correspondences = set(transitions)-used_correspondences
    assert len(unused_correspondences) == 0, ",".join(
        sorted(list(unused_correspondences)))


def _add_file(l, obj):
    for k in "keyboard,message".split(","):
        if k in obj:
            l.add(obj[k])


def test_useless_resources():
    files = os.listdir(_RES_FOLDER)
    files = {fn[:-4]
             for fn in files if fn.endswith(".txt") and fn not in _SPECIAL_NAMES}

    used_files = set()
    for transition in transitions.values():
        if isinstance(transition, list):
            for t in transition:
                _add_file(used_files, t)
        else:
            _add_file(used_files, transition)

    unused_files = files-used_files
    assert len(unused_files) == 0, ",".join(sorted(list(unused_files)))
#    assert len(files)==0,files


with open(path.join(_RES_FOLDER, "correspondence.json")) as f:
    correspondence = json.load(f)
with open(path.join(_RES_FOLDER, "transitions.json")) as f:
    transitions = json.load(f)
