import sys
import json
import re
import codecs

def main():
	tweet_file = codecs.open(sys.argv[1], encoding='utf-8')
	freqTable = {}
	for tweet in tweet_file:
		jsonTweet = json.loads(tweet)
		if ("text" in jsonTweet):
			text = jsonTweet["text"]
			for term in text.split():
				act = 0
				if term in freqTable:
					 act = freqTable[term]
				freqTable[term] = act+1
	allTermFreq = float(sum(freqTable.values()))
	for term in freqTable:
		print "%s %0.4f" %(term.encode('utf-8'), freqTable[term]/allTermFreq)

if __name__ == '__main__':
    main()
