from sys import stdin

line1 = stdin.readline()
N, M, K = [int(i) for i in line1.split()]

line2 = stdin.readline()
object_class_arr = [(int(val), idx + 1) for idx, val in enumerate(line2.split())]

object_class_arr.sort(key=lambda x: x[0])
answer_list = [[] for i in range(K)]

for idx, (idx_prev,val) in enumerate(object_class_arr):
	part_num = idx % K
	answer_list[part_num].append(val)

for cur_list in answer_list:
	print(len(cur_list), *cur_list)