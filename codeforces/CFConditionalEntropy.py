from sys import stdin
from collections import defaultdict
from math import pow, sqrt, log

def read_int():
	return (int)(stdin.readline())

line = stdin.readline()
ints = [int(val) for val in line.split()]
Kx = ints[0]
Ky = ints[1]
N = read_int()

Xs = [{} for i in range(Kx)]
XsNum = [0.0] * Kx

for i in range(N):
	cur_line = stdin.readline()
	int_line = [int(val) for val in cur_line.split()]
	x = int_line[0] - 1
	y = int_line[1] - 1
	XsNum[x] += 1.0
	if y in Xs[x]:
		Xs[x][y] += 1.0
	else:
		Xs[x][y] = 1.0

totalSum = 0.0
for i in range(Kx):
	xLen = XsNum[i]
	sumPi = xLen / float(N)
	curSum = 0.0
	curDict = Xs[i]
	for key, value in curDict.items():
		if (value == 0):
			continue
		curVal = value / float(xLen)
		curSum -= curVal * log(curVal)
	totalSum += curSum * sumPi

print(totalSum)
