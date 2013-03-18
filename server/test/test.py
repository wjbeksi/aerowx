#!/usr/bin/env python

import json, urllib, urllib2

url = "http://127.0.0.1:8051"  # Default target

def test_1():
	# Build a standard request
	data = [{"source" : "metar", 
             "location" : "kros",
             "datetime" : ""}]

	request_data = json.dumps(data)

	# Build an HTTP request
	#request_data = urllib.urlencode(request_data)
	req = urllib2.Request(url, request_data)
	response = urllib2.urlopen(req)
	response_data = response.read()

	print response_data

# Run test
test_1()
