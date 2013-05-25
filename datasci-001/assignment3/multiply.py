import MapReduce
import sys

"""
Word Count Example in the Simple Python MapReduce Framework
"""

mr = MapReduce.MapReduce()

# =============================
# Do not modify above this line
matrices = ["a", "b"]

def mapper(record):
	[matrix, i, j, value] = record
	if (matrix == "a"):
		for k in range(0,5):
			mr.emit_intermediate((i,k), record)
	if (matrix == "b"):
		for k in range(0,5):
			mr.emit_intermediate((k,j), record)

def reducer(key, list_of_values):
	value = 0
	aList = [i for i in list_of_values if i[0] == u'a']
	bList = [i for i in list_of_values if i[0] == u'b']
	for a in aList:
		for b in bList:
			if a[2] == b[1]:
				value += a[3]*b[3]

	if (value > 0):
		mr.emit((key[0], key[1], value))

# Do not modify below this line
# =============================
if __name__ == '__main__':
  inputdata = open(sys.argv[1])
  mr.execute(inputdata, mapper, reducer)
