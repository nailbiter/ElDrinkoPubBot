#!/usr/bin/env python3
"""===============================================================================

        FILE: ./bin/edit-transitions.py

       USAGE: ././bin/edit-transitions.py

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-12T21:20:01.647196
    REVISION: ---

==============================================================================="""

import click
import json
import random
import os
from os import path
from graphviz import Digraph
import json

_ANYSTATE = "da0b607fc3f252df"


def _get_random_string(k=16):
    return "".join(random.choices(population=list("01234567890abcdef"), k=k))


@click.group()
@click.option("--template-folder", type=click.Path(), envvar="TEMPLATE_FOLDER")
@click.pass_context
def edit_transitions(ctx, **kwargs):
    ctx.ensure_object(dict)
    for k, v in kwargs.items():
        ctx.obj[k] = v


@edit_transitions.command()
@click.option("-m", "--message-type", type=click.Choice(["TelegramKeyboard", "TelegramArrayOutputMessage", "TelegramTextOutputMessage"]))
@click.option("--create-files/--no-create-files", default=True)
@click.option("--create-new-transition/--no-create-new-transition", default=True)
@click.pass_context
def add_transition(ctx, message_type, create_files, create_new_transition):
    transitions_fn = f"{ctx.obj['template_folder']}/transitions.json"
    with open(transitions_fn) as f:
        transitions = json.load(f)
    if create_new_transition:
        last_key = _get_random_string()
    else:
        last_key = list(transitions.keys())[-1]

    files_to_create = []
    if message_type == "TelegramKeyboard":
        keyboard = _get_random_string(24)
        message = _get_random_string(24)
        transitions[last_key] = {
            "tag": message_type,
            "keyboard": keyboard,
            "message": message
        }
        files_to_create = [keyboard, message]
    elif message_type=="TelegramTextOutputMessage":
        message = _get_random_string(24)
        transitions[last_key] = {
            "tag": message_type,
            "message": message
        }
        files_to_create = [message]
    elif message_type == "TelegramArrayOutputMessage":
        transitions[last_key] = []
    else:
        raise NotImplementedError(message_type)

    if create_files:
        for k in files_to_create:
            fn = f"{ctx.obj['template_folder']}/{k}.txt"
            os.system(f"touch {fn} && git add {fn}")
    with open(transitions_fn, "w") as f:
        json.dump(transitions, f, indent=2)


_EDGE_STYLES = {
    "ConjunctionPredicate": {"style": "dotted"},
    "IsPhoneNumberPredicate": {"style": "dashed"},
    "IsTextMessagePredicate": {"style": "bold"},
    "MessageComparisonPredicate": {"arrowhead": "ediamond"},
    "MessageKeyboardComparisonPredicate": {"arrowhead": "diamond"},
    "NegationPredicate": {"arrowhead": "obox"},
    "WidgetPredicate": {"arrowhead": "box"},
    "TrivialPredicate": {},
}


@edit_transitions.command()
@click.option("--gv-filename", type=click.Path(), default=".tmp/state_machine.gv")
@click.option("--pic-filename", type=click.Path())
@click.pass_context
def print_gv(ctx, gv_filename, pic_filename):
    #    if pic_filename is None:
    #        base,ext = path.splitext(gv_filename)
    #        pic_filename = f"{base}.png"
    data = {}
    for k in "transitions correspondence".split(" "):
        fn = path.join(ctx.obj["template_folder"], f"{k}.json")
        with open(fn) as f:
            data[k] = json.load(f)

    # click.echo(data["correspondence"])
    dot = Digraph()
    for ss, es, cond, corr in data["correspondence"]:
        if ss is None:
            ss = _ANYSTATE
        label = corr["correspondence"][:6]
#        if "value" in cond:
#            label = f"{label},{corr['value']}"
        dot.edge(ss, es, label=label, **_EDGE_STYLES[cond["tag"]])
    click.echo(json.dumps(_EDGE_STYLES, indent=2))
    dot.node(_ANYSTATE, label="ANY STATE")
    dot.view()


if __name__ == "__main__":
    edit_transitions()
