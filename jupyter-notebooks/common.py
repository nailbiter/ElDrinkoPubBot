"""===============================================================================

        FILE: common.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-01-17T19:21:00.459453
    REVISION: ---

==============================================================================="""
import pandas as pd
from datetime import datetime
from pytz import timezone
from pymongo import MongoClient
from datetime import datetime
from IPython.core.display import HTML
import re
from functools import reduce
import re
import pandas as pd
import json
from functools import reduce


def _reducer(acc, val):
    l, d = acc
    if val["tag"] == "ss":
        if d:
            l.append(d)
            d = {}

    d[val["tag"]] = val["value"]
#     d[f"{val['tag']}_instant"] = val["instant"]
    d["instant"] = val["instant"]
    return (l, d)


def parse_log(fns):
    df = pd.concat([
        pd.read_json(fn, lines=True, orient="records")
        for fn in fns
    ])
    df

    _df = df[[x == "nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoPubBot" for x in df["loggerName"]]
             ].loc[:, ["instant", "message"]]
    _df = _df[[x[:2] in ["im", "om", "es", "ss"] for x in _df["message"]]]
    prog = re.compile("(om|im|es|ss)\([0-9]+\):\s*(.*)")

    data = [dict(tag=prog.match(r["message"]).group(1), value=prog.match(r["message"]).group(
        2), instant=r["instant"]) for r in _df.to_dict(orient="records") if prog.match(r["message"]) is not None]

    l, d = reduce(_reducer, data, ([], {}))
    if d is not None:
        l.append(d)
    _df = pd.DataFrame(l)
    testdata_df = _df

    testdata_df["im"] = [json.loads(im) for im in testdata_df["im"]]
    testdata_df["om"] = [json.loads(im) if not pd.isna(
        im) else None for im in testdata_df["om"]]
    #testdata_df[[im["userData"]["chatId"]==211228499 for im in testdata_df["im"]]]
    testdata_df["instant"] = [datetime.fromtimestamp(
        i["epochSecond"]) for i in testdata_df["instant"]]
    testdata_df = testdata_df.set_index("instant").sort_index()
    # HTML(testdata_df.to_html())
    return testdata_df
