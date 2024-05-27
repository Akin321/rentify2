from django import forms
from rent.models import Property


class PropertyForm(forms.ModelForm):
    class Meta:
        model=Property
        fields=['place','area','img','bedrooms','bathrooms','hospitals','colleges']