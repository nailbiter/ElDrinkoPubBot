#!/usr/bin/env python3
"""===============================================================================

        FILE: ./bin/java2python.py

       USAGE: ././bin/java2python.py

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-01-31T13:03:07.201082
    REVISION: ---

==============================================================================="""

import click
import os
import logging
from os import path

_INPUT_SOURCE_TREE = "src/main/java"

def _add_logger(f):
    logger = logging.getLogger(f.__name__)

    def _f(*args, **kwargs):
        return f(*args, logger=logger, **kwargs)
    _f.__name__ = f.__name__
    return _f

@_add_logger
def _system(cmd,logger=None):
    logger.info(f"> {cmd}")
    return os.system(cmd)

@click.command()
@click.option("--j2py-exe",default="~/Downloads/java2python-0.5.1/bin/j2py")
@click.option("--output-source-tree",type=click.Path(),default="src/main/python")
@_add_logger
def java2python(j2py_exe,output_source_tree,logger=None):
    if True:
        logging.basicConfig(level=logging.INFO)
    _system(f"rm -rf {output_source_tree}")
    os.makedirs(output_source_tree,exist_ok=True)
    for root, dirs, files in os.walk(_INPUT_SOURCE_TREE, topdown=False):
        for name in files:
            if not name.endswith(".java"):
                continue

            name = path.join(root,name)
            name = path.relpath(name,_INPUT_SOURCE_TREE)
            dir_,_ = path.split(name)
            os.makedirs(path.join(output_source_tree,dir_),exist_ok=True)

            dest_name = path.join(output_source_tree,name)
            dest_name = path.splitext(dest_name)[0]+".py"

            _system(f"{j2py_exe} {os.path.join(_INPUT_SOURCE_TREE,name)} {dest_name}")
#        for name in dirs:
#            print(os.path.join(root, name))

if __name__=="__main__":
    java2python()
