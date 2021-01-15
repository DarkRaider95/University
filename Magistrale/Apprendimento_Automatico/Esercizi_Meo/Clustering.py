import csv
import sys
from os.path import join
from sklearn.metrics import silhouette_score
import numpy as np
from sklearn.cluster import KMeans
import matplotlib.pyplot as plt

def draw(data, n_samples, labels, name, k):
    fig = plt.figure(figsize=(8,8))

    ax = fig.add_subplot(111)
    fig.subplots_adjust(top=1)
    ax.set_title('Clustered points in {}'.format(name))

    ax.set_xlabel('x')
    ax.set_ylabel('y')

    # set the list of colors to be selected when plotting the different clusters
    #color=['b','g','r','c','m','y','k','w']
    color=['b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b']
    #color = ['blue', 'orange', 'green', 'red', 'purple', 'brown', 'pink', 'gray', 'olive','cyan']
    #plot the dataset
    for clu in range(k, 0, -1):
        # collect the sequence of cooordinates of the points in each given cluster (determined by clu)
        print(data[clu].size)
        data_list_x = [data[i,0] for i in range(n_samples) if labels[i]==clu]
        data_list_y = [data[i,1] for i in range(n_samples) if labels[i]==clu]
        plt.scatter(data_list_x, data_list_y, s=2, edgecolors='none', c=color[clu], alpha=0.5)

    plt.show()

def load_data(file_path, file_name):
   with open(join(file_path, file_name)) as csv_file:
       data_file = csv.reader(csv_file,delimiter=',')
       temp1 = next(data_file)
       n_samples = int(temp1[0])
       n_features = int(temp1[1])
       temp2 = next(data_file)
       feature_names = np.array(temp2[:n_features])

       data_list = [iter for iter in data_file]
               
       data = np.asarray(data_list, dtype=np.float64)                  
       
   return(data,feature_names,n_samples,n_features)

def test(k, data, name):
    score = -1
    best_labels = None
    for i in range(0,10):
        kmeans = KMeans(n_clusters=k, random_state=0).fit(data)
        #print(kmeans.labels_)
        prev_score = silhouette_score(data, kmeans.labels_, metric='euclidean',  sample_size=1000)
        #print(prev_score)
        if(score < prev_score):
            best_labels=kmeans.labels_
            score = prev_score
            
    print("Best silhouette score {}: {}".format(name, score))
            
    return best_labels

# The main program reads the input file containing the dataset
# file_path is the file path where the file with the data to be read are located
# we assume the file contains an example per line
# each example is a list of real values separated by a comma (csv format)
# The first line of the file contains the heading with:
# N_samples,n_features,
# The second line contains the feature names separated by a comma     

#file_path="~/meo/Documents/Didattica/Laboratorio-15-16-Jupyter/"
file_path="./Datasets/"
# all the three datasets contain data points on (x,y) 
file_name1="3-clusters.csv"
file_name2="dataset-DBSCAN.csv"     
file_name3="CURE-complete.csv"

#data1,feature_names1,n_samples1,n_features1 = load_data(file_path, file_name1)
#data2,feature_names2,n_samples2,n_features2 = load_data(file_path, file_name2)
data3,feature_names3,n_samples3,n_features3 = load_data(file_path, file_name3)

np.random.seed(5)

#labels1=test(3, data1, "Dataset 1")
#labels2=test(5, data2, "Dataset 2")
labels3=test(8, data3, "Dataset 3")

#draw(data1, n_samples1, labels1, "Dataset 1", 3)
#draw(data2, n_samples2, labels2, "Dataset 2", 5)
draw(data3, n_samples3, labels3, "Dataset 3", 4)