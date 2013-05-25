import MapReduce
import sys

"""
Word Count Example in the Simple Python MapReduce Framework
"""

mr = MapReduce.MapReduce()

# =============================
# Do not modify above this line

def mapper(record):
	key = record[1]
	value = record
	mr.emit_intermediate(key, value)

def reducer(key, list_of_values):
	orders = [i for i in list_of_values if i[0] == u'order']
	line_items = [i for i in list_of_values if i[0] == u'line_item']
	values = [i+j for i in orders for j in line_items]
	for value in values:
		mr.emit(value)

# Do not modify below this line
# =============================
if __name__ == '__main__':
  inputdata = open(sys.argv[1])
  mr.execute(inputdata, mapper, reducer)
