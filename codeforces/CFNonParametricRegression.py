from sys import stdin
from math import sqrt, cos, exp
from math import pi

def manhattan(a, b):
	ans = 0
	for i in range(len(a)):
		ans += abs(a[i] - b[i])
	return ans

def euclidean(a, b):
	ans = 0
	for i in range (len(a)):
		ans += pow(a[i] - b[i], 2)
	return sqrt(ans)

def chebyshev(a, b):
	maxDist = -1
	for i in range (len(a)):
		maxDist = max(maxDist, abs(a[i] - b[i]))
	return maxDist

def uniform(a):
	return 0.5 if (abs(a) < 1) else 0.0

def triangular(a):
	return (1 - abs(a)) if (abs(a) < 1) else 0.0

def epanechnikov(a):
	return 3 / 4 * (1 - pow(a, 2)) if (abs(a) < 1) else 0.0

def quartic(a):
	return 15 / 16 * pow((1 - pow(a, 2)), 2) if (abs(a) < 1) else 0.0

def triweight(a):
	return 35 / 32 * pow((1 - pow(a, 2)), 3) if (abs(a) < 1) else 0.0

def tricube(a):
	return 70 / 81 * pow((1 - pow(abs(a), 3)), 3) if (abs(a) < 1) else 0.0

def gaussian(a):
	return 1 / sqrt(pi * 2) * exp((-0.5) * pow(a, 2));

def cosine(a):
	return pi / 4 * cos(pi / 2 * a) if (abs(a) < 1) else 0.0

def logistic(a):
	return 1 / (exp(a) + 2 + exp(-a))

def sigmoid(a):
	return 2 / pi * (1 / (exp(a) + exp(-a)))

def fixed(h, p, distFunc):
	pLen = len(p)
	pKern = [0] * pLen
	for i in range(pLen):
		pKern[i] = distFunc(p[i] / h)
	return pKern

def variable(k, p, distFunc):
	return fixed(p[k], p, distFunc)

def count(q, p, targets, isFixed, h, kernlFunc, distFunc):
	pNum = len(p)
	p = map(lambda x: globals()[distFunc](x, q), p)
	p, targets = zip(*sorted(zip(p, targets), key=lambda x: x[0]))
	if (h == 0 or p[0] == 0):
		s = 0
		total = 0
		for p_cur, t_cur in zip(p, targets):
			if p_cur != 0:
				break
			s += t_cur
			total += 1
		return s / total if (total != 0) else sum(targets) / len(targets)
	kernelArr = []
	if (isFixed):
		kernelArr = fixed(h, p, globals()[kernlFunc])
	else:
		kernelArr = variable(h, p, globals()[kernlFunc])
	wTotal = sum(kernelArr)
	targetsTotal = 0
	for i in range(pNum):
		targetsTotal += (kernelArr[i] * targets[i])
	if (wTotal == 0):
		return sum(targets) / len(targets)
	else:
		return targetsTotal / wTotal
		

line = stdin.readline()
N, M = [int(i) for i in line.split()]
points = [([0] * M) for i in range(N)]
targets = [0] * N
for i in range(N):
	cur_line = stdin.readline()
	int_line = [int(val) for val in cur_line.split()]
	for j in range(len(int_line) - 1):
		points[i][j] = int_line[j]
	targets[i] = int_line[len(int_line) - 1]

q_line = stdin.readline()
q = [int(i) for i in q_line.split()]

distMetric = stdin.readline().rstrip('\n')
kernel = stdin.readline().rstrip('\n')
window = stdin.readline().rstrip('\n')
h = int(stdin.readline())
ans = 0.0
if (window == "fixed"):
	ans = count(q, points, targets, True, h, kernel, distMetric)
else: 
	ans = count(q, points, targets, False, h, kernel, distMetric)

print (ans)



