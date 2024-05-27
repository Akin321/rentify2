from django.shortcuts import render, redirect, get_object_or_404
from rent.models import Property
from.forms import PropertyForm
# Create your views here.
def add(request):
    if request.method == 'POST':
        place = request.POST.get('place')
        area = request.POST.get('area')
        bedrooms = request.POST.get('bedrooms')
        bathrooms = request.POST.get('bathrooms')
        hospitals = request.POST.get('hospitals')
        img = request.FILES.get('img')
        colleges=request.POST.get('colleges')
        user = request.user
        property = Property(place=place, area=area, bedrooms=bedrooms, bathrooms=bathrooms, hospitals=hospitals, img=img, colleges=colleges,added_by=user)
        property.save()
        return redirect('rent:home')
    return render(request, 'add.html')

def update(request,id):
    property=Property.objects.get(id=id)
    form = PropertyForm(request.POST,request.FILES, instance=property)
    if request.method == 'POST':
            if form.is_valid():
                form.save()
                return redirect('rent:home')
    return render(request,'update.html',dict(form=form))

def delete(request,id):
    property = get_object_or_404(Property, id=id)
    property.delete()
    return redirect('/')


