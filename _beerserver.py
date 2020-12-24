"""===============================================================================

        FILE: _beerserver.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2020-12-19T18:12:29.921622
    REVISION: ---

==============================================================================="""
from pymongo import MongoClient
import logging
from datetime import datetime
import html
import pandas as pd


_ROOT_URL = "https://48ad68dd0e23.ngrok.io/"


def add_logger(f):
    logger = logging.getLogger(f.__name__)

    def _f(*args, **kwargs):
        return f(*args, logger=logger, **kwargs)
    _f.__name__ = f.__name__
    return _f


def get_mongo_client():
    with open("secret.txt") as f:
        mongo_pass = f.read().strip()
    mongo_client = MongoClient(
        f"mongodb+srv://nailbiter:{mongo_pass}@cluster0-ta3pc.gcp.mongodb.net/beerbot?retryWrites=true&w=majority")
    return mongo_client


@add_logger
def get_orders(date, logger=None):
    mongo_client = get_mongo_client()

    if date:
        logger.info(date)
        _date = datetime.strptime(date, "%Y-%m-%d")
        regx = f".*{_date.strftime('%d.%m.%y')}.*"
        logger.info(regx)
        search_object = {"order.timestamp": {"$regex": regx}}
    else:
        search_object = {}

    res = [{**{k: v for k, v in o["order"].items() if k != "_id"}, "_timestamp":o["order"]["_timestamp"].strftime("%d.%m.%y %H:%M")}
           for o
           in mongo_client.beerbot.order_history.find(filter=search_object, sort=[("order._timestamp", -1)])]
    return res


def format_beerlist_table_html(mongo_client, collname):
    beerlist = pd.DataFrame([
        {
            **({"ctrl": "".join(map(lambda t: html.escape(f"<a href=\"{t[1]}\">{t[0]}</a>"), {"del": f"{_ROOT_URL}delete_beeritem/{r['name']}"}.items()))} if collname == "proto_beerlist" else {}),
            **r
        }
        for i, r
        in enumerate(mongo_client.beerbot[collname].find())
    ])
    beerlist = beerlist.drop(columns=["_id"])
    table_html = beerlist.to_html(index=None, render_links=True)
    return table_html


def format_beerlist(mongo_client, request, msg=None):
    if msg is None:
        msg_ = ""
    else:
        msg_ = f"{msg}<br>"
    return f"""
    {msg_}
    <p>proto items</p>
    {format_beerlist_table_html(mongo_client,'proto_beerlist')}
    <p>production items</p>
    {format_beerlist_table_html(mongo_client,'beerlist')}
    <a href="{_ROOT_URL}add_beeritem">добавить</a><br>
    <a href="{_ROOT_URL}load_to_prd">загрузить в боевой бот</a><br>
    <a href="{_ROOT_URL}load_from_prd">обнулить изменения</a><br>
    """
