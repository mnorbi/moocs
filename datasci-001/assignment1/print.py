import urllib
import json

response = urllib.urlopen("http://search.twitter.com/search.json?q=microsoft")
jsonResponse = json.load(response)
results = jsonResponse["results"]

for i in range(10):
	print(results[i]["text"])
