from django.urls import path

from.import views
app_name='login'
urlpatterns=[
    path('registration/',views.register,name='register'),
    path('logout/',views.logout,name='logout'),
    path('seller/',views.seller,name='seller'),
    path('',views.login_user,name='login')
]