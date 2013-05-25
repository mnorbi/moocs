import sys
import json
import re
import codecs

def loadDictionary(aFile):
	d = {}
	pattern = re.compile("^(.+)\s+(-?\d+)$")
	for line in aFile:
		match = pattern.match(line)
		key = match.group(1).strip().lower()
		d[key] = int(match.group(2))
	return d

def sentimentScore(text, dictionary):
	score = 0
	for word in dictionary:
		if (word in text):
			score += dictionary[word]
	return score

def getUSState(jsonTweet):
	state = None
	place = jsonTweet.get("place", None)
	if place != None and place.get("country_code", "") == "US":
		state = place["full_name"][-2:].upper()
	return state
	

def main():
	sent_file = codecs.open(sys.argv[1], encoding='utf-8')
	tweet_file = codecs.open(sys.argv[2], encoding='utf-8')
	sentimentDict = loadDictionary(sent_file)
	usStateScore = {}
	for tweet in tweet_file:
		jsonTweet = json.loads(tweet)
		usState = getUSState(jsonTweet)
		if usState:
			score  = sentimentScore(jsonTweet["text"], sentimentDict)
			usStateScore[usState] = usStateScore.get(usState, 0) + score
	values = usStateScore.values()
	if values:
		print usStateScore.keys()[values.index(max(values))]
#		for a in [ "timezone", "location", "utc_offset", "lang", "coordinates", "place", "user", "lang" ]:
#			if (a in jsonTweet and jsonTweet[a] != None and a == "place"):
#				print a
#				print jsonTweet[a]
#				for aa in [ "country_code", "place_type", "name" ]:
#					if jsonTweet[a].get("country_code") == "US":
#						print aa
#						print jsonTweet[a][aa]

#		if "user" in jsonTweet:
#			user = jsonTweet["user"]
#			for a in [ "timezone", "location", "utc_offset", "lang", "coordinates", "place", "user", "lang" ]:
#				if (a in user and user[a] != None and False):
#					print "user."+a
#					print user[a]
"""
		if ("coordinates" in jsonTweet):
			print "=========== coordinates ======="
			print jsonTweet["coordinates"]
		if ("place" in jsonTweet):
			print "======== place ============"
			print jsonTweet["place"]
		if ("user" in jsonTweet):
			user = jsonTweet
			print "=========== user ========="
			#print jsonTweet["user"]
			for  a in :
				if a in user:
					print "%s: %s"%(a,user[a])
			
		if ("place" in jsonTweet):
			print "=========== place ========="
			print jsonTweet["place"]
		if ("lang" in jsonTweet):
			print "========== lang ============"
			print jsonTweet["lang"]
"""
#		if ("text" in jsonTweet):
#			text = jsonTweet["text"].lower()
#			score = sentimentScore(text, sentimentDict)
#			print(score)

if __name__ == '__main__':
    main()
