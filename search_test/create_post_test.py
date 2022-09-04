from konlpy.corpus import kolaw
from auth import Auth
import random
import requests
import json

SETENCE_MIN_LEN = 30
TTTLE_LEN = 10
BASE_URL = "http://localhost:8080/api"


def get_random_file():
    fids = kolaw.fileids()
    return random.choice(fids)


def get_random_setence(fid):
    fobj = kolaw.open(fid)
    strings = fobj.read()
    last_idx = -1 if len(strings) < 30 else random.randint(30, len(strings))
    return strings[:last_idx]


def get_random_title(fid):
    fobj = kolaw.open(fid)
    strings = fobj.read()
    last_idx = min(len(strings), TTTLE_LEN)
    return strings[:last_idx]


def request_create_post(token):
    fid = get_random_file()
    data = {
        "title": get_random_title(fid),
        "content": get_random_setence(fid),
        "contentType": "text",
        "categoryId": 2,
        "tagDto": [],
    }
    res = requests.post(
        BASE_URL + "/v1/posts",
        data=json.dumps(data),
        headers={"Content-Type": "application/json; charset=utf-8", "Authorization": "Bearer " + token},
    )
    if res.status_code == 200:
        print("Create post success")


def main():
    user = Auth()
    user.create_user()
    user.login()
    n = input("생성할 post 갯수를 입력하세요 : ")
    for _ in range(int(n)):
        request_create_post(user.token)


main()
