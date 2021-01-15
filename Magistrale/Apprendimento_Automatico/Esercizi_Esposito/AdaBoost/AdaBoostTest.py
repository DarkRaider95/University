import numpy as np
import math
import sklearn.datasets
from sklearn.svm import SVC
from AdaBoost import AdaBoost
from RandomLinearModel import RandomLinearModel

def classificationErr(y,Y):
    return 1/2-(np.inner(y,Y)/(2*len(Y)))

X,y = sklearn.datasets.make_hastie_10_2()
X_train = X[0:8000,:]
y_train = y[0:8000]
X_test = X[8000:,:]
y_test = y[8000:]

def exercise(weakModel, T):
	
	adaboost = AdaBoost(weakModel, T)

	adaboost.fit(X_train, y_train)

	y_train_ = adaboost.predict(X_train)
	y_test_ = adaboost.predict(X_test)

	print("Error on training set: ", classificationErr(y_train_, y_train))
	print("Error on test set: ", classificationErr(y_test_, y_test))


def main():
	weakModel1 = SVC(kernel="poly", degree=3)
	#weakModel2 = RandomLinearModel()
	exercise(weakModel1, 100)
	#exercise(weakModel2, 10000)

if __name__ == '__main__':
	main()