#!/bin/sh
export FLASK_APP=beerserver.py

git pull
flask run --port 5001
