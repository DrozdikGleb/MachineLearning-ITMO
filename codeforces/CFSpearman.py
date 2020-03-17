from sys import stdin
from collections import defaultdict
from math import pow, sqrt

def read_int():
	return (int)(stdin.readline())

def calc_average(x):
	return div_by_zero(sum(x), len(x))

def div_by_zero(x, y):
	if (y == 0):
		return 0
	else:
		return x / y

N = read_int()
x = [(0, 0)] * N
y = [(0, 0)] * N

for i in range(N):
	cur_line = stdin.readline()
	int_line = [int(val) for val in cur_line.split()]
	x[i] = (int_line[0], i + 1, 0)
	y[i] = (int_line[1], i + 1, 0)

x.sort(key=lambda elem: elem[0])
y.sort(key=lambda elem: elem[0])

for i in range(N):
	curX = list(x[i])
	curY = list(y[i]) 
	curX[2] = i
	curY[2] = i
	x[i] = tuple(curX)
	y[i] = tuple(curY)

x.sort(key=lambda elem: elem[1])
y.sort(key=lambda elem: elem[1])

p = div_by_zero(6, N * (N - 1) * (N + 1))
sum1 = 0.0
for i in range(N):
	curX = x[i]
	curY = y[i] 
	#print(curX)
	#print(curY)
	sum1 += pow(curX[2] - curY[2], 2)
p = 1 - p * sum1
print(p)