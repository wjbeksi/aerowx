#!/usr/bin/env python

import re
import sys,os
import json 
import string
import logging
import datetime, time
import urllib, urllib2
from sqlalchemy import *
from logging import debug
from parsers.metar import Metar
from parsers.mav import Mav
from HTMLParser import HTMLParser

# Enable debug prints 
logging.basicConfig(level=logging.DEBUG)

###############################################################################
# Cache global database config
###############################################################################
if os.path.exists('/var/www/aerowx/server/wsgi/cache.db'):
    # we need to use an absolute path for running in apache... not sure where the 
    # relative path begins... need to fix   
    db = create_engine('sqlite:////var/www/aerowx/server/wsgi/cache.db')
else:
    # Fall back on relative path
    debug("Absolute database path not found, using relative path")
    db = create_engine('sqlite:///cache.db')

db.echo = False # set to True for debugging
metadata = MetaData(db)
# Table Reference
# cache = Table('cache', metadata,
#               Column('id', Integer, primary_key=True),
#               Column('source', String(16)),
#               Column('location', String(16)),
#               Column('datetime', Integer),
#               Column('json', String)
# )
cache = Table('cache', metadata, autoload=True)

###############################################################################
# Utility function to truncate a datetime object to the nearest interval specified
###############################################################################
def roundTime(dt=None, roundTo=5):
    return (dt - datetime.timedelta(minutes=dt.minute % roundTo,
                                 seconds=dt.second,
                                 microseconds=dt.microsecond))

###############################################################################
# Caching Interface
###############################################################################
def CacheLookup(client_req):
    try:
        # determine time cutoff (current time rounded to 5 minute intervals)
        timestamp = roundTime(datetime.datetime.now(), 5).strftime("%s")

        s = cache.select((cache.c.source == client_req[0]['source'].lower()) & 
                         (cache.c.location == client_req[0]['location'].lower()) & 
                         (cache.c.datetime >= timestamp)).order_by(cache.c.id.desc())
        rs = s.execute()
        hit = rs.fetchone()

        if hit != None and hit['json'] != '':
            # Just return the most recent entry
            debug("Cache HIT: source=%s location=%s cached=%s" % (hit['source'], hit['location'], datetime.datetime.fromtimestamp(hit['datetime'])))
            return str(hit['json'])
        else:
            return None
    except:
        # return None on any failure
        return None

def CacheInsertion(client_req, json_data):
    timestamp = datetime.datetime.now().strftime("%s")
    i = cache.insert()
    i.execute(source=client_req[0]['source'].lower(), location=client_req[0]['location'].lower(), datetime=timestamp, json=json_data)

###############################################################################
# Validate all required fields of a client request, make sure we have a valid req
# client_req = [{
#    "source"   : "",  # Weather service to use
#    "location" : "",  # Weather station ID 
#    "datetime" : "",  # (optional) Date & time to get weather info for
# }]
###############################################################################
def ValidateClientRequest(client_req):
    try:
        source = client_req[0]['source'].lower() 
        location = client_req[0]['location'] 
        
        # validate a proper source and location is set
        if source in ['metar', 'mav'] and len(location) > 0:
            return True
    except:
        debug("No source in client request")

    # invalid client request
    return False

###############################################################################
# External Weather request handler 
# Determine which source should be queried and pass in a station id 
# A JSON object will be returned containing current weather info
# for the station ID.  
###############################################################################
def ExternalRequest(client_req):
    if client_req[0]['source'].lower() == 'metar':
        response = get_metar_report(client_req[0]['location'].upper())

    elif client_req[0]['source'].lower() == 'mav':
        response = get_mav_report(client_req[0]['location'].upper())

    # Cache returning data to prevent future web queries
    # Do not cache error responses
    if not 'error' in response:
        CacheInsertion(client_req, response)

    return response

###############################################################################
# Main client request handler
# Process a new client request, validate the contents of the request
# Look for a local cache hit for the request, if not in cache
# issue an extern request to update the current weather info.
###############################################################################
def ClientRequestHandler(client_req):
    response = []

    # Validate the client_req
    if (ValidateClientRequest(client_req)):
        # Look up request in the cache
        cacheHit = CacheLookup(client_req)
        if cacheHit != None:
            response = cacheHit
        else:
            # Pass on client_req to the correct handler if there's no cache hit
            response = ExternalRequest(client_req)
    else:
        response = json.dumps([{"error": "invalid request"}])

    return response

###############################################################################
# Weather report handlers 
###############################################################################
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
    base_url = "http://www.nws.noaa.gov/cgi-bin/mos/getmav.pl?sta="
    url = base_url + station_id
    parser = parse_mav_html(station_id)
    #try:
    req = urllib2.urlopen(url)
    a = parser.feed(req.read())
    data = parser.return_data()
    debug(data)
    obs = Mav.Mav(data)
    return obs.json()
    #return json.dumps([{"error": "MAV requests not implemented yet"}])
#    except:
 #       debug("MAV error")

###############################################################################
# HTML parser to get RAW MAV report data from webpage
###############################################################################
class parse_mav_html(HTMLParser):
    returnData = ""
    def __init__(self, station_id):
        self.station_id = station_id
        HTMLParser.__init__(self)
    def handle_data(self, data):
        regex = re.compile(r'\s+%s\s+GFS\s+MOS\s+GUIDANCE' % self.station_id)
        if regex.search(data):
            # Set data to be returned
            self.returnData = data
    def return_data(self):
        # return parsed data requested
        return self.returnData 

###############################################################################
# Application entry point
###############################################################################
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

###############################################################################
# If we are running directly from Python (i.e. local testing), then 
# execute a local httpd test server.  Otherwise, the Apache mod_wsgi 
# will import this module to handle requests.
###############################################################################
if __name__ == '__main__':
    from wsgiref.simple_server import make_server

    httpd = make_server('localhost', 8051, application)
    print "Listening on http://localhost:8051"
    while (1):
        httpd.handle_request()
