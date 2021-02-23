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
import pandas as pd

_USERS = {
    "me": "340880765",
    "dad": "145766172"
}
_NAMES = {
    "dev": "DevElDrinkoPubBot",
    "prd": "ElDrinkoPubBot",
    "stg": "ProtoElDrinkoPubBot"
}


@click.group()
@click.option("--mongo-url", envvar="MONGO_URL")
@click.pass_context
def db(ctx, mongo_url):
    ctx.ensure_object(dict)
    client = MongoClient(mongo_url)
    ctx.obj["client"] = client
    settings = list(client.beerbot["_settings"].find())
    settings = {r["id"]: r for r in settings}
    ctx.obj["settings"] = {k: settings[v] for k, v in _NAMES.items()}


@db.command()
@click.option("-c", "--chat-id", default="me", type=click.Choice(list(_USERS)))
@click.pass_context
def reset(ctx, chat_id):
    client = ctx.obj["client"]
    for o, cn in itertools.product(ctx.obj["settings"].values(), ["data", "state_machine_states"]):
        client.beerbot[o["mongodb"][cn]].delete_one({"id": _USERS[chat_id]})


@db.command()
@click.argument("src", type=click.Choice(["dev", "prd", "stg"]))
@click.argument("dst", type=click.Choice(["dev", "prd", "stg"]))
@click.option("-w", "--what", type=click.Choice(["beerlist"]), default="beerlist")
@click.option("--dry-run/--no-dry-run",default=False)
@click.pass_context
def mv(ctx, src, dst, what,dry_run):
    assert dst != "prd"
    src, dst = [ctx.obj["settings"][k]["mongodb"][what] for k in [src, dst]]
    click.echo(f"{src} => {dst}")
    client = ctx.obj["client"]
    df = pd.DataFrame(client.beerbot[src].find())
    df = df.drop(columns=["_id"])
    click.echo(f"going to insert:\n{df}")
    if not dry_run:
        client.beerbot.drop_collection(dst)
        client.beerbot[dst].insert_many(df.to_dict(orient="records"))
    else:
        click.echo("dry run")


if __name__ == "__main__":
    db()
