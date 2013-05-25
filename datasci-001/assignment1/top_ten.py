import sys
import json
import re
import codecs

def main():
	tweet_file = codecs.open(sys.argv[1], encoding='utf-8')
	freqTable = {}
	for tweet in tweet_file:
		jsonTweet = json.loads(tweet)
		entities = jsonTweet.get("entities",None)
		if entities:
			hashtags = entities.get("hashtags",None)
			if hashtags:
				for ht in hashtags:
					tag = ht["text"]
					freqTable[tag] = freqTable.get(tag,0)+1
	i=0
	for tag in sorted(freqTable, key = freqTable.get, reverse = True):
		i = i+1
		if i > 10:
			break;
		print tag, float(freqTable[tag])
		
if __name__ == '__main__':
    main()
