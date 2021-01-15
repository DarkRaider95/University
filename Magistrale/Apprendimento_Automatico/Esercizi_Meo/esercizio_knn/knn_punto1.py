import numpy as np
import matplotlib.pyplot as plt
from sklearn import neighbors, datasets

n_neighbors = 4

iris = datasets.load_iris()

X = iris.data[:, :2]
Y = iris.target

clf_knn = neighbors.KNeighborsClassifier(n_neighbors, weights='uniform')
clf_knn = clf_knn.fit(X, Y)

predicted=[]

for i in range(len(Y)):
    instance=(X[i,:]).reshape(1, -1)    
    predicted.append(clf_knn.predict(instance)[0])

for i,j in zip(Y,predicted):
	if(i!=j):
		print("MISSCLASSIFIED")
	else:
		print("CLASSIFIED")

y = []

for i in predicted:
    if i == 0:
        y.append("red")
    elif i== 1:
        y.append("blue")
    else:
        y.append("green")
        
plt.scatter(X[:,0], X[:,1], c=y)
plt.show()