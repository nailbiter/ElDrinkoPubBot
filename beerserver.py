from flask import Flask, render_template
from datetime import datetime, date
from re import match
import re
from pymongo import MongoClient
import pandas as pd
import logging
from _beerserver import get_mongo_client, get_orders


# global var's
logging.basicConfig(level=logging.INFO)
app = Flask(__name__)

# procedures




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
    elif date != "all" and match(r"^\d{4}-\d{2}-\d{2}$", date) is None:
        return f"""date should be in the format "YYYY-MM-DD" (or absent)!<br>(received: "{date}")"""
    else:
        raise NotImplementedError
    orders = get_orders(date)
    return render_template("table.jinja.html", table=pd.DataFrame(orders).to_html(index=False) if orders else "none")
