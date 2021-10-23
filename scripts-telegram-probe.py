#!/usr/bin/env python3
"""===============================================================================

        FILE: ./scripts-telegrame-probe.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-10-23T11:41:49.526621
    REVISION: ---

==============================================================================="""

import click
import sqlite3
import logging
from telegram import InlineKeyboardButton, InlineKeyboardMarkup, KeyboardButton, ReplyKeyboardMarkup
from telegram.ext import Updater, CommandHandler, CallbackQueryHandler, MessageHandler, Filters
import inspect
import types
from typing import cast
from datetime import datetime
import pandas as pd


class _TelegramCallback:
    def __init__(self, sqlite3_db_path, telegram_token):
        self._sqlite3_db_path = sqlite3_db_path
        self._telegram_token = telegram_token

        conn, cur = self._get_conn_cursor()
        cur.execute("""
        CREATE TABLE IF NOT EXISTS telegram_probe(telegram_token TEXT, datetime_iso TEXT, message_text TEXT, username TEXT, chat_id INT)
        """)
        conn.commit()
        conn.close()

    def _get_conn_cursor(self):
        conn = sqlite3.connect(self._sqlite3_db_path)
        cur = conn.cursor()
        return conn, cur

    def __call__(self, update, context):
        # taken from https://stackoverflow.com/a/13514318
        this_function_name = cast(
            types.FrameType, inspect.currentframe()).f_code.co_name
        logger = logging.getLogger(__name__).getChild(this_function_name)
        chat_id = update.effective_message.chat_id
        message_text = update.message.text
        effective_user = update.effective_user
        username = effective_user.username
        first_name = effective_user.first_name
        last_name = effective_user.last_name

        logger.info(f"message \"{message_text}\" from {username}({chat_id})")

        conn, cur = self._get_conn_cursor()
        cur.execute(
            "INSERT INTO telegram_probe VALUES (?,?,?,?,?)",
            (self._telegram_token, datetime.now().isoformat(),
             message_text, username, chat_id),
        )
        conn.commit()
        conn.close()


@click.group()
@click.option("-t", "--telegram-token", required=True)
@click.option("--sqlite3-db-path", default=".telegram_probe.sqlite3")
@click.pass_context
def scripts_telegram_probe(ctx, **kwargs):
    ctx.ensure_object(dict)
    ctx.obj["kwargs"] = kwargs


@scripts_telegram_probe.command()
@click.pass_context
def probe(ctx):
    telegram_token, sqlite3_db_path = [ctx.obj["kwargs"][k]
                                       for k in "telegram_token,sqlite3_db_path".split(",")]
    logging.basicConfig(level=logging.INFO)
    updater = Updater(telegram_token, use_context=True)
    bot = updater.bot
    updater.dispatcher.add_handler(
        MessageHandler(
            filters=Filters.all,
            callback=_TelegramCallback(sqlite3_db_path, telegram_token)
        )
    )
    updater.start_polling()
    updater.idle()


@scripts_telegram_probe.command()
@click.pass_context
def show(ctx):
    telegram_token, sqlite3_db_path = [ctx.obj["kwargs"][k]
                                       for k in "telegram_token,sqlite3_db_path".split(",")]
    conn = sqlite3.connect(sqlite3_db_path)
    df = pd.read_sql_query("select * from telegram_probe", conn)
    conn.close()

    df = df.query(f"telegram_token==\"{telegram_token}\"")
    df["datetime"] = df.pop("datetime_iso").apply(datetime.fromisoformat)

    click.echo(df)


if __name__ == "__main__":
    scripts_telegram_probe()
