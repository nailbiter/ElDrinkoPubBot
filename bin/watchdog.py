#!/usr/bin/env python3
from argparse import ArgumentParser
from random import choices
import string
from os import system, getcwd, fork, kill, _exit, execv
from time import sleep
from signal import signal, SIGINT, SIGKILL
import os
from subprocess import call
import sys
from urllib import request
import json
import re
from datetime import datetime
from requests import get
from pymongo import MongoClient
from pprint import pprint


# global const's
REFRESH_PERIOD_SECONDS = 60
# global var's
Child_pid = 0
# procedures


class GithubChecker:
    @staticmethod
    def __Get_sha(login, repo, branch, github_pass):
        url = f"https://api.github.com/repos/{login}/{repo}/branches/{branch}"
        r = get(url, auth=('nailbiter', github_pass))
        data = json.loads(r.text)
        return data["commit"]["sha"]
        print(f"url: {url}")

    def __init__(self, repo_url, branch):
        m = re.match("https://github.com/([a-zA-Z]+)/([a-zA-Z]+)", repo_url)
        assert(m is not None)

        self.login = m.group(1)
        self.repo = m.group(2)
        self.branch = branch

        secret = ""
        with open("secret.txt") as f:
            secret = f.read().strip()
        mongo_uri = f"mongodb+srv://nailbiter:{secret}@cluster0-ta3pc.gcp.mongodb.net/beerbot?retryWrites=true&w=majority"
        mongo_client = MongoClient(mongo_uri)
        doc = mongo_client["beerbot"]["_passwords"].find_one(
            {"key": "github_token"})
        self.github_pass = doc["value"]

        self.sha = GithubChecker.__Get_sha(
            self.login, self.repo, self.branch, self.github_pass)

    def __call__(self):
        print(f"should restart callback {datetime.now().isoformat()}")
        new_sha = GithubChecker.__Get_sha(
            self.login, self.repo, self.branch, self.github_pass)
        if(self.sha != new_sha):
            print(f"{self.sha}!={new_sha} => reload")
            self.sha = new_sha
            return True
        else:
            print(f"{self.sha}=={new_sha} => no reload")
            return False


def kill_child(p):
    print(f"pid: {p}")
    pgrp = os.getpgid(p)
    print(f"pgrp: {pgrp}")
    os.killpg(pgrp, SIGKILL)


def signal_handler(sig, frame):
    print('You pressed Ctrl+C!')
    kill_child(Child_pid)
    sys.exit(0)


# main
parser = ArgumentParser()
parser.add_argument("repo_url", help="remote repository's URL in https scheme")
parser.add_argument("branch", help="branch name")
parser.add_argument("command", help="command to execute on hash change")
args = parser.parse_args()
print(f"getcwd: {getcwd()}")
shouldRestartCallback = GithubChecker(args.repo_url, args.branch)
signal(SIGINT, signal_handler)

while True:
    Child_pid = fork()
    if(Child_pid == 0):
        os.setpgid(0, 0)
        system(args.command)
        _exit(0)
    else:
        while True:
            sleep(REFRESH_PERIOD_SECONDS)
            if shouldRestartCallback():
                kill_child(Child_pid)
                break
