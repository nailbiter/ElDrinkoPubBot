#!/usr/bin/env python3
"""===============================================================================

        FILE: bin/strip-comments.py

       USAGE: ./bin/strip-comments.py

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-03-04T21:31:17.103044
    REVISION: ---

==============================================================================="""

import click
import re
import os

from git import Repo


def _get_head_sha():
    _path = "."
    # FIXME: this probably can be done better
    while True:
        try:
            repo = Repo(_path)
            break
        except Exception:
            _path = path.join(_path, "..")
    assert not repo.bare
    head_commit = repo.head.commit
    assert(not head_commit.diff(None)
           ), "should be no changes on tree (do `git commit -a`)"
    return head_commit.hexsha


@click.command()
@click.argument("fn", type=click.Path())
@click.option("--git-commit/--no-git-commit", default=True)
@click.option("--prettify/--no-prettify", default=True)
def strip_comments(fn, git_commit, prettify):
    if git_commit:
        _get_head_sha()
    with open(fn) as f:
        body = f.read()
    body = body.split("\n")
    body = [line for line in body if re.match(r"^ *#.*$", line) is None]
    body = "\n".join(body)
    while "\n\n" in body:
        body = body.replace("\n\n", "\n")
    with open(fn, "w") as f:
        f.write(body)
    if prettify:
        os.system(f"autopep8 -i \"{fn}\"")
    if git_commit:
        os.system(f"git commit -a -m \"strip_comments {fn}\"")


if __name__ == "__main__":
    strip_comments()
