#!/usr/bin/env python3
"""===============================================================================

        FILE: bin/get-latest-log.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-03-07T21:23:25.562944
    REVISION: ---

==============================================================================="""

import click
import os
from os import path
import re

@click.command()
@click.option("--logs-folder",type=click.Path(),default=".log")
def get_latest_log(logs_folder):
    fns = os.listdir(logs_folder)
#    fns = [fn for fn in fns if re.match(r"[a-zA-Z]+_\d{14}\.log\.txt",fn) is not None]
    fn = max(fns,key=lambda fn:path.getmtime(path.join(logs_folder,fn)))
    os.system(f"less {path.join(logs_folder,fn)}")

if __name__=="__main__":
    get_latest_log()
