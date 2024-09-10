#!/usr/bin/env python
import asyncio
import dataclasses
import os
import json
from telethon import TelegramClient
from dotenv import load_dotenv

load_dotenv()

@dataclasses.dataclass
class Dialog(object):
    id: int
    name: str
    index: int


async def main():
    api_id = os.getenv('API_ID')
    api_hash = os.getenv('API_HASH')
    client = TelegramClient('session', api_id, api_hash)
    client.parse_mode = 'html'

    async with client:
        dialogs = []
        index = 1
        async for  d in client.iter_dialogs():
            if not d.is_channel:
                continue

            dialog = Dialog(id=d.entity.id, name=d.entity.title, index = index)
            dialogs.append(dialog)
            index += 1
# Запись списка каналов в файл
    with open('dialogs.json', 'w', encoding='utf-8') as f:
        json.dump([dataclasses.asdict(d) for d in dialogs], f, ensure_ascii=False, indent=4)



if __name__ == '__main__':
    asyncio.run(main())