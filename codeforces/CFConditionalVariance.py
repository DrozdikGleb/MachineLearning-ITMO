from sys import stdin
from collections import defaultdict
from math import pow, sqrt, log

def read_int():
	return (int)(stdin.readline())

def calc_var(x):
	av = sum(x) / len(x)
	total = 0.0
	for i in range(len(x)):
		total += pow(x[i] - av, 2)
	return total / len(x)

K = read_int()
N = read_int()

Xs = [[] for i in range(K)]
XsNum = [0.0] * K

for i in range(N):
	cur_line = stdin.readline()
	int_line = [int(val) for val in cur_line.split()]
	x = int_line[0] - 1
	y = int_line[1]
	XsNum[x] += 1.0
	Xs[x].append(y)

ans = 0.0
for i in range(K):
	curList = Xs[i]
	if (len(curList) == 0):
		continue
	ans += (len(curList) / N) * calc_var(curList)
print(ans)