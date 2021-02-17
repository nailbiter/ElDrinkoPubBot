#!/usr/bin/env python3
"""===============================================================================

        FILE: ./bin/reset-db.py

       USAGE: ././bin/reset-db.py

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-17T23:06:17.415164
    REVISION: ---

==============================================================================="""

import click
from pymongo import MongoClient
import itertools


@click.command()
@click.option("-c", "--chat-id", default=340880765, type=int)
@click.option("--mongo-url", envvar="MONGO_URL")
def reset_db(chat_id, mongo_url):
    client = MongoClient(mongo_url)
    for p,cn in itertools.product(["","proto_","dev_"],["data","state_machine_states"]):
        client.beerbot[p+cn].delete_one({"id":str(chat_id)})


if __name__ == "__main__":
    reset_db()
