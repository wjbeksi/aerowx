#!/usr/bin/env python

import json

def application(environ, start_response):
    try:
        request_body_size = int(environ.get('CONTENT_LENGTH', 0))
    except ValueError:
         request_body_size = 0

    request_body = environ['wsgi.input'].read(request_body_size)
    #data = json.loads(request_body)

    status = '200 OK'
    output = 'Hello World!'

    response_headers = [('Content-type', 'text/plain'),
                        ('Content-Length', str(len(output)))]
    start_response(status, response_headers)

    return [output]
