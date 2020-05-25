#!/usr/bin/env python3
from argparse import ArgumentParser
from daemonize import Daemonize
from random import choices
import string
from os import system, getcwd
from time import sleep


#global const's
PID = f"/tmp/{''.join(choices(list('_-'+string.ascii_lowercase+string.ascii_uppercase+string.digits),k=12))}.pid"
REFRESH_PERIOD_SECONDS = 2
#global var's
#procedures
class GithubChecker:
    def __init__(self):
        pass
    def __call__(self):
        pass
class Action:    
    def __init__(self,command,curdir):
        self.command = command
        self.curdir = curdir
        print(f"Action.init: {self.command} {self.curdir}")
    def __call__(self,**kwargs):
        #chdir(self.curdir)
        print(f"command: {self.command}")
        system(f"cd {self.curdir} && {self.command}")

#main
parser = ArgumentParser()
parser.add_argument("repo_url",help="remote repository's URL in https scheme")
parser.add_argument("branch",help="branch name")
parser.add_argument("command",help="command to execute on hash change")
args = parser.parse_args()
print(f"getcwd: {getcwd()}")
action = Action(args.command,getcwd())
daemon = Daemonize(app="test_app", pid=PID, action=action,foreground=True)
daemon.start()
callback = GithubChecker()
while True:
    sleep(REFRESH_PERIOD_SECONDS)
    if(callback()):
        print("restart callback")
        daemon.exit()
        daemon.start()
