from sys import stdin

class DatasetObject:
    def __init__(self, features = None, y = None):
        self.features = features
        self.y = y

class GiniCalculator:
    def __init__(self, objects = None):
        self.gini_sum = 0
        self.objects_amount = 0
        if (objects is not None):
            self.objects_amount = len(objects)
        self.map_class_to_amount = {}
        if (objects == None):
            return
        for elem in objects:
            self.update_map(elem.y, True)
        for k, v in self.map_class_to_amount.items():
            self.gini_sum += pow(v, 2)

    def update_map(self, y, isLeft):
        if (isLeft):
            if (y in self.map_class_to_amount):
                self.map_class_to_amount[y] = self.map_class_to_amount[y] + 1
            else:
                self.map_class_to_amount[y] = 1
        else:
            self.map_class_to_amount[y] = self.map_class_to_amount[y] - 1

    def update_as_left(self, y):
        self.gini_sum -= pow(self.map_class_to_amount.get(y, 0), 2)
        self.update_map(y, True)
        self.gini_sum += pow(self.map_class_to_amount.get(y, 0), 2)
        self.objects_amount = self.objects_amount + 1

    def update_as_right(self, y):
        self.gini_sum -= pow(self.map_class_to_amount.get(y, 0), 2)
        self.update_map(y, False)
        self.gini_sum += pow(self.map_class_to_amount.get(y, 0), 2)
        self.objects_amount = self.objects_amount - 1

    def get_gini(self):
        if (self.objects_amount == 0):
            return 1.0
        else:
            return 1.0 - self.gini_sum / pow(self.objects_amount, 2)

class Node:
    def __init__(self, objects = None):
        self.root = None
        self.objects = objects
        self.left = None
        self.right = None
        self.clazz = None
        self.parent = None
        self.val = None
        self.feature_id = None
        
    def isLeaf(self):
        return self.left == None and self.right == None
    
    def build_children(self, feature_id, feature_val):
        leftElems = []
        rightElems = []
        for elem in self.objects:
            if (elem.features[feature_id] < feature_val):
                leftElems.append(elem)
            else:
                rightElems.append(elem)
        self.left = Node(leftElems)
        self.right = Node(rightElems)
        self.left.parent = self
        self.right.parent = self
    
class DecisionTree:
    def __init__(self, h=None):
        self.h = h
        self.X = None
        self.Y = None
        self.objects = []
        
    def fit(self, X, Y):
        self.X = X
        self.Y = Y
        self.features_num = len(X[0])
        self.objects_num = len(X)
        for i in range(self.objects_num):
            cur_object = DatasetObject(X[i], Y[i])
            self.objects.append(cur_object)
        return self.build_tree(self.objects)
    
    def __predict(self, node, x):
        if (node.left == None):
            return node.clazz
        if x[node.feature_id] < node.val:
            return self.__predict(node.left, x)
        else:
            return self.__predict(node.right, x)
    
    def predict(self, x):
        return self.__predict(self.root, x)
        
    def predict_all(self, X):
        res = []
        for i in range(len(X)):
            res.append(self.predict(X[i]))
        return res
        
    def build_tree(self, objects):
        root = Node(objects)
        self.root = root
        return self.build_tree_inner(root, 1)
        
    def build_tree_inner(self, node, depth):
        if (len(node.objects) == 0):
            node.clazz = node.parent.clazz
        if (depth > self.h):
            node.clazz = self.get_most_common_class(node.objects)
        else:
            best_feature_id, best_feature_val = self.get_best_feature(node.objects)
            node.build_children(best_feature_id, best_feature_val)
            node.feature_id = best_feature_id
            node.val = best_feature_val
            self.build_tree_inner(node.left, depth + 1)
            self.build_tree_inner(node.right, depth + 1)

    def get_most_common_class(self, objects):
        y = [elem.y for elem in objects]
        return max(set(y), key=y.count)

    def get_best_feature(self, objects):
        gini_min = 100000
        best_feature_id = -1
        best_feature_val = -1
        obj_num = len(objects)
        for feature_id in range(self.features_num):
            #sort by feature id
            objects.sort(key = lambda cur_object: cur_object.features[feature_id])
            left_calc = GiniCalculator(None)
            right_calc = GiniCalculator(objects)
            for j in range(obj_num - 1):
                cur_object = objects[j]
                next_object = objects[j + 1]
                cur_val = (cur_object.features[feature_id] + next_object.features[feature_id]) / 2.0
                left_calc.update_as_left(cur_object.y)
                right_calc.update_as_right(cur_object.y)
                cur_gini = (left_calc.objects_amount / obj_num) * left_calc.get_gini() + (right_calc.objects_amount / obj_num) * right_calc.get_gini()
                #cur_gini = self.count_split_gini(objects, j)
                if (cur_gini < gini_min):
                    gini_min = cur_gini
                    best_feature_id = feature_id
                    best_feature_val = cur_val
        print(best_feature_id, best_feature_val)
        return best_feature_id, best_feature_val
    
    def count_split_gini(self, objects, split_pos):
        obj_len = len(objects)
        left_obj_num = split_pos + 1
        right_obj_num = obj_len - left_obj_num
        right_gini = self.calc_gini_index(objects[left_obj_num:obj_len])
        left_gini = self.calc_gini_index(objects[0:left_obj_num])
        return (left_obj_num / obj_len) * left_gini + (right_obj_num / obj_len) * right_gini
    
    def calc_gini_index(self, objects):
        class_to_amount = {}
        obj_num = len(objects)
        for elem in objects:
            if (elem.y in class_to_amount):
                class_to_amount[elem.y] = class_to_amount[elem.y] + 1
            else:
                class_to_amount[elem.y] = 1
        gini_index = 0.0
        for k, v in class_to_amount.items():
            gini_index += pow(v / obj_num, 2)
        return 1 - gini_index

def read_int():
    return (int)(stdin.readline())

line1 = stdin.readline()
M, K, H = [int(i) for i in line1.split()]

N = read_int()
X = [[] for i in range(N)]
Y = []

for i in range(N):
    cur_line = stdin.readline().split()
    for j in range(M):
        X[i].append(float(cur_line[j]))
    Y.append(int(cur_line[M]))

tree = DecisionTree(H)
tree.fit(X, Y)


