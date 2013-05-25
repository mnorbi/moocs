import MapReduce
import sys

"""
Word Count Example in the Simple Python MapReduce Framework
"""

mr = MapReduce.MapReduce()

# =============================
# Do not modify above this line

def mapper(record):
	personA = record[0]
	personB = record[1]
	mr.emit_intermediate((personB, personA), 0)
	mr.emit_intermediate((personA, personB), 1)

def reducer(key, list_of_values):
	if not (1 in list_of_values and 0 in list_of_values):
		mr.emit(key)

# Do not modify below this line
# =============================
if __name__ == '__main__':
  inputdata = open(sys.argv[1])
  mr.execute(inputdata, mapper, reducer)
