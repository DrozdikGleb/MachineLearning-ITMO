from sys import stdin
from collections import defaultdict
from math import pow, sqrt, log

class Neuron(object):
	def __init__(self):
		self.weights = []
		self.b = 0

def read_int():
	return (int)(stdin.readline())

def generate_binary(n):
	bin_arr = range(0, int(pow(2,n)))
	bin_arr = [bin(i)[2:] for i in bin_arr]

	max_len = len(max(bin_arr, key=len))
	bin_arr = [i.zfill(max_len) for i in bin_arr]
	return bin_arr

M = read_int()

vals = [read_int() for i in range(int(pow(2,M)))]

bin_arr = generate_binary(M)

layer = []

for i in range (len(bin_arr)):
	if (vals[i] == 1):
		cur_str = bin_arr[i]
		neuron = Neuron()
		one_nums = 0
		for j in range(len(cur_str)):
			if (cur_str[j] == '0'):
				neuron.weights.append(-1.0)
			else:
				neuron.weights.append(1.0)
				one_nums += 1
		neuron.b = -(one_nums - 0.5)
		layer.append(neuron)

if (len(layer) == 0):
	print(1)
	print(1)
	sec_layer = [0 for i in range(M)]
	sec_layer.append(-0.5)
	print(*sec_layer)
	exit()

print(2)
print(len(layer), 1)

for i in range(len(layer)):
	cur_neuron = layer[i]
	print(' '.join(map(str, cur_neuron.weights)), cur_neuron.b)

sec_layer = [1 for i in range(len(layer))]
sec_layer.append(-0.5)
print(*sec_layer)
