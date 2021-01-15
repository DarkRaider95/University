import numpy as np
import math
from sklearn.base import clone
from copy import copy

class AdaBoost:
	def __init__(self, weakModel, T):
		#genero T modelli
		self.weakModels = list()        
		self.weakModels.append(weakModel)
		for i in range(0, T):
			self.weakModels.append(copy(weakModel))
		self.T = T
		#array pesi modelli
		self.cw = np.array([])
		return None

	def fit(self, X, y):
		w = np.ones(len(X)) / len(X)
		
		for t in range(0, self.T):
			self.weakModels[t].fit(X,y,sample_weight=w*len(X)) #alleno il modello t-esimo
			#self.weakModels[t].fit(X,y,sample_weight=w) #alleno il modello t-esimo
			m = self._weakPredict(X,t)

			miss = [int(x) for x in (m != y)]
			#miss2 = [x if x==1 else -1 for x in miss]

			#err = np.dot(w,miss) / sum(w)

			err = AdaBoost._computeErr(w,m,y)/sum(w) #calcolo errore pesato
			self.cw = np.append(self.cw,(0.5*math.log((1-err)/err))) #calcolo peso classificatore

			#self.cw = np.append(self.cw,0.5 * np.log((1 - err) / float(err)))
			w = self._updateWeights(w,m,y,t) #aggiorno i pesi per gli esempi in base alla classificazione
			#w = np.multiply(w, np.exp([float(x) * self.cw[t] for x in miss2]))

			#self.weakModels.append(type(self.weakModels[t])(self.param))
			if(t % math.floor(self.T/10) == 0):
				print("Iteration T: {} weighted err:{} clf weight:{} Classification Error:{}".format(t, err, self.cw[t], AdaBoost.classificationErr(self.predictT(X,t),y)))

	def predictT(self, X, T):
		y = np.array([])
		for x in X:
			s = 0
			for t in range(0, T):
				s += self.weakModels[t].predict(x.reshape(1,-1))*self.cw[t]
			if s > 0:
				y=np.append(y,1)
			else:
				y=np.append(y,-1)
		return y

	def predict(self, X):
		y = np.array([])
		for x in X:
			s = 0
			for t in range(0,self.T):
				s += self.weakModels[t].predict(x.reshape(1,-1))*self.cw[t]
			if s > 0:
				y=np.append(y,1)
			else:
				y=np.append(y,-1)
		return y
	
	def _weakPredict(self, X, t):
		y = np.array([])
		for x in X:
			y=np.append(y,self.weakModels[t].predict(x.reshape(1,-1)))
		return y
					
	def _computeErr(w,m,y):
		err = 0
		for i in range(0,len(w)):
			if(m[i]!=y[i]):
				err+=w[i]
		return err
	
	def _updateWeights(self,w,m,y,t):
		w2 = np.copy(w)
		for i in range(0,len(y)):
			w2[i]=w[i]*math.exp((-self.cw[t])*y[i]*m[i])
		#w2 /= len(y) #normalizzazione
		return w2

	def classificationErr(y,Y):
		return 1/2-(np.inner(y,Y)/(2*len(Y)))