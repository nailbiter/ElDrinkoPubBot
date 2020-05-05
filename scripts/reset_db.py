#!/usr/bin/env python3
from pymongo import MongoClient


#global const's

#main
print("hi")
with open("secret.json") as f:
    mongo_pass = f.read()
print(f"mongo_pass: {mongo_pass}")
client = MongoClient(f"mongodb+srv://nailbiter:{password}@cluster0-ta3pc.gcp.mongodb.net/beerbot?retryWrites=true&w=majority")
