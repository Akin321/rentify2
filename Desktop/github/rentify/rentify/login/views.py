from django.conf import settings
from django.contrib import messages, auth
from django.contrib.auth import get_user_model, authenticate, login
from django.shortcuts import render, redirect


# Create your views here.
def seller(request):
    if request.method=='POST':
        username=request.POST.get('username')
        password=request.POST.get('Password')
        user=auth.authenticate(username=username,password=password)
        # if user is not None and not user.is_administrator:
        if user is not None and user.is_staff:
            auth.login(request,user)
            return redirect('/')
        else:
            messages.info(request,'invalid credntials')
            return redirect('login:seller')

    return render(request,'login.html',{'user_type':'Seller'})

def login_user(request):
    if request.method=='POST':
        username=request.POST.get('username')
        password=request.POST.get('Password')
        user=auth.authenticate(username=username,password=password)
        # if user is not None and not user.is_administrator:
        if user is not None and not user.is_staff:
            auth.login(request,user)
            return redirect('/')
        else:
            messages.info(request,'invalid credntials')
            return redirect('login:login')

    return render(request,'login.html',{'user_type':'buyer'})
def register(request):
    User = get_user_model()
    if request.method=='POST':
        username=request.POST.get('username')
        fname=request.POST.get('fname')
        lname=request.POST.get('lname')
        email=request.POST.get('email')
        password=request.POST.get('psw')
        cpassword=request.POST.get('psw-repeat')
        is_seller = request.POST.get('is_seller') == 'on'

        if password == cpassword:
            if User.objects.filter(username=username).exists():
                messages.info(request, 'username taken')
                return redirect('login:register')
            else:
                if is_seller:
                    user=User.objects.create_user(username=username,password=password,first_name=fname,last_name=lname,email=email,is_staff=True)
                else:
                    user=User.objects.create_user(username=username,password=password,first_name=fname,last_name=lname,email=email,is_staff=False)


                user.save()
                # Authenticate the user
                return redirect("login:login")
        else:
            messages.info(request,'password not matching')
            return redirect("login:register")

    else:
        return render(request,"registration.html")

def logout(request):
    auth.logout(request)

    return redirect(settings.LOGOUT_REDIRECT_URL)