from unicodedata import name
from django.views.decorators.http import require_http_methods
from konlpy.tag import Okt
from models import Words, WordsPosts
import nltk
import json
import re


# Create your views here.
okt = Okt()


@require_http_methods(["POST"])
def process_message(request):
    body = decode_body(request.body)
    op, post_id = body["message"].split(",")
    if op == "DELETE":
        pass
    elif op == "CREATE":
        save_content(int(post_id))
    elif op == "UPDATE":
        pass
    else:
        print("Invalid operation")


def get_back_db_connection():
    pass


def get_post_from_back_db(post_id):
    pass


def save_content(post_id):
    post = get_post_from_back_db(post_id)
    content = post["content"]
    ko_words = get_ko_noun(content)
    en_words = get_en_noun(content)
    words = save_word(ko_words + en_words)
    save_post_word(words, post_id)


def save_word(words):
    word_models = []
    for w in words:
        try:
            word = Words.objects.get(name=w)
            word_models.append(word)
        except Words.DoesNotExist:
            new_w = Words(name=w)
            new_w.save()
            word_models.append(new_w)
    return word_models


def save_post_word(post_id, words):
    for w in words:
        WordsPosts(word=w, post_id=post_id, appear_start=0, appear_end=0)
        WordsPosts.save()


def decode_body(body):
    body_unicode = body.decode("utf-8")
    body = json.loads(body_unicode)
    return body


def get_ko_noun(content):
    ko_content = re.sub("[a-zA-Z]", "", content)
    return set(okt.nouns(ko_content))


def get_en_noun(content):
    en_content = re.sub("[^a-zA-Z]", "", content)
    word_tokens = nltk.word_tokenize(en_content)
    tokens_pos = nltk.pos_tag(word_tokens)
    return [w for w, pos in tokens_pos if "NN" in pos]
