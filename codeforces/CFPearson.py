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
x = [0] * N
y = [0] * N

for i in range(N):
	cur_line = stdin.readline()
	int_line = [int(val) for val in cur_line.split()]
	x[i] = int_line[0]
	y[i] = int_line[1]

avX = calc_average(x)
avY = calc_average(y)

sum1 = 0.0
sum2 = 0.0
sum3 = 0.0
for i in range(N):
	sum1 += (x[i] - avX) * (y[i] - avY)
	sum2 += pow((x[i] - avX), 2)
	sum3 += pow((y[i] - avY), 2)

r = div_by_zero(sum1, (sqrt(sum2) * sqrt(sum3)))

print(r)