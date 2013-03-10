#!/usr/bin/env python

import json

###################################################################
# TODO ### define Supporting API
###################################################################
def CacheLookup():
    pass


def CacheInsertion():
    pass


def ExternalRequest():
    pass


def ResponseParser():
    pass



###################################################################
# Application Entry Point
###################################################################
def application(environ, start_response):
    try:
        request_body_size = int(environ.get('CONTENT_LENGTH', 0))
    except ValueError:
         request_body_size = 0

    if request_body_size > 0:
        # Parse input JSON
        try:
            request_body = environ['wsgi.input'].read(request_body_size)
            
            data = json.loads(request_body)
            print data
        except:
            print "Error parsing JSON input"
    else:
        print "No JSON input"

    # Request weather data update (from cache, then web)
    output = [{"Text": "Hello World!"}]



    # Construct JSON response
    response_body = json.dumps(output)
    status = '200 OK'
    response_headers = [('Content-type', 'application/json'),
                        ('Content-Length', str(len(response_body)))]
    start_response(status, response_headers)

    return [response_body]


###################################################################
# if we are running directly from python (i.e. local testing), 
# then execute a local httpd test server.
# Otherwise the apache mod_wsgi will import this module to
# handle requests.
###################################################################
if __name__ == '__main__':
    from wsgiref.simple_server import make_server

    httpd = make_server('localhost',8051,application)
    print "Listening on http://localhost:8051"
    while(1):
        httpd.handle_request()