from pyexpat import model
from django.db import models

# Create your models here.
class Words(models.Model):
    word = models.CharField(max_length=200, null=False, unique=True)
    

class WordsPosts(models.Model):
    word = models.ForeignKey(Words, on_delete=models.CASCADE)
    post_id = models.BigIntegerField(unique=True)
    appear_start = models.IntegerField(null=False)
    appear_end = models.IntegerField(null=False)
    
    