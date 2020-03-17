from sys import stdin
from math import sqrt, cos, exp
from math import pi
import pandas
from sklearn.metrics import f1_score
import operator

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
        if (h == 0):
            pKern[i] = 0
		pKern[i] = distFunc(p[i] / h)
	return pKern

def variable(k, p, distFunc):
	return fixed(p[k], p, distFunc)

def countFScore(x, y, isFixed, h, kernlFunc, distFunc):
    size = len(x)
    actualClasses = list()
    predictedClasses = list()
    for i in range(size):
        train_x = x.drop(i).values.tolist()
        train_y = y.drop(i).values.tolist()
        test_x = x.iloc[i].values.tolist()
        test_y = y.iloc[i]
        actualClasses.append(test_y)
        predictedClasses.append(predictClass(test_x, train_x, train_y, isFixed, h ,kernlFunc, distFunc))
    return f1_score(actualClasses, predictedClasses, average='micro')

def predictClass(q, p, targets, isFixed, h, kernlFunc, distFunc):
	pNum = len(p)
	p = map(lambda x: globals()[distFunc](x, q), p)
	p, targets = zip(*sorted(zip(p, targets), key=lambda x: x[0]))
	kernelArr = []
	if (isFixed):
		kernelArr = fixed(h, p, globals()[kernlFunc])
	else:
		kernelArr = variable(h, p, globals()[kernlFunc])
    mapClasses = {}
    classValues = [0, 1, 4, 5, 7]
    for key in classValue:
        mapClasses[key] = 0
    for i in range(len(kernelArr)):
        mapClasses[targets[i]] += kernelArr[i]
    return max(mapClasses.items(), key=operator.itemgetter(1))[0]
