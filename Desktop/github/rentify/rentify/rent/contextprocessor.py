from.models import Property
def menu_links(request):
    # property=Property.objects.filter(added_by=request.user)
    links = Property.objects.values_list('place', flat=True).distinct()
    return dict(links=links)