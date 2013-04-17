#!/usr/bin/env python

import json, urllib, urllib2, pprint

url = "http://aerowx.dccmn.com/get_weather"  # Default target

def issue_request(request):
	request_data = json.dumps(request)

	# Build an HTTP request
	#request_data = urllib.urlencode(request_data)
	req = urllib2.Request(url, request_data)
	response = urllib2.urlopen(req)
	response_data = response.read()

	return json.loads(response_data)

def test_1():
	# Build a standard METAR request
	print "# Build a standard METAR request"
	data = [{"source" : "metar", 
             "location" : "kros",
             "datetime" : ""}]
	pprint.pprint(issue_request(data))

def test_2():
	# Build a standard MAV request
	print "# Build a standard MAV request"
	data = [{"source" : "mav", 
             "location" : "kros",
             "datetime" : ""}]
	pprint.pprint(issue_request(data))


def test_error_handling():
	print """
For the following tests expect an error JSON response
This is testing the error handling, we should not see unhandled exceptions
	"""

	# ERROR Invalid Source
	print "# ERROR Invalid Source"

	data = [{"source" : "google", 
             "location" : "kros",
             "datetime" : ""}]

	print issue_request(data)

	# ERROR Invalid Source
	print "# ERROR Invalid Station"
	
	data = [{"source" : "metar", 
             "location" : "krol",
             "datetime" : ""}]

	print issue_request(data)

	# ERROR No Source
	print "# ERROR No Source"
	
	data = [{"source" : "", 
             "location" : "kros",
             "datetime" : ""}]

	print issue_request(data)
	
	# ERROR No location
	print "# ERROR No location"
	
	data = [{"source" : "metar", 
             "location" : "",
             "datetime" : ""}]

	print issue_request(data)

	# ERROR Missing Request Fields
	print "# ERROR Missing Request Fields"
	
	data = [{"location" : "",
             "datetime" : ""}]

	print issue_request(data)

# Run tests
#test_1()
test_2()
#test_error_handling()
