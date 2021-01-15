import numpy as np

class RandomLinearModel:
	def __init__(self):
		self.w = None		
		return None

	def loss(self, y, y_, w):
		miss = [int(x) for x in (y_ != y)]

		err = np.dot(w,miss) / sum(w)

		if(err > 0.5):
			self.w*-1
			
		return None
		
	def fit(self,X,y,sample_weight=None):
		self.w = np.random.uniform(-1,1, X[0].size)
		self.w = np.append(self.w, np.random.randint(-100,100))

		y_=np.zeros(y.size)

		for i in range(0,len(X)):
			y_[i]=self.predict(X[i])

		self.loss(y,y_,sample_weight)

		return None
		
	def predict(self,X):
		X=np.append(X,1)
		return 1 if np.dot(X,self.w) > 0 else -1