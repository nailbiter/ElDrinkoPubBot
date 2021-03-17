#!/usr/bin/env python3
"""===============================================================================

        FILE: bin/sample-render.py

       USAGE: ./bin/sample-render.py

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-03-17T22:05:51.045182
    REVISION: ---

==============================================================================="""

import click
from os import path
from jinja2 import Environment
from jinja2.loaders import FileSystemLoader
import json

@click.command()
@click.argument("template-filename",type=click.Path())
@click.option("--template-folder",envvar="TEMPLATE_FOLDER",type=click.Path())
def sample_render(template_filename,template_folder):
    jinja_env = Environment(loader=FileSystemLoader(template_folder))
    with open(path.join(path.split(__file__)[0],".resources","sample_render","data.json")) as f:
        data=json.load(f)
    click.echo(f"\"{jinja_env.get_template(path.relpath(template_filename,template_folder)).render(data)}\"")

if __name__=="__main__":
    sample_render()
