from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.template import loader, Context, RequestContext
from django.template.defaultfilters import slugify
from django.shortcuts import render_to_response
from django import forms

def redirect(view, params):
    return HttpResponseRedirect(reverse(view, None, params))

def render(template, context, request):
    t = loader.get_template(template)
    c = RequestContext(request, context)
    return render_to_response(template, context, context_instance=RequestContext(request))

def serveFile(file, request):
    return HttpResponse(open(file).read())

def sort(list, field):
    list.sort(key=lambda obj:obj[field].lower())
    
def sortByName(list):
    sort(list, 'name')

def getFileFromPost(request, fileParam):
    if fileParam in request.FILES:  
        return request.FILES[fileParam]
    else:
        return None 
