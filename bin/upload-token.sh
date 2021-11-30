#!/bin/sh
scp -i ~/.ssh/kimgym.pem .token.json ec2-user@ec2-3-143-68-209.us-east-2.compute.amazonaws.com:/home/ec2-user/eldrinko-master
