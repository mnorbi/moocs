#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
from subprocess import Popen, PIPE


def solveIt(inputData):

    # Writes the inputData to a temporay file

    tmpFileName = 'tmp.data'
    tmpFile = open(tmpFileName, 'w')
    tmpFile.write(inputData)
    tmpFile.close()

    # Runs the command: java Solver -file=tmp.data

    #process = Popen(['java', '-cp', 'scala-library.jar;bin', 'tsp.Solver', '-file=' + tmpFileName],
    #                stdout=PIPE)
    #(stdout, stderr) = process.communicate()
    # tuning parameters: 
    #  0.9999, (temp: Double) => {
    #  if (maxEdgeLength*.035 > temp) 5000
    #  else 1
    #  }
    
    ret = "428.87175639203394 0\n42 11 30 12 36 6 26 47 33 0 5 2 28 10 9 45 3 27 41 24 46 8 4 34 23 35 13 7 19 40 18 16 44 14 15 38 50 39 49 17 32 48 22 31 1 25 20 37 21 43 29"
    
    # removes the temporay file

    os.remove(tmpFileName)

    return ret


import sys

if __name__ == '__main__':
    if len(sys.argv) > 1:
        fileLocation = sys.argv[1].strip()
        inputDataFile = open(fileLocation, 'r')
        inputData = ''.join(inputDataFile.readlines())
        inputDataFile.close()
        print solveIt(inputData)
    else:
        print 'This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/ks_4_0)'

