#!/usr/bin/env python

import json, logging
from logging import debug

# enable debug prints 
logging.basicConfig(level=logging.DEBUG)

###################################################################
# TODO ### define Supporting API
###################################################################
def CacheLookup(client_req):
    return None


def CacheInsertion():
    pass

def ResponseParser():
    pass

# TODO: break out into explicit parameters?
def ExternalRequest(client_req):
    pass


# Validate all required fields of a client request, make sure we have a valid req
# client_req = [{
#    "source"   : "",  # Weather service to use
#    "location" : "",  # Weather station ID 
#    "datetime" : "",  # Date & time to get weather info for
# }]
def ValidateClientRequest(client_req):
    return True

def ClientRequestHandler(client_req):
    response = []

    # validate client_req
    if(ValidateClientRequest(client_req)):
        # look up request in cache
        cacheHit = CacheLookup(client_req)
        if cacheHit != None:
            response = cacheHit
        else:
            # pass on client_req to correct handler if no cache hit
            response = ExternalRequest(client_req)
    else:
        response = [{"error": "invalid request"}]

    return response

###################################################################
# Application Entry Point
###################################################################
def application(environ, start_response):
    client_req = [{"error": "invalid request"}]
    try:
        request_body_size = int(environ.get('CONTENT_LENGTH', 0))
    except ValueError:
         request_body_size = 0

    if request_body_size > 0:
        # Parse input JSON
        try:
            request_body = environ['wsgi.input'].read(request_body_size)
            
            client_req = json.loads(request_body)
            debug(client_req)
        except:
            debug("Error parsing JSON input")
    else:
        debug("No JSON input")

    # Request weather data update (from cache, then web)
    response = ClientRequestHandler(client_req)

    # Construct JSON response
    response_body = json.dumps(response)
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