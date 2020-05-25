#!/usr/bin/env python3
from argparse import ArgumentParser
from daemonize import Daemonize
from random import choices
import string
from os import system, getcwd, fork, kill, _exit, execv
from time import sleep
from signal import signal, SIGINT, SIGKILL
import os
from subprocess import call


#global const's
PID = f"/tmp/{''.join(choices(list('_-'+string.ascii_lowercase+string.ascii_uppercase+string.digits),k=12))}.pid"
REFRESH_PERIOD_SECONDS = 30
#global var's
#procedures
class GithubChecker:
    def __init__(self):
        pass
    def __call__(self):
        print("should restart callback")
        return True
class Action:    
    def __init__(self,command,curdir):
        self.command = command
        self.curdir = curdir
        print(f"Action.init: {self.command} {self.curdir}")
    def __call__(self,**kwargs):
        print(f"command: {self.command}")
        system(f"cd {self.curdir} && {self.command}")
class OnSigterm:
    def __init__(self,daemon):
        self.daemon = daemon
    def __call__(self,*args,**kwargs):
        print("got SIGINT...")
        self.daemon.exit()
        exit(0)

#main
parser = ArgumentParser()
parser.add_argument("repo_url",help="remote repository's URL in https scheme")
parser.add_argument("branch",help="branch name")
parser.add_argument("command",help="command to execute on hash change")
args = parser.parse_args()
print(f"getcwd: {getcwd()}")
shouldRestartCallback = GithubChecker()

while True:
    pid = fork()
    if(pid==0):
        #retcode = call(args.command,shell=True)
        execv(args.command,[args.command])
        print(f"retcode: {retcode}")
        _exit(0)
    else:
        while True:
            sleep(REFRESH_PERIOD_SECONDS)
            if shouldRestartCallback():
                print(f"pid: {pid}")
                pgrp = os.getpgid(pid)
                print(f"pgrp: {pgrp}")
                mypid = os.getpid()
                mypgrp = os.getpgid(mypid)
                print((mypid,mypgrp))
                os.killpg(pgrp, SIGINT)
                #kill(pid, SIGKILL)
                break

#action = Action(args.command,getcwd())
#daemon = Daemonize(app="test_app", pid=PID, action=action)
#signal(SIGINT,OnSigterm(daemon))
#daemon.start()
#while True:
#    sleep(REFRESH_PERIOD_SECONDS)
#    if(shouldRestartCallback()):
#        daemon.exit()
#        daemon.start()
