from django.contrib.auth import get_user_model
from django.db import models

# Create your models here.
class Property(models.Model):
    place=models.CharField(max_length=200)
    area=models.CharField(max_length=200)
    img=models.ImageField(upload_to='image')
    bedrooms=models.IntegerField()
    bathrooms=models.IntegerField()
    hospitals=models.IntegerField()
    colleges=models.IntegerField()
    added_by = models.ForeignKey(get_user_model(), on_delete=models.CASCADE)


    def __str__(self):
        return self.place




