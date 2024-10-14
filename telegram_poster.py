#!/usr/bin/env python
from telethon import TelegramClient
import asyncio
import sys
import os
from dotenv import load_dotenv

load_dotenv()

API_ID = os.getenv('API_ID')
API_HASH = os.getenv('API_HASH')
BOT_TOKEN = os.getenv('BOT_TOKEN')

async def send_message(text, channel_username, image_paths):
    async with TelegramClient('bot_session', API_ID, API_HASH) as client:
        await client.start(bot_token=BOT_TOKEN)
        if image_paths:
            for image_path in image_paths:
                await client.send_file(channel_username, image_path, caption=text)
        else:
            await client.send_message(channel_username, text)

if __name__ == "__main__":
    text = sys.argv[1]
    channel_username = sys.argv[2]
    image_paths = sys.argv[3].strip('[]').split(',') if len(sys.argv) > 3 else []
    image_paths = [path.strip().strip('"') for path in image_paths]
    asyncio.run(send_message(text, channel_username, image_paths))
