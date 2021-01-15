from sklearn.metrics.pairwise import euclidean_distances
from sklearn import neighbors, datasets
from sklearn.model_selection import cross_val_score 
import numpy as np

n_neighbors = 7

iris = datasets.load_iris()

def gaussian_kernel(X, Y=None, gamma=None):
    """
    Compute the rbf (gaussian) kernel between X and Y::
        K(x, y) = exp(-gamma ||x-y||^2)
    for each pair of rows x in X and y in Y.
    Read more in the :ref:`User Guide <rbf_kernel>`.
    Parameters
    ----------
    X : array of shape (n_samples_X, n_features)
    Y : array of shape (n_samples_Y, n_features)
    gamma : float, default None
        If None, defaults to 1.0 / n_features
    Returns
    -------from sklearn.model_selection import cross_val_score 
    
    kernel_matrix : array of shape (n_samples_X, n_samples_Y)
    """
    #X, Y = check_pairwise_arrays(X, Y)
    X=X.reshape(1,-1)
    Y=Y.reshape(1,-1)
    
    if Y is None:
        Y=X
    if gamma is None:
        gamma = 1.0 / X.shape[1]

    K = -(euclidean_distances(X, Y, squared=True))/(2*gamma**2)
    np.exp(K, K)    # exponentiate K in-place
    return K

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
    gamma += gamma/2