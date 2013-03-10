import json, urllib, urllib2

url = "http://127.0.0.1:8051" #default target


def test1():
	# build standard request
	data = [{"test":"123"}]

	request_data = json.dumps(data)

	# build http request
	#request_data = urllib.urlencode(request_data)
	req = urllib2.Request(url, request_data)
	response = urllib2.urlopen(req)
	response_data = response.read()

	print response_data


#run test
test1()