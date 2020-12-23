#!/usr/bin/env python3
"""===============================================================================

        FILE: bin/download_table.py

       USAGE: ./bin/download_table.py

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2020-12-23T17:15:50.511323
    REVISION: ---

==============================================================================="""

import click
from pymongo import MongoClient
import pandas as pd

def _get_mongo_client():
    with open("secret.txt") as f:
        mongo_pass = f.read().strip()
    mongo_client = MongoClient(
        f"mongodb+srv://nailbiter:{mongo_pass}@cluster0-ta3pc.gcp.mongodb.net/beerbot?retryWrites=true&w=majority")
    return mongo_client

@click.command()
@click.argument("what",type=click.Choice(["prd","stg"]))
def download_table(what):
    client = _get_mongo_client()
    if what=="stg":
        df = pd.DataFrame(client.beerbot.proto_beerlist.find())
    elif what=="prd":
        df = pd.DataFrame(client.beerbot.beerlist.find())
    else:
        raise NotImplementedError(f"what: {what}")
    df = df.drop(columns=["_id"])
    df.to_csv("./src/main/resources/eldrinkopubbot.tsv",index=None,sep="\t")
    print(df)

if __name__=="__main__":
    download_table()
