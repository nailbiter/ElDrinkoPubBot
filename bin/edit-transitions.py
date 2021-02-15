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

def _get_random_string(k=16):
    return "".join(random.choices(population=list("01234567890abcdef"),k=k))

@click.group()
@click.option("--template-folder", type=click.Path(),envvar="TEMPLATE_FOLDER")
@click.pass_context
def edit_transitions(ctx,**kwargs):
    ctx.ensure_object(dict)
    for k,v in kwargs.items():
        ctx.obj[k] = v

@edit_transitions.command()
@click.option("-m","--message-type",type=click.Choice(["TelegramKeyboard"]))
@click.option("--create-files/--no-create-files",default=True)
@click.option("--create-new-transition/--no-create-new-transition",default=True)
@click.pass_context
def add_transition(ctx,message_type,create_files,create_new_transition):
    transitions_fn = f"{ctx.obj['template_folder']}/transitions.json"
    with open(transitions_fn) as f:
        transitions = json.load(f)
    if create_new_transition:
        last_key = _get_random_string()
    else:
        last_key = list(transitions.keys())[-1]
    if message_type=="TelegramKeyboard":
        keyboard = _get_random_string(24)
        message = _get_random_string(24)
        transitions[last_key] = {
            "tag":"TelegramKeyboard",
            "keyboard":keyboard,
            "message":message
        }
        with open(transitions_fn,"w") as f:
            json.dump(transitions,f,indent=2)
        if create_files:
            for k in [keyboard,message]:
                fn = f"{ctx.obj['template_folder']}/{k}.txt"
                os.system(f"touch {fn} && git add {fn}")
    else:
        raise NotImplementedError(message_type)

if __name__=="__main__":
    edit_transitions()
