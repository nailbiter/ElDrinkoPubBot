from flask import Flask, render_template
from datetime import datetime, date
from re import match
import re
from pymongo import MongoClient
import pandas as pd
import logging


# global var's
logging.basicConfig(level=logging.INFO)
app = Flask(__name__)

# procedures
def _get_mongo_client():
    with open("secret.txt") as f:
        mongo_pass = f.read().strip()
    mongo_client = MongoClient(
        f"mongodb+srv://nailbiter:{mongo_pass}@cluster0-ta3pc.gcp.mongodb.net/beerbot?retryWrites=true&w=majority")
    return mongo_client
def _get_orders(date):
    logger = logging.getLogger("_get_orders")
    mongo_client = _get_mongo_client()
    
    if date:
        logger.info(date)
        _date = datetime.strptime(date,"%Y-%m-%d")
        regx = f".*{_date.strftime('%d.%m.%y')}.*"
        logger.info(regx)
        search_object = {"order.timestamp":{"$regex":regx}}
    else:
        search_object = {}

    res = [{**{k:v for k,v in o["order"].items() if k!="_id"}, "_timestamp":o["order"]["_timestamp"].strftime("%d.%m.%y %H:%M")} 
            for o 
            in mongo_client.beerbot.order_history.find(filter=search_object,sort=[("order._timestamp",-1)])]
    return res

@app.route("/refresh_db",methods=["POST"])
def refresh_db():
    mongo_client = _get_mongo_client()
    for cn in ["proto_data"]:
        coll = mongo_client.beerbot[cn]
        coll.delete_one({"id":"145766172"})
    return "done"
@app.route('/')
@app.route('/<date>')
def hello_world(date=None):
    if date is None:
        date = datetime.now().strftime("%Y-%m-%d")
    elif date=="all":
        date = None
    elif date=="refresh_db":
        return render_template("refresh_db.jinja.html")
    elif date != "all" and match(r"^\d{4}-\d{2}-\d{2}$", date) is None:
        return f"""date should be in the format "YYYY-MM-DD" (or absent)!<br>(received: "{date}")"""
    else:
        raise NotImplementedError
    orders = _get_orders(date)
    return render_template("table.jinja.html",table=pd.DataFrame(orders).to_html(index=False) if orders else "none")
