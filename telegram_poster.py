#!/usr/bin/env python
from telethon import TelegramClient
import asyncio
import sys
import os
import dataclasses
import json
from telethon import TelegramClient
from dotenv import load_dotenv
from datetime import datetime, timedelta

load_dotenv()


API_ID = os.getenv('API_ID')
API_HASH = os.getenv('API_HASH')
BOT_TOKEN = os.getenv('BOT_TOKEN')  # Замените на токен вашего бота

async def send_message(text):
    async with TelegramClient('bot_session', API_ID, API_HASH) as client:
        await client.start(bot_token=BOT_TOKEN)
        await client.send_message(CHANNEL_USERNAME, text)



if __name__ == "__main__":
    text = sys.argv[1]
    CHANNEL_USERNAME = sys.argv[2]
    asyncio.run(send_message(text))
