"""===============================================================================

        FILE: beerserver.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2020-12-23T14:46:12.609298
    REVISION: ---

TODO: 
    1. FIXUP request.url_root
==============================================================================="""
from flask import Flask, render_template, request
from datetime import datetime, date
import re
from pymongo import MongoClient
import pandas as pd
import logging
from _beerserver import get_mongo_client, get_orders, format_beerlist


logging.basicConfig(level=logging.INFO)
app = Flask(__name__)


@app.route("/added_beeritem", methods=["POST"])
def added_beeritem():
    mongo_client = get_mongo_client()
    r = {k: v for k, v in request.form.items()}
    # TODO: validation: beer name and category should exist, price should be number
    price_fn = "price (UAH/L)"
    if re.match(r"^\d+$", r[price_fn]) is None:
        r[price_fn] = int(r[price_fn])
        msg = "could not add {r} because \"{r[price_fn]}\" is not a number"
    else:
        msg = f"added {r}"
        mongo_client.beerbot.proto_beerlist.insert_one(r)
    return format_beerlist(mongo_client, request, render_template, msg=msg)


@app.route("/load_from_prd")
def load_from_prd():
    mongo_client = get_mongo_client()
    records = list(mongo_client.beerbot.beerlist.find())
    mongo_client.beerbot.proto_beerlist.drop()
    for r in records:
        mongo_client.beerbot.proto_beerlist.insert_one(
            {k: v for k, v in r.items() if k != "_id"})
    msg = f"added {len(records)} items"
    return format_beerlist(mongo_client, request, render_template, msg)


@app.route("/load_to_prd")
def load_to_prd():
    mongo_client = get_mongo_client()
    records = list(mongo_client.beerbot.proto_beerlist.find())
    mongo_client.beerbot.beerlist.drop()
    for r in records:
        mongo_client.beerbot.beerlist.insert_one(
            {k: v for k, v in r.items() if k != "_id"})
    msg = f"added {len(records)} items"
    return format_beerlist(mongo_client, request, render_template, msg)


@app.route("/add_beeritem")
def add_beeritem():
    mongo_client = get_mongo_client()
    r = mongo_client.beerbot.proto_beerlist.find_one()
    del r["_id"]
    return render_template("add_beeritem.jinja.html", r=r)


@app.route("/delete_beeritem/<name>")
def delete_beeritem(name):
    mongo_client = get_mongo_client()
    res = mongo_client.beerbot.proto_beerlist.delete_one({"name": name})
    msg = f"res: {res}, removed {name}"
    return format_beerlist(mongo_client, request, render_template, msg)


@app.route("/move_beeritem/<direction>/<name>")
def move_beeritem(direction, name):
    assert direction in ["up", "down"]
    mongo_client = get_mongo_client()
    res = pd.DataFrame(mongo_client.beerbot.proto_beerlist.find())
    res = res.drop(columns=["_id"])
    idx = list(res["name"]).index(name)
    res = res.to_dict(orient="records")
    if direction == "down":
        if idx+1 < len(res):
            t = res[idx+1]
            res[idx+1] = res[idx]
            res[idx] = t
    elif direction == "up":
        if idx > 0:
            t = res[idx-1]
            res[idx-1] = res[idx]
            res[idx] = t

    mongo_client.beerbot.proto_beerlist.drop()
    for r in res:
        mongo_client.beerbot.proto_beerlist.insert_one(r)

    msg = f"moved {name} {direction}"
    return format_beerlist(mongo_client, request, render_template, msg)


@app.route("/beerlist")
def beerlist():
    mongo_client = get_mongo_client()
    return format_beerlist(mongo_client, request, render_template)

#@app.route("/delete_category/<int:idx>")
#def delete_category(idx):
#    mongo_client = get_mongo_client()
#    return render_template("categories.jinja.html",mongo_client=mongo_client,url_root=request.url_root)

@app.route("/add_category")
def add_category():
    pass
@app.route("/categories")
def categories():
    mongo_client = get_mongo_client()
    return render_template("categories.jinja.html",mongo_client=mongo_client,url_root=request.url_root)


@app.route("/refresh_db", methods=["POST"])
def refresh_db():
    mongo_client = get_mongo_client()
    for cn in ["proto_data"]:
        coll = mongo_client.beerbot[cn]
        coll.delete_one({"id": "145766172"})
    return "done"


@app.route('/')
@app.route('/<date>')
def hello_world(date=None):
    if date is None:
        date = datetime.now().strftime("%Y-%m-%d")
    elif date == "all":
        date = None
    elif date == "refresh_db":
        return render_template("refresh_db.jinja.html")
    elif date != "all" and re.match(r"^\d{4}-\d{2}-\d{2}$", date) is None:
        return f"""date should be in the format "YYYY-MM-DD" (or absent)!<br>(received: "{date}")"""
    else:
        raise NotImplementedError
    orders = get_orders(date)
    return render_template("table.jinja.html", table=pd.DataFrame(orders).to_html(index=False) if orders else "none")
