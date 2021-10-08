"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/py/util/google_spreadsheet.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-10-08T16:45:04.484605
    REVISION: ---

==============================================================================="""

from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
import string
import json
import os
import pandas as pd

_SCOPES = [
    'https://www.googleapis.com/auth/spreadsheets.readonly',
    "https://www.googleapis.com/auth/spreadsheets",
    "https://www.googleapis.com/auth/drive",
]


def get_creds(client_secret_file, token_file=".token.json", create_if_not_exist=False):
    """
    This subroutine reads the token file whose location is given by the required argument `token_file`. If `token_file` does not exists or does not contain
    the valid token file, the default behaviour is to panic. Otherwise (if `create_if_not_exist` key is set to `True`), subroutine attempts to create it, by using
    the client secret file, whose location should be given via the `client_secret_file` optional parameter.

    The code is adapted from https://developers.google.com/sheets/api/quickstart/python#step_2_configure_the_sample

    :return: credentials object
    """
    creds = None
    if os.path.exists(token_file):
        creds = Credentials.from_authorized_user_file(token_file, _SCOPES)
    if not creds or not creds.valid:
        if create_if_not_exist:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
            else:
                flow = InstalledAppFlow.from_client_secrets_file(
                    client_secret_file, _SCOPES)
                creds = flow.run_local_server(port=0)
            with open(token_file, 'w') as token:
                token.write(creds.to_json())
        else:
            raise Exception(
                f"provide valid credentials file: {token_file} given")

    return creds


def download_df_from_google_sheets(creds, spreadsheet_id, sheet_name=None):
    service = build('sheets', 'v4', credentials=creds)

    # Call the Sheets API
    sheet = service.spreadsheets()
    range_ = "A1:AZ"
    if sheet_name is not None:
        range_ = f"'{sheet_name}'!{range_}"
    result = sheet.values().get(spreadsheetId=spreadsheet_id,
                                range=range_).execute()
    values = result.get('values', [])

    header, body = values[0], values[1:]
    return pd.DataFrame([{k: v for k, v in zip(header, r)} for r in body])


def upload_df_to_google_sheets(creds, df, spreadsheet_title, folder_id=None, sheet_name=None):
    service = build('sheets', 'v4', credentials=creds)
    spreadsheet = {
        'properties': {
            'title': spreadsheet_title
        }
    }
    spreadsheet = service.spreadsheets().create(body=spreadsheet,
                                                fields='spreadsheetId').execute()
    spreadsheet_id = spreadsheet.get('spreadsheetId')

    if sheet_name is not None:
        _rename_sheet(service, spreadsheet_id, sheet_name)
    if folder_id is not None:
        _move_sheet_to_folder(creds, spreadsheet_id, folder_id)
    _write_into_table(service, spreadsheet_id, df, sheet_name)

    return spreadsheet_id


def _write_into_table(service, spreadsheet_id, df, sheet_name=None):
    assert len(list(df)) <= len(
        string.ascii_uppercase), f"too many columns: {len(list(df))}>{len(string.ascii_uppercase)}"
    range_ = f"A1:{string.ascii_uppercase[len(list(df))-1]}{len(df)+1}"
    if sheet_name is not None:
        range_ = f"'{sheet_name}'!{range_}"
    header = list(df)
    result = service.spreadsheets().values().update(
        spreadsheetId=spreadsheet_id,
        range=range_,
        valueInputOption="RAW",
        body={
            "values": [
                header,
                *json.loads(df.to_json(orient="values")),
            ]
        },
    ).execute()


def _move_sheet_to_folder(creds, spreadsheet_id, folder_id):
    file_id = spreadsheet_id
    # Retrieve the existing parents to remove
    drive_service = build('drive', 'v3', credentials=creds)
    file_ = drive_service.files().get(fileId=file_id,
                                      fields='parents').execute()
    previous_parents = ",".join(file_.get('parents'))
    # Move the file to the new folder
    file_ = drive_service.files().update(fileId=file_id,
                                         addParents=folder_id,
                                         removeParents=previous_parents,
                                         fields='id, parents').execute()


def _rename_sheet(service, spreadsheet_id, sheet_name, sheet_id="0"):
    data = {
        "requests": [{
            "updateSheetProperties": {
                "properties": {"title": sheet_name, "sheetId": sheet_id},
                "fields": "title"
            }
        }]
    }
    res = service.spreadsheets().batchUpdate(
        spreadsheetId=spreadsheet_id, body=data).execute()


def _add_sheet(service, spreadsheet_id, sheet_name):
    data = {'requests': [
        {
            'addSheet': {
                'properties': {'title': sheet_name}
            }
        }
    ]}
    res = service.spreadsheets().batchUpdate(
        spreadsheetId=spreadsheet_id, body=data).execute()


def spreadsheet_id_to_url(spreadsheet_id):
    spreadsheet_url = f"https://docs.google.com/spreadsheets/d/{spreadsheet_id}/edit#gid=0"
    return spreadsheet_url
