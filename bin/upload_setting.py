#!/usr/bin/env python3
import sys
import json
from argparse import ArgumentParser
from pymongo import MongoClient
from jinja2 import Template



#global const's

#main
parser = ArgumentParser()
parser.add_argument("data_file",help="json data file")
parser.add_argument("--id",help="unique id in collection")
parser.add_argument("--password",help="password")
parser.add_argument("--connection_string",help="connection string")
parser.add_argument("--collection",help="collection")

args = parser.parse_args()
assert(args.password is not None)
assert(args.connection_string is not None)
assert(args.collection is not None)

with open(args.data_file) as f:
    doc = json.load(f)

client = MongoClient(Template(args.connection_string).render({"password":args.password}))
coll = client.beerbot[args.collection]
if args.id is None:
    coll.insert_one(doc)
else:
    coll.replace_one({args.id:doc[args.id]},doc,upsert=True)

for v in doc["mongodb"].values():
    client.beerbot[v]
