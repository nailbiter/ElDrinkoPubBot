#!/usr/bin/env python3
"""===============================================================================

        FILE: scripts-get-beerlist.py

       USAGE: ./scripts-get-beerlist.py

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-11-02T06:11:14.881348
    REVISION: ---

==============================================================================="""

import click
from py.util import google_spreadsheet
import os
from jinja2 import Template

_SCP_TEMPLATE = Template(
    """scp -P {{port}} .token.json 0.tcp.ap.ngrok.io:~/Dokumente/forgithub/ElDrinkoPubBot/""")


@click.command()
@click.option("--port", "-p", required=True, type=int)
def scripts_get_beerlist(port):
    creds = google_spreadsheet.get_creds(
        client_secret_file="client_secret.json", create_if_not_exist=True)

    os.system(_SCP_TEMPLATE.render({"port": port}))
#    os.system("scp -i ~/.ssh/kimgym.pem .token.json ec2-user@ec2-3-143-68-209.us-east-2.compute.amazonaws.com:~/eldrinko-staging")


if __name__ == "__main__":
    scripts_get_beerlist()
