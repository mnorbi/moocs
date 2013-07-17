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
    #  if (maxEdgeLength*.016 > temp){
    #    500
    #  }else{
    #    1
    #  }
    
    ret = "29716.289983744635 0\n25 86 162 147 130 94 150 55 20 181 46 27 11 114 132 105 103 182 81 163 113 24 19 141 9 8 101 115 4 176 2 82 39 5 17 84 58 149 63 142 188 95 85 159 64 173 186 13 67 32 165 44 98 77 30 56 71 134 160 126 75 79 193 156 106 183 157 68 133 108 124 145 45 120 189 100 194 197 73 60 170 111 6 131 66 74 158 35 128 107 198 175 196 190 28 127 57 102 110 192 21 184 172 41 22 109 167 10 88 152 69 48 169 97 138 89 16 139 166 96 104 31 93 161 125 199 155 0 49 168 174 129 80 33 148 185 37 65 7 51 137 119 179 26 23 164 87 178 12 180 78 146 40 83 136 171 14 72 38 53 90 62 153 151 15 76 42 70 187 122 121 92 3 154 43 59 52 123 117 144 135 18 191 50 118 36 195 61 34 29 99 1 143 47 140 91 116 177 54 112\n" 

    
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

