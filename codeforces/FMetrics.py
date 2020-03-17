from sys import stdin

K = int(stdin.readline())

CM = [[] for i in range(K)]
w = [0] * K
P = [0] * K
R = [0] * K
F = [0] * K
total = 0

def divide(a, b):
	if (b == 0):
		return 0
	else:
		return a / b

for i in range(K):
	line = stdin.readline()
	CM[i] = [int(val) for val in line.split()]
	total += sum(CM[i])

for i in range(K):
	line_total = 0
	column_total = 0
	for j in range(K):
		line_total += CM[i][j]
		column_total += CM[j][i]
	w[i] = divide(line_total, total)
	P[i] = divide(CM[i][i], line_total)
	R[i] = divide(CM[i][i], column_total)

for i in range(K):
	F[i] = divide(2 * P[i] * R[i], (P[i] + R[i])) * w[i]
	P[i] *= w[i]
	R[i] *= w[i]

F_MACR = divide(2 * sum(P) * sum(R), sum(P) + sum(R))
F_MICR = sum(F)

print(F_MACR)
print(F_MICR)