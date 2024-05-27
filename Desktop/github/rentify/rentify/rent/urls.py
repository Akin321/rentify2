from django.urls import path
from . import views
app_name='rent'
urlpatterns=[
    path('',views.home,name="home"),
    path('search/',views.SearchResult,name='searchresult'),
    path('property/<int:id>/',views.property,name="property"),
    path('filter/<str:place>/',views.filter,name="property_detail")
]