#!/usr/bin/env python
from telethon import TelegramClient
import asyncio
import sys
import os
import dataclasses
import json
from telethon import TelegramClient
from dotenv import load_dotenv

load_dotenv()


API_ID = os.getenv('API_ID')
API_HASH = os.getenv('API_HASH')
BOT_TOKEN = os.getenv('BOT_TOKEN')  # Замените на токен вашего бота
CHANNEL_USERNAME = os.getenv('CHANNEL_USERNAME') # Замените на username вашего канала

async def send_message(text):
    async with TelegramClient('bot_session', API_ID, API_HASH) as client:
        await client.start(bot_token=BOT_TOKEN)
        await client.send_message(CHANNEL_USERNAME, text)

if __name__ == "__main__":
    text = sys.argv[1]
#     text ="Это сообщение отправлено моей супер пупер секретной программой"
    asyncio.run(send_message(text))
