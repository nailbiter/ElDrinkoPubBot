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
    1(done). FIXUP request.url_root
    2. correct redirect
==============================================================================="""
from flask import Flask, render_template, request
from datetime import datetime, date
import re
from pymongo import MongoClient
import pandas as pd
import logging
from _beerserver import get_mongo_client, get_orders, format_beerlist
import logging


logging.basicConfig(level=logging.INFO)
app = Flask(__name__)


@app.route("/added_beeritem", methods=["POST"])
def added_beeritem():
    logger = logging.getLogger(request.path)
    mongo_client = get_mongo_client()
    logger.info("got {r}")
    r = {k: v for k, v in request.form.items()}
    # TODO: validation: beer name and category should exist, price should be number
    price_fn = "price (UAH/L)"
    if re.match(r"^\d+$", r[price_fn]) is None:
        r[price_fn] = int(r[price_fn])
        msg = "could not add {r} because \"{r[price_fn]}\" is not a number"
    else:
        msg = f"added {r}"
        mongo_client.beerbot.proto_beerlist.insert_one(r)
    return format_beerlist(mongo_client, request, render_template, request.url_root, msg=msg)


@app.route("/load_from_prd")
def load_from_prd():
    mongo_client = get_mongo_client()
    msgs = []
    for coll in ["beerlist", "categories"]:
        records = list(mongo_client.beerbot[coll].find())
        mongo_client.beerbot[f"proto_{coll}"].drop()
        for r in records:
            mongo_client.beerbot[f"proto_{coll}"].insert_one(
                {k: v for k, v in r.items() if k != "_id"})
        msgs.append(f"added {len(records)} items to proto_{coll}")
    return format_beerlist(mongo_client, request, render_template, request.url_root, msg="<br>".join(msgs))


@app.route("/load_to_prd")
def load_to_prd():
    mongo_client = get_mongo_client()
    msgs = []
    for coll in ["beerlist", "categories"]:
        records = list(mongo_client.beerbot[f"proto_{coll}"].find())
        mongo_client.beerbot[coll].drop()
        for r in records:
            mongo_client.beerbot[coll].insert_one(
                {k: v for k, v in r.items() if k != "_id"})
        msgs.append(f"added {len(records)} items to {coll}")

    return format_beerlist(mongo_client, request, render_template, request.url_root, msg="<br>".join(msgs))


# FIXME: merge with add_category
@app.route("/add_beeritem")
def add_beeritem():
    mongo_client = get_mongo_client()
    r = mongo_client.beerbot.proto_beerlist.find_one()
    del r["_id"]
    return render_template("add_item.jinja.html", 
        r={
            **{k:"text" for k in r if k!="category"}, 
            "category":[r["name"] for r in mongo_client.beerbot.proto_categories.find()]
        }, 
        action="added_beeritem"
    )


@app.route("/delete/<what>/<name>")
def delete_beeritem(what, name):
    mongo_client = get_mongo_client()

    if what == "beeritem":
        res = mongo_client.beerbot.proto_beerlist.delete_one({"name": name})
        msg = f"res: {res}, removed {name}"
        return format_beerlist(mongo_client, request, render_template, request.url_root, msg)
    if what == "category":
        if mongo_client.beerbot.proto_beerlist.find_one({"category": name}) is not None:
            msg = f"cannot remove {name}, since some beerlist items depend on it"
        else:
            res = mongo_client.beerbot.proto_categories.delete_one({
                                                                   "name": name})
            msg = f"res: {res}, removed {name}"

        return render_template("categories.jinja.html",
                               mongo_client=mongo_client,
                               url_root=request.url_root,
                               msg=msg
                               )
    else:
        raise NotImplementedError(f"what: {what}")


@app.route("/move/<what>/<direction>/<name>")
def move(what, direction, name):
    assert direction in ["up", "down"]
    mongo_client = get_mongo_client()
    if what == "beeritem":
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
        return format_beerlist(mongo_client, request, render_template, request.url_root, msg=msg)
    elif what == "category":
        # FIXME: merge with logic in previous clause, use one function
        res = pd.DataFrame(mongo_client.beerbot.proto_categories.find())
        res = res.drop(columns=["_id"])
        idx = int(name)
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

        mongo_client.beerbot.proto_categories.drop()
        for r in res:
            mongo_client.beerbot.proto_categories.insert_one(r)

        msg = f"moved {name} {direction}"
        return render_template("categories.jinja.html",
                               mongo_client=mongo_client,
                               url_root=request.url_root,
                               msg=msg
                               )
    else:
        raise NotImplementedError(f"what: {what}")


@app.route("/beerlist")
def beerlist():
    mongo_client = get_mongo_client()
    return format_beerlist(mongo_client, request, render_template, request.url_root)

# @app.route("/delete_category/<int:idx>")
# def delete_category(idx):
#    mongo_client = get_mongo_client()
#    return render_template("categories.jinja.html",mongo_client=mongo_client,url_root=request.url_root)


@app.route("/add_category")
def add_category():
    mongo_client = get_mongo_client()
    r = mongo_client.beerbot.proto_categories.find_one()
    del r["_id"]
    return render_template("add_item.jinja.html", r={r:"text"for k in r}, action="added_category")


@app.route("/added_category", methods=["POST"])
def added_category():
    mongo_client = get_mongo_client()
    r = {k: v for k, v in request.form.items()}
    if mongo_client.beerbot.proto_categories.find_one({"name": r["name"]}) is not None:
        msg = f"cannot add {r} (already present)"
    else:
        msg = f"added {r}"
        mongo_client.beerbot.proto_categories.insert_one(r)
    return render_template("categories.jinja.html",
                           mongo_client=mongo_client,
                           url_root=request.url_root,
                           msg=msg
                           )


@app.route("/categories")
def categories():
    mongo_client = get_mongo_client()
    return render_template("categories.jinja.html", mongo_client=mongo_client, url_root=request.url_root)


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
