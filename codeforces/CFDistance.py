from sys import stdin
from collections import defaultdict
from math import pow, sqrt

def read_int():
	return (int)(stdin.readline())

def count_distance(x):
	l = len(x)
	x.sort()
	dist = 0
	for i in range(l - 1, -1, -1):
		dist += i * x[i] - (l - 1 - i) * x[i]
	return dist * 2

K = read_int()
N = read_int()
Xs = [[] for i in range(K)]
XsAll = []

for i in range(N):
	cur_line = stdin.readline()
	int_line = [int(val) for val in cur_line.split()]
	x = int_line[0]
	y = int_line[1] - 1
	Xs[y].append(x)
	XsAll.append(x)

sumInternal = 0
for i in range(K):
	curXs = Xs[i]
	sumInternal += count_distance(curXs)

totalSum = count_distance(XsAll)
p = 4

print(sumInternal)
print(totalSum - sumInternal)
