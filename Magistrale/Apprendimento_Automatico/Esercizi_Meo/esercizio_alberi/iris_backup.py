from sklearn import tree
from sklearn.datasets import load_iris
from sklearn.metrics import accuracy_score
from sklearn.metrics import f1_score
from sklearn.model_selection import cross_val_score
import numpy as np
import graphviz
from sklearn.metrics import confusion_matrix
import matplotlib.pyplot as plt

"""
Modify the given Jupyter notebook on decision trees on Iris data and perform the following tasks:

1. get an artificial inflation of some class by a given factor (weigh more the classes virginica e versicolor which are more difficult to discriminate).
2. modify the weight of some classes (set to 10 the weights for misclassification between virginica into versicolor and vice versa)
3. avoid overfitting (by improving the error on the test set) tuning the parameters on: the minimum number of samples per leaf, max depth of the tree, min_impurity_decrease parameters, max leaf nodes, etc.
4. build the confusion matrix 
5. build the ROC curve (or coverage curve in coverage space): you have to build three curves, one for each class, considered in turn as the positive class
"""

#FUNZIONE CHE GONFIA IL DATASET CON UN PESO PER OGNI CLASSE
def inflation(w1, w2, w3, target):  
	#ciclo target controllo classe appartenza è aggiungo numero indice per il peso e mischio
	indices = []
	
	for i in range(0,len(target)):
		if(target[i]==0):
			indices+=([i]*w1)
		elif(target[i]==1):
			indices+=([i]*w2)
		else:
			indices+=([i]*w3)

	#print(indices)
	return np.random.permutation(indices)

#FUNZIONE CHE COSTRUISCE LA CURVA DI COPERTURA
def buildCurve(pos, values):
	curvex=[0]
	curvey=[0]
	prevx = 0
	prevy = 0
	for v in values:
		curvey.append(prevy+v[pos])
		curvex.append(prevx+sum(v)-v[pos])
		prevy = prevy+v[pos]
		prevx = prevx+sum(v)-v[pos]

	return [curvex, curvey]

def buildCurves(clf, X, Y, classes):
	#ID FOGLIE PER OGNI ESEMPIO X
	leaves_ids = clf.apply(X)

	values = dict()

	#CONTO ELEMENTI PER OGNI CLASSE NELLE FOGLIE
	for i in range(0,len(leaves_ids)):
		if(leaves_ids[i] not in values):
			values[leaves_ids[i]]=[0,0,0]		
		values[leaves_ids[i]][Y[i]]+=1
					
	values = list(values.values())
	curves = []

	#print("values", list(values))
	for i in range(0, classes):
		#ORDINO IN BASE ALLA CLASSE POSITIVA
		values.sort(key=lambda x:x[i], reverse=True)
		#print("VALUES_{} : {}\n".format(i, values))
		curves.append(buildCurve(i, values))

	return curves


X = [[0, 0, 0], [1, 1, 1], [0, 1, 0], [0, 0, 1], [1, 1, 0], [1, 0, 1]]
Y = [1, 0, 0, 0, 1, 1]

clf = tree.DecisionTreeClassifier()
clf = clf.fit(X, Y)

print(clf.predict([[0, 1, 1]]))

print(clf.predict([[1, 0, 1],[0, 0, 1]]))

dot_data = tree.export_graphviz(clf, out_file="tree")  
graph = graphviz.Source(dot_data)  

iris = load_iris()

clf = tree.DecisionTreeClassifier(criterion="entropy", random_state=300, min_samples_split=40,min_samples_leaf=1,max_depth=3,class_weight={0:1,1:10,2:10})#[{0:1, 1:1, 2:1}, {0:1, 1:1, 2:10}, {0:1, 1:10, 2:1}]

# Generate a random permutation of the indices of examples that will be later used 
# for the training and the test set
np.random.seed(0)
indices = inflation(2, 3, 3 , iris.target)#np.random.permutation(len(iris.data))
print("length dataset: ",len(indices))

# We now decide to keep the last 10 indices for test set, the remaining for the training set
indices_training=indices[:-100]
indices_test=indices[-100:]

iris_X_train = iris.data[indices_training] # keep for training all the matrix elements with the exception of the last 10 
iris_y_train = iris.target[indices_training]
iris_X_test  = iris.data[indices_test] # keep the last 10 elements for test set
iris_y_test  = iris.target[indices_test]

print("daset training: ",len(iris_X_train))

# fit the model to the training data
clf = clf.fit(iris_X_train, iris_y_train)

# apply fitted model "clf" to the test set 
predicted_y_test = clf.predict(iris_X_test)

# print the predictions (class numbers associated to classes names in target names)
print("Predictions:")
print(predicted_y_test)
print("True classes:")
print(iris_y_test) 
print(iris.target_names)

# print the corresponding instances indexes and class names 
for i in range(len(iris_y_test)): 
	print("Instance # "+str(indices_test[i])+": ")
	print("Predicted: "+iris.target_names[predicted_y_test[i]]+"\t True: "+iris.target_names[iris_y_test[i]]+"\n")

for i in range(len(iris_y_test)): 
	print("Instance # "+str(indices_test)+": ")
	s=""
	for j in range(len(iris.feature_names)):
		s=s+iris.feature_names[j]+"="+str(iris_X_test[i][j])
		if (j<len(iris.feature_names)-1): s=s+", "
	print(s)
	print("Predicted: "+iris.target_names[predicted_y_test[i]]+"\t True: "+iris.target_names[iris_y_test[i]]+"\n")

# print some metrics results
acc_score = accuracy_score(iris_y_test, predicted_y_test)
print("Accuracy score: "+ str(acc_score))
f1=f1_score(iris_y_test, predicted_y_test, average='macro')
print("F1 score: "+str(f1))


#clf = tree.DecisionTreeClassifier(criterion="entropy",random_state=300,min_samples_leaf=5,class_weight={0:1,1:10,2:10})
#clf = clf.fit(iris.data, iris.target)
scores = cross_val_score(clf, iris.data, iris.target, cv=5) # score will be the accuracy
print("Cross Validation Score: ",scores)

# computes F1- score
f1_scores = cross_val_score(clf, iris.data, iris.target, cv=5, scoring='f1_macro')
print("F1 scores: ",f1_scores)


dot_data = tree.export_graphviz(clf, out_file=None) 
graph = graphviz.Source(dot_data) 
graph.render("my_iris_predictions")

print(list(iris.feature_names))
print(list(iris.target_names))

#MATRICE DI CONFUSIONE
print("Confusion Matrix:")
print(confusion_matrix(iris_y_test, predicted_y_test))

#CURVE DI COPERTURA
curves = buildCurves(clf, iris_X_train, iris_y_train, len(iris.target_names))

print(curves)

plt.plot(curves[0][0], curves[0][1], label='Setosa')
plt.plot(curves[1][0], curves[1][1], label='Versicolour')
plt.plot(curves[2][0], curves[2][1], label='Virginica')
plt.xlabel("Negative")
plt.ylabel("Positive")

plt.show()