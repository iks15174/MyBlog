from pyexpat import model
from django.db import models

# Create your models here.
class Posts(models.Model):
    post_id = models.BigIntegerField()
    content = models.TextField(null=False)
    contentType = models.CharField(max_length=100, default="text")

class Words(models.Model):
    word = models.CharField(max_length=200, null=False, unique=True)
    

class WordsPosts(models.Model):
    post = models.ForeignKey(Posts, on_delete=models.CASCADE)
    word = models.ForeignKey(Words, on_delete=models.CASCADE)
    appear_start = models.IntegerField(null=False)
    appear_end = models.IntegerField(null=False)
    
    