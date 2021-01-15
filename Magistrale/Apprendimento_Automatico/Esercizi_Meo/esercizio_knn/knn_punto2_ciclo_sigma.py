from sklearn.metrics.pairwise import euclidean_distances
from sklearn import neighbors, datasets
import numpy as np
from sklearn.model_selection import cross_val_score 

n_neighbors = 7

iris = datasets.load_iris()

#params = dict()

#params[X]=iris.data
#params["gamma"]=0.5
def rbf_kernel(X, Y=None, gamma=None):
	K = np.exp(-(np.linalg.norm(X-Y)**2)/(2*gamma**2))
	#K = np.zeros((X.shape[0],Y.shape[0]))
	#for i,x in enumerate(X):
	#	for j,y in enumerate(Y):
	#		K[i,j] = np.exp(-(np.linalg.norm(x-y)**2)/(2*gamma**2))
	return K
	
	#X, Y = check_pairwise_arrays(X, Y)
	#X=X.reshape(1,-1)
	#Y=X
	#if gamma is None:
	#    gamma = 1.0 / X.shape[1]
#
#    #K = euclidean_distances(X, Y, squared=True)
#    #K *= -gamma
#    #K = np.exp(K)    # exponentiate K in-place
	#return K
gamma = 0.01
hit = 0
miss = 0

scores=[0]

count = 0

prev = 0

while (sum(scores)/len(scores)<0.90 and count < 100):    
	print("Gamma:",gamma)
	clf_knn = neighbors.KNeighborsClassifier(n_neighbors, weights='distance', metric=rbf_kernel, metric_params={"gamma":gamma})
	clf_knn = clf_knn.fit(iris.data, iris.target)
	scores = cross_val_score(clf_knn, iris.data, iris.target, cv=5) # score will be the accuracy
	print(scores)
	
	# shows the model predictions  
	for i in range(len(iris.target)):
		instance=(iris.data[i,:]).reshape(1, -1)
		predicted=clf_knn.predict(instance)[0]
		if iris.target[i]==predicted:
			hit+=1
		else:
			miss+=1
	
	if(prev + prev*0.1 < sum(scores)/len(scores) or prev - prev*0.1 > sum(scores)/len(scores)):
		prev = sum(scores)/len(scores)
		print("Classified:{} Missclassified{}".format(hit, miss))
		count = 0
	else:
		count+=1
	
	hit=0
	miss=0
	gamma += 0.001

"""
clf_knn = neighbors.KNeighborsClassifier(n_neighbors, weights='distance', metric=rbf_kernel, metric_params={"gamma":0.5})
clf_knn = clf_knn.fit(iris.data, iris.target)
scores = cross_val_score(clf_knn, iris.data, iris.target, cv=5) # score will be the accuracy
print(scores)
# shows the model predictions  
for i in range(len(iris.target)):
	#instance=(iris.data[i,:])
	#print("instance",instance)
	instance=(iris.data[i,:]).reshape(1, -1)
	#print("instance RESHAPED",instance)
	predicted=clf_knn.predict(instance)[0]
"""