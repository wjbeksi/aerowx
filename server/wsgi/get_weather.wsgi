#!/usr/bin/env python

import urllib
import json, logging
from logging import debug
from parsers.metar import Metar

# TODO: this needs to go into a metar function
BASE_URL = "http://weather.noaa.gov/pub/data/observations/metar/stations"

# Enable debug prints 
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
    # TODO: this needs to go into a metar function
    name = 'KROS'  # Hard coded for testing...
    url = "%s/%s.TXT" % (BASE_URL, name)
    try:
        urlh = urllib.urlopen(url)
        report = ''
        for line in urlh:
            if line.startswith(name):
                report = line.strip()
                obs = Metar.Metar(line)
                return obs.json()
        if not report:
            print "No data for ", name, "\n\n"
    except Metar.ParserError, err:
        print "METAR code: ", line
        print string.join(err.args,", "),"\n"

# Validate all required fields of a client request
# client_req = [{
#    "source"   : "",  # Weather service to use
#    "location" : "",  # Weather station ID 
#    "datetime" : "",  # Date & time to get weather info for
# }]
def ValidateClientRequest(client_req):
    return True

def ClientRequestHandler(client_req):
    response = []

    # Validate the client_req
    if (ValidateClientRequest(client_req)):
        # Look up request in the cache
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
# Application entry point
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
    #response_body = json.dumps(response)
    response_body = response
    status = '200 OK'
    response_headers = [('Content-type', 'application/json'),
                        ('Content-Length', str(len(response_body)))]
    start_response(status, response_headers)

    return [response_body]

###################################################################
# If we are running directly from Python (i.e. local testing), then 
# execute a local httpd test server.  Otherwise, the Apache mod_wsgi 
# will import this module to handle requests.
###################################################################
if __name__ == '__main__':
    from wsgiref.simple_server import make_server

    httpd = make_server('localhost', 8051, application)
    print "Listening on http://localhost:8051"
    while (1):
        httpd.handle_request()
