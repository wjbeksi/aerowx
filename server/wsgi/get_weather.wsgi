#!/usr/bin/env python

import sys
import json 
import string
import urllib
import logging
from logging import debug
from parsers.metar import Metar

# Enable debug prints 
logging.basicConfig(level=logging.DEBUG)

###################################################################
# Caching Interface
###################################################################
def CacheLookup(client_req):
    return None

def CacheInsertion(json_data):
    pass


###################################################################
# Validate all required fields of a client request, make sure we have a valid req
# client_req = [{
#    "source"   : "",  # Weather service to use
#    "location" : "",  # Weather station ID 
#    "datetime" : "",  # (optional) Date & time to get weather info for
# }]
###################################################################
def ValidateClientRequest(client_req):
    try:
        source = client_req[0]['source'].lower() 
        location = client_req[0]['location'] 
        
        # validate a proper source and location is set
        if source in ['metar', 'gfs'] and len(location) > 0:
            return True
    except:
        debug("No source in client request")

    # invalid client request
    return False


###################################################################
# External Weather request handler 
# Determine which source should be queried and pass in a station id 
# A JSON object will be returned containing current weather info
# for the station ID.  
# TODO ## implement caching here
###################################################################
def ExternalRequest(client_req):
    if client_req[0]['source'].lower() == 'metar':
        response = get_metar_report(client_req[0]['location'].upper())

    elif client_req[0]['source'].lower() == 'mav':
        response = get_mav_report(client_req[0]['location'].upper())

    # Cache returning data to prevent future web queries
    CacheInsertion(response)

    return response

###################################################################
# Main client request handler
# Process a new client request, validate the contents of the request
# Look for a local cache hit for the request, if not in cache
# issue an extern request to update the current weather info.
###################################################################
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
        response = json.dumps([{"error": "invalid request"}])

    return response

###################################################################
# Weather report handlers 
###################################################################
def get_metar_report(station_id):
    base_url = "http://weather.noaa.gov/pub/data/observations/metar/stations"
    url = "%s/%s.TXT" % (base_url, station_id)
    try:
        urlh = urllib.urlopen(url)
        report = ''
        for line in urlh:
            if line.startswith(station_id):
                report = line.strip()
                obs = Metar.Metar(line)
                return obs.json()
        if not report:
            debug("No data for %s", station_id)
            return json.dumps([{"error": "invalid station id"}])
    except Metar.ParserError, err:
        debug("METAR code: %s", line)
        debug("%s", string.join(err.args, ", "))

def get_mav_report(station_id):
    # TODO implement MAV decoding
    pass

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
