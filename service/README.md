JSONP request
curl -i -X GET -HAccept:application/json 'http://localhost:9080/api/tweets/latest?since_id=7780&count=1&callback=mycb'
JSON request
curl -i -X GET -HAccept:application/json 'http://localhost:9080/api/tweets/latest?since_id=7780&count=1&callback=mycb'