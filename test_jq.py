"""===============================================================================

        FILE: test_jq.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION:

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (nailbiter@dtws-work.in)
ORGANIZATION: Datawise Inc.
     VERSION: ---
     CREATED: 2020-11-26T10:58:19.901667
    REVISION: ---

==============================================================================="""
import json
import os


def test_jq():
    jsons = []
    ignores = set()
    for root, _, files in os.walk("./src/main/resources", topdown=False):
        for name in files:
            fn = os.path.join(root, name)
            if fn.startswith("./venv"):
                continue
            if fn.endswith(".json"):
                jsons.append(fn)
            if name == ".ignore_format":
                with open(fn) as of:
                    files2 = [l.strip() for l in of.readlines()]
                for fn2 in files2:
                    ignores.add(os.path.join(root, fn2))

    for fn in jsons:
        if fn in ignores:
            continue
        with open(fn) as f:
            text = f.read()
        with open(fn) as f:
            data = json.load(f)
        assert text.strip() == json.dumps(
            data, indent=2, sort_keys=True, ensure_ascii=False), fn
