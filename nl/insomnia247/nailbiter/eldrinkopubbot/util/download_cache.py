"""===============================================================================

        FILE: /home/pi/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/util/download_cache.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-02T22:47:42.400147
    REVISION: ---

==============================================================================="""
import uuid
import os
import sqlite3
import pandas as pd
from os import path


class DownloadCache:
    _DOWNLOAD_CACHE_TABLE_NAME = "download_cache"

    def __init__(self, extension, db_filename="download_cache.db", tmp_folder="/tmp"):
        self._extension = extension
        self._db_filename = db_filename
        self._tmp_folder = tmp_folder

    def __call__(self, url):
        conn = sqlite3.connect(self._db_filename)
        try:
            self._cache_df = pd.read_sql(
                f"select * from {DownloadCache._DOWNLOAD_CACHE_TABLE_NAME}", conn)
        except pd.io.sql.DatabaseError:
            self._cache_df = pd.DataFrame({"filename": [], "url": []})
        slice_ = self._cache_df[[url == url_ for url_ in self._cache_df.url]]
        res = None
        if len(slice_) > 0:
            res = list(slice_["filename"])[0]
        else:
            fn = path.join(self._tmp_folder,
                           f"{uuid.uuid4()}{self._extension}")
            os.system(f"wget -O {fn} \"{url}\"")
            pd.DataFrame({"filename": [fn], "url": [url]}).to_sql(
                DownloadCache._DOWNLOAD_CACHE_TABLE_NAME, conn, if_exists="append", index=None)
            res = fn
        conn.close()
        return res
