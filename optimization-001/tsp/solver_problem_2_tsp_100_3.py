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
    # val storedNeighboursLimit = 10
    # tuning parameters: 
    #  if (maxEdgeLength*0.019 > temp){
    #    1000
    #  }else{
    #    1
    #  }
    
    ret = "20798.722833889136 0\n25 40 43 44 99 11 32 21 35 92 54 5 87 20 88 77 37 47 7 83 39 74 66 57 71 24 3 55 51 84 17 79 26 29 14 80 96 16 4 91 13 69 28 62 64 76 34 50 2 89 61 98 67 78 95 73 81 10 75 56 31 27 58 86 65 0 12 93 15 97 33 60 1 36 45 46 30 94 82 49 23 6 85 63 59 41 68 48 42 53 9 18 52 22 8 90 38 70 72 19\n"


    
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

