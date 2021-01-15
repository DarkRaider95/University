from sklearn import datasets
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
import matplotlib.pyplot as plt
import numpy as np
import math

def genClassifiers(probs):
	Pos=[]
	Neg=[]	

	#labelling probs in all possible ways
	for i in range(0,len(probs)):
		Pos.append(probs[:i])
		Neg.append(probs[i:])

	return Pos, Neg

def calcTPFP(Pos, Neg):
	FP=[]
	TP=[]

	for P in Pos:
		fp = 0
		tp = 0		
		
		for p in P:
			#print(p)
			if(p[1] == 0):
				tp = tp + 1
			else:
				fp = fp + 1
		#print(tp,fp)		
		TP.append(tp)
		FP.append(fp)

	return FP, TP 

def coverage_area(FP,TP, Nneg):
	cov_area = 0

	for i in range(0,len(TP)-1):		
		cov_area = cov_area + ((Nneg - FP[i]) * (TP[i+1]-TP[i]))

	return cov_area

def ranking_error(probs_and_target, Npos, Nneg):
	error=0
	halferror=0

	for i in range(0, len(probs_and_target)-2):
		if(probs_and_target[i][1]==1):
			for j in range(i+1, len(probs_and_target)-1):
				if(probs_and_target[i][1] != probs_and_target[j][1] and probs_and_target[i][0][0] == probs_and_target[j][0][0]):
					halferror=halferror+1
				elif(probs_and_target[i][1] != probs_and_target[j][1]):
					error=error+1

	print("Errors:{} Halferror:{}".format(error, halferror))
	print("Tot Errors: ", error + (halferror/2))
	print("NNeg: ", Nneg)
	print("Pos: ", Npos)
	print("Pos * Neg: ", Npos*Nneg)
	
	return (error+(halferror/2))/((len(probs_and_target)-Nneg)*Nneg)

def main():
	#First part: load the data and fit a scoring classifier
	data = datasets.load_breast_cancer()
	model = LogisticRegression()

	model.fit(data.data, data.target)

	probs = model.predict_proba(data.data)

	#sorted(probs, key=lambda x: x[0], reverse=True)
	#probs.sort(reverse=True)

	#print(probs)

	probs_and_target = list(zip(probs, data.target))
	
	#Second part: ranking and coverage plot
	probs_and_target.sort(key=lambda x:x[0][0], reverse=True)
	#sorted(probs_and_target, key=lambda x: x[0][0], reverse=True)
	#print(probs_and_target)
	#print("Len / 2: ", len(probs)/2)

	Pos, Neg = genClassifiers(probs_and_target)

	#print("Pos: {} Neg: {}".format(len(Pos), len(Neg)))

	FP, TP = calcTPFP(Pos,Neg)

	#print("FP: {} TP: {}".format(FP, TP))

	plt.plot(FP, TP, color="red")
	plt.xlabel("False Positive")
	plt.ylabel("True Positive")

	#Third part
	Nneg = np.count_nonzero(data.target == 1)
	Npos = np.count_nonzero(data.target == 0)

	cov_area = coverage_area(FP, TP, Nneg)
	rank_error = ranking_error(probs_and_target, Npos, Nneg)

	print("Coverage area: ", cov_area)
	print("Ranking error: ", rank_error)
	print("1 - Coverage area: ",1-((cov_area)/(Nneg * Npos)))
	print("Check Ranking error equal 1 - Coverage area : ", 1-((cov_area)/(Nneg * Npos))==rank_error)
	plt.show()


if __name__ == '__main__':
	main()