#!/usr/bin/env python
import asyncio
import dataclasses
import io
import os
import re
import sys

from tqdm.asyncio import tqdm

from PIL import Image
from dotenv import load_dotenv
from ebooklib import epub
from telethon import TelegramClient

load_dotenv()
sys.stdout.reconfigure(encoding='utf-8')

@dataclasses.dataclass
class Dialog(object):
    id: int
    name: str

def message_to_html(msg, image_path):
    html_body = ''
    if image_path:
        html_body += f'<img src="{image_path}"/>'

    if msg:
        msg = re.sub(r'\n', r'<br>', msg)
        html_body += msg

    return html_body

async def process_dialog(client, dialog, limit):
    book = epub.EpubBook()
    book.set_identifier(str(dialog.id))
    book.set_title(dialog.name)
    book.set_language('ru')

    msg_cnt = limit or sum([1 async for _ in client.iter_messages(dialog.id)])

    chapters = []
    msg_iter = client.iter_messages(dialog.id, reverse=False, limit=limit)
    async for msg in tqdm(msg_iter, total=msg_cnt):
        if not (msg.text or msg.photo):
            continue

        image_path = None
        if msg.photo and not msg.web_preview:
            image_bytes = await msg.download_media(file=bytes)
            await asyncio.sleep(0.1)
            pil_image = Image.open(io.BytesIO(image_bytes))

            compressed_image = io.BytesIO()
            pil_image.save(
                compressed_image,
                format='JPEG',
                quality=35,
                optimize=True,
                progressive=True,
            )

            image_path = f'images/msg_{msg.id}.jpeg'
            image = epub.EpubItem(
                uid=f'msg_{msg.id}',
                file_name=image_path,
                media_type='image/jpeg',
                content=compressed_image.getvalue(),
            )
            book.add_item(image)

        chapter = epub.EpubHtml(
            title=msg.date.strftime(r'%Y-%m-%d %H:%M:%S'),
            file_name=f'msg_{msg.id}.xhtml',
            lang='ru',
        )
        html = message_to_html(msg.text, image_path)
        chapter.set_content(html)

        book.add_item(chapter)
        chapters.append(chapter)

    book.toc = chapters

    book.add_item(epub.EpubNcx())
    book.add_item(epub.EpubNav())

    book.spine = ['nav'] + chapters

    os.makedirs('results', exist_ok=True)
    epub.write_epub(f'./results/{dialog.name}.epub', book)

async def main():
    api_id = os.getenv('API_ID')
    api_hash = os.getenv('API_HASH')
    client = TelegramClient('session', api_id, api_hash)
    client.parse_mode = 'html'

    async with client:
        dialogs = []
        async for d in client.iter_dialogs():
            if not d.is_channel:
                continue

            dialog = Dialog(id=d.entity.id, name=d.entity.title)
            dialogs.append(dialog)

        with open('indices.txt', 'r') as file:
            indices = [int(line.strip()) - 1 for line in file]
            assert all(0 <= idx < len(dialogs) for idx in indices), 'Dialog indices should be correct'

        limit = int(os.getenv('MESSAGE_LIMIT', 100))

        tasks = [process_dialog(client, dialogs[idx], limit) for idx in indices]
        await asyncio.gather(*tasks)

if __name__ == '__main__':
    asyncio.run(main())