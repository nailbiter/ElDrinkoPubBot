from flask import Flask, render_template, request
from datetime import datetime, date
from re import match
import re
from pymongo import MongoClient
import pandas as pd
import logging
from _beerserver import get_mongo_client, get_orders


logging.basicConfig(level=logging.INFO)
app = Flask(__name__)


@app.route("/added_beeritem", methods=["POST"])
def added_beeritem():
    mongo_client = get_mongo_client()
    r = {k: v for k, v in request.form.items()}
    mongo_client.beerbot.proto_beerlist.insert_one(r)
    msg = f"added {r}"
    return f"""
    {msg}<br>
    {_format_beerlist(mongo_client,request)}
    """


@app.route("/refresh_db", methods=["POST"])
def refresh_db():
    mongo_client = get_mongo_client()
    for cn in ["proto_data"]:
        coll = mongo_client.beerbot[cn]
        coll.delete_one({"id": "145766172"})
    return "done"


def _format_beerlist_table_html(mongo_client, collname):
    beerlist = pd.DataFrame([
        {
            **({"delete": f"{request.url_root}delete_beeritem/{r['name']}"} if collname == "proto_beerlist" else {}),
            **r
        }
        for i, r
        in enumerate(mongo_client.beerbot[collname].find())
    ])
    beerlist = beerlist.drop(columns=["_id"])
    table_html = beerlist.to_html(index=None, render_links=True)
    return table_html


def _format_beerlist(mongo_client, request, msg=None):
    if msg is None:
        msg_ = ""
    else:
        msg_ = f"{msg}<br>"
    return f"""
    {msg_}
    <p>proto items</p>
    {_format_beerlist_table_html(mongo_client,'proto_beerlist')}
    <p>production items</p>
    {_format_beerlist_table_html(mongo_client,'beerlist')}
    <a href="{request.url_root}add_beeritem">добавить</a><br>
    <a href="{request.url_root}load_to_prd">загрузить в боевой бот</a><br>
    <a href="{request.url_root}load_from_prd">обнулить изменения</a><br>
    """


@app.route("/load_from_prd")
def load_from_prd():
    mongo_client = get_mongo_client()
    records = list(mongo_client.beerbot.beerlist.find())
    mongo_client.beerbot.proto_beerlist.drop()
    for r in records:
        mongo_client.beerbot.proto_beerlist.insert_one(
            {k: v for k, v in r.items() if k != "_id"})
    msg = f"added {len(records)} items"
    return _format_beerlist(mongo_client, request, msg)


@app.route("/load_to_prd")
def load_to_prd():
    mongo_client = get_mongo_client()
    records = list(mongo_client.beerbot.proto_beerlist.find())
    mongo_client.beerbot.beerlist.drop()
    for r in records:
        mongo_client.beerbot.beerlist.insert_one(
            {k: v for k, v in r.items() if k != "_id"})
    msg = f"added {len(records)} items"
    return _format_beerlist(mongo_client, request, msg)


@app.route("/add_beeritem")
def add_beeritem():
    mongo_client = get_mongo_client()
    r = mongo_client.beerbot.proto_beerlist.find_one()
    del r["_id"]
    return render_template("add_beeritem.jinja.html", r=r)
    # return render_template("table.jinja.html", table=pd.DataFrame(orders).to_html(index=False) if orders else "none")


@app.route("/delete_beeritem/<name>")
def delete_beeritem(name):
    mongo_client = get_mongo_client()
    res = mongo_client.beerbot.proto_beerlist.delete_one({"name": name})
    msg = f"res: {res}, removed {name}"
    return _format_beerlist(mongo_client, request, msg)


@app.route("/beerlist")
def beerlist():
    mongo_client = get_mongo_client()
    return _format_beerlist(mongo_client, request)


@app.route('/')
@app.route('/<date>')
def hello_world(date=None):
    if date is None:
        date = datetime.now().strftime("%Y-%m-%d")
    elif date == "all":
        date = None
    elif date == "refresh_db":
        return render_template("refresh_db.jinja.html")
    elif date != "all" and match(r"^\d{4}-\d{2}-\d{2}$", date) is None:
        return f"""date should be in the format "YYYY-MM-DD" (or absent)!<br>(received: "{date}")"""
    else:
        raise NotImplementedError
    orders = get_orders(date)
    return render_template("table.jinja.html", table=pd.DataFrame(orders).to_html(index=False) if orders else "none")
