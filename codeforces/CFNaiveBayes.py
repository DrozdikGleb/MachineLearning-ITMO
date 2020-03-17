from sys import stdin
from collections import defaultdict
from math import log, exp
 
def read_int():
	return (int)(stdin.readline())
 
def classify(K, classes, Ls, freq, counts, alpha, L, data, total_words, N):
    answer = [0.0] * K
    total_e = 0.0
    for i in range(K):
    	if (counts[i] != 0):
    		answer[i] += log(classes[i] / N * Ls[i])
    unique_words = set()
    for i in range(L):
    	unique_words.add(data[i])
    for cur_word in unique_words:
    	for j in range(K):
    		if (counts[j] == 0):
    			continue
    		if not cur_word in freq[j]:
    			nominator = alpha
    		else:
    			nominator = (freq[j][cur_word] + alpha)
    		denominator = (counts[j] + alpha * len(freq[j]))
    		if (nominator != 0) and (denominator != 0):
    			answer[j] += log(nominator) - log(denominator)
    maxq = max(answer)
    for i in range(K):
        answer[i] -= maxq
        answer[i] = exp(answer[i]) if (counts[i] != 0) else 0
    total_e = sum(answer)
    for i in range(K):
    	answer[i] = answer[i] / total_e
    return answer
 
K = read_int()
Ls = [int(val) for val in stdin.readline().split()]
a = read_int()
N = read_int()
freq = {}
word_number = defaultdict(lambda:0)
class_number = defaultdict(lambda:0)
total_words = 0

for i in range(N):
	cur_line = stdin.readline().split()
	C = (int)(cur_line[0]) - 1
	L = (int)(cur_line[1])
	unique_words = set()
	for i in range(L):
		unique_words.add(cur_line[i + 2])
	for cur_word in unique_words:
		#cur_word = cur_line[j + 2]
		if not C in freq:
			freq[C] = {}
		if not cur_word in freq[C]:
			freq[C][cur_word] = 0
		freq[C][cur_word] += 1
	word_number[C] += 1.0
	class_number[C] += 1.0
	total_words += L
M = read_int()
for i in range(M):
	cur_line = stdin.readline().split()
	L = (int)(cur_line[0])
	data = [0] * L
	for j in range(L):
		data[j] = cur_line[j + 1]
	answer = classify(K, class_number, Ls, freq, word_number, a, L, data, total_words, N)
	print(*answer)