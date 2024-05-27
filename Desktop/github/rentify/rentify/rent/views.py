from django.core.paginator import Paginator, EmptyPage, InvalidPage
from django.db.models import Q
from django.http import HttpResponse
from django.shortcuts import render
from.models import Property
# Create your views here.
def home(request):
    properties=None
    user=request.user
    if user.is_authenticated:
        if user.is_staff:
            properties = Property.objects.filter(added_by=user.id)

            paginator = Paginator(properties, 3)
            try:
                page = int(request.GET.get('page', '1'))
            except:
                page = 1
            try:
                properties = paginator.page(page)
            except (EmptyPage, InvalidPage):
                properties = paginator.page(paginator.num_pages)
        else:
            properties = Property.objects.all()
            paginator = Paginator(properties, 3)
            try:
                page = int(request.GET.get('page', '1'))
            except:
                page = 1
            try:
                properties = paginator.page(page)
            except (EmptyPage, InvalidPage):
                properties = paginator.page(paginator.num_pages)


    return render(request, 'home.html', {'properties': properties})
def filter(request,place):
    user=request.user
    properties = Property.objects.filter(place=place)
    context = {'properties': properties}
    return render(request, 'filter.html', context)

def SearchResult(request):
    property=None
    query=None
    if 'q' in request.GET:
        query=request.GET.get('q')
        property=Property.objects.all().filter(Q(place__contains=query))
    return render(request,'filter.html',{'query':query,'properties':property})

def property(request,id):
    try:
        property = Property.objects.get(id=id)
    except Exception as e:
        raise e
    return render(request, 'property.html', {'property': property})


