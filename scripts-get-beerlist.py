#!/usr/bin/env python3
"""===============================================================================

        FILE: scripts/get-beerlist.py

       USAGE: ./scripts/get-beerlist.py

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-10-08T17:44:07.536424
    REVISION: ---

==============================================================================="""
from py.util import google_spreadsheet

creds = google_spreadsheet.get_creds(
    client_secret_file="client_secret.json", create_if_not_exist=True)
