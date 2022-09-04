import requests
import json


class Auth:

    BASE_URL = "http://localhost:8080/api"
    default_id = "test@test.com"
    default_passwrd = "1234565"
    default_name = "test"

    def __init__(self):
        self.email = ""
        self.password = self.default_passwrd
        self.token = ""

    def create_user(self):
        num = 1
        while True:
            data = {
                "email": self.default_id + str(num),
                "password": self.default_name,
                "name": self.default_name + str(num),
                "social": "NoSocial",
            }
            res = requests.post(
                self.BASE_URL + "/v1/auth/signup",
                data=json.dumps(data),
                headers={"Content-Type": "application/json; charset=utf-8"},
            )
            if int(res.status_code) == 200:
                print("회원가입 성공")
                break
            num += 1
        self.email = data["email"]
        self.password = data["password"]
        return data

    def login(self):
        data = {"email": self.email, "password": self.password}
        res = requests.post(
            self.BASE_URL + "/v1/auth/login",
            data=json.dumps(data),
            headers={"Content-Type": "application/json; charset=utf-8"},
        )
        content = res.json()
        if int(res.status_code) == 200:
            print("로그인 성공")
        self.token = content["accessToken"]
