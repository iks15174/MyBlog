from django.urls import path
from . import views

urlpatterns = [
    path('message/', views.process_message, name='process_message'),
    # path('<keyword>/', views.index, name='index'),
]