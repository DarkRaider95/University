from numpy import ndarray, zeros
import numpy as np
import scipy
from scipy.sparse import csr_matrix
import math
from sklearn.naive_bayes import MultinomialNB, BernoulliNB

class_labels = ["Joy","Sadness"]
n_features=11288 # number of columns in the matrix = number of features (distinct elements in the documents)
n_rows=11981 # number rows of the matrix
n_elements=71474 # number of the existing values in the matrix (not empty, to be loaded in the matrix in a sparse way)

#path_training="/Users/meo/Documents/Didattica/Laboratorio-15-16-Jupyter/"
path_training="./"
file_name="joy_sadness6000.txt"

# declare the row and col arrays with the indexes of the matrix cells (non empty) to be loaded from file
# they are needed because the matrix is sparse and we load in the matrix only the elements which are present
row=np.empty(n_elements, dtype=int)
col=np.empty(n_elements, dtype=int)
data=np.empty(n_elements, dtype=int)

row_n=0 # number of current row to be read and managed
cur_el=0 # position in the arrays row, col and data
twitter_labels=[] # list of class labels (target array) of the documents (twitter) that will be read from the input file
twitter_target=[] # list of 0/1 for class labels
with open(path_training + file_name, "r") as fi:
    for line in fi:
        el_list=line.split(',')
        l=len(el_list)
        last_el=el_list[l-1] # I grab the last element in the list which is the class label
        class_name=last_el.strip() # eliminate the '\n'
        twitter_labels.append(class_name)
        # twitter_labels contains the labels (Joy/Sadness); twitter_target contains 0/1 for the respective labels
        if (class_name==class_labels[0]):
           twitter_target.append(0)
        else:
           twitter_target.append(1)
        i=0 # I start reading all the doc elements from the beginning of the list
        while(i<(l-1)):
            element_id=int(el_list[i]) # identifier of the element in the document
            element_id=element_id-1 # the index starts from 0 (the read id starts from 1)
            i=i+1
            value_cell=int(el_list[i]) # make access to the following value in the file which is the count of the element in the documento 
            i=i+1
            row[cur_el]=row_n # load the data in the three arrays: the first two are the row and col indexes; the last one is the matrix cell value
            col[cur_el]=element_id
            data[cur_el]=value_cell
            cur_el=cur_el+1
        row_n=row_n+1
fi.close
print("final n_row="+str(row))
# loads the matrix by means of the indexes and the values in the three arrays just filled
twitter_data=csr_matrix((data, (row, col)), shape=(n_rows, n_features)).toarray()
print("resulting matrix:")
print(twitter_data)
print(twitter_labels)
print(twitter_target)


#Dividing Dataset

np.random.seed(0)
indices = np.random.permutation(len(twitter_data))

n_test = int(math.floor(len(twitter_data)*0.2))

print(n_test)

indices_training=indices[:-n_test]
indices_test=indices[-n_test:]

#print(indices_training)
#print(len(twitter_data))
#print(len(twitter_target))
#print(twitter_target[2,4,6])

twitter_X_train = twitter_data[indices_training]
twitter_Y_train = [twitter_target[i] for i in indices_training]#twitter_target[indices_training]
twitter_X_test  = twitter_data[indices_test]
twitter_Y_test  = [twitter_target[i] for i in indices_test]#twitter_target[indices_test]

#Multivariate Bernoulli
clf1 = BernoulliNB(binarize=1.0)
clf1.fit(twitter_X_train, twitter_Y_train)

#Multinomial Bernoulli
clf2=MultinomialNB()
clf2.fit(twitter_X_train, twitter_Y_train)

hit1=0
hit2=0

for i in range(0,len(twitter_X_test)):
    y1 = clf1.predict(twitter_X_test[i].reshape(1,-1))
    y2 = clf2.predict(twitter_X_test[i].reshape(1,-1))
    
    if(y1 == twitter_Y_test[i]):
        hit1+=1
    
    if(y2 == twitter_Y_test[i]):
        hit2+=1
        
print("Accuracy Multivariate: ", hit1/len(twitter_Y_test))
print("Accuracy Multinomial: ", hit2/len(twitter_Y_test))