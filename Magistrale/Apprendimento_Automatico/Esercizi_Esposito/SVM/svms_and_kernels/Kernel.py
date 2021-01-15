import sklearn
import numpy as np
from matplotlib import pyplot as plt
from sklearn.svm import SVC
from sklearn.metrics.pairwise import euclidean_distances
from kernel_usage_dataset import X,labels_1, labels_2, labels_3

def gaussian_kernel(X, Y):
	K = np.zeros((X.shape[0],Y.shape[0]))
	for i,x in enumerate(X):
		for j,y in enumerate(Y):
			K[i,j] = np.exp(-(np.linalg.norm(x-y)**2)/(2*0.001**2))
	return K

def polynomial_kernel(X, Y):
	K = (np.inner(X,Y)+1)**X.shape[1]

	return K

def laplacian_kernel(X, Y):
	K = np.zeros((X.shape[0],Y.shape[0]))
	for i,x in enumerate(X):
		for j,y in enumerate(Y):
			K[i,j] = np.exp(-(np.linalg.norm(x-y))/0.5)
	return K

def countErr(y1,y2):
	miss = 0
	for i in range(0, len(y1)):
		if(y1[i]!=y2[i]):
			miss+=1

	return miss

def test(C, kernel, title, target):
	clf = SVC(C=C,kernel=kernel)
	clf.fit(X, target)

	print(title)

	y_p = clf.predict(X)

	miss = countErr(y_p,target)

	print("Errori di classificazione:", miss)

	for i in y_p:
		if i == 'r':
			i='red'
		elif i == 'b':
			i='blue'
	
	x = np.squeeze(np.asarray(X[:,0]))
	y = np.squeeze(np.asarray(X[:,1]))

	plt.scatter(x, y, c=y_p)
	plt.show()

def main():
	test(1, laplacian_kernel, "Kernel 1 Laplacian kernel", labels_1)
	test(100, polynomial_kernel, "Kernel 2 Polynomial kernel", labels_2)
	test(100, gaussian_kernel, "Kernel 3 Gaussian kernel", labels_3)
	
if __name__ == '__main__':
	main()