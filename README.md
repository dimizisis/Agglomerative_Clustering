# agglomerative-clustering

## Decomposing Modules Using an Agglomerative Clustering Technique

The clustering algorithm that we use for the module decomposition is the agglomerative algorithm, a type of Hierarchical Clustering. In general, Hierarchical Clustering seeks to build a hierarchy of clusters and is based on the core idea of entities being more related to nearby entities than to entities farther away. As such, these algorithms connect entities to form clusters based on their distance. A cluster can be described largely by the maximum distance needed to connect parts of the cluster. 

The Agglomerative Clustering algorithm we use works as follows: Firstly, it assigns each entity to a single cluster. In each iteration, it merges the two clusters with the minimum distance. Finally, the algorithm terminates when all entities are contained in a single cluster.

In order to be able to decide the actual clusters we must pick a threshold value for the minimum distance as a cut-off value. The hierarchy of the clusters is usually represented by a dendrogram (Figure). The leaves of the tree represent the entities, the root is the final cluster and the intermediate nodes are the actual clusters. The height of the tree represents the different levels of the distance threshold in which two clusters were merged.

There are plenty of methods to pick the closest clusters. We chose the Average Linkage method, in which the distance between one cluster and another cluster is considered to be equal to the average distance from any member of one cluster to any member of the other cluster.

As for the threshold (cut-off) value for the minimum distance, we do not define a fixed one, but we apply the agglomerative clustering algorithm for a range of threshold values (from 0.1 to 1.0) and we present the results. We’ve observed that higher thresholds (ranging from 0.85 to 1.0) generally produce better results than lower ones.

The distance metric we chose to use is the Jaccard Distance, which produces decent results in software remodularization. To define the Jaccard Distance between two procedures, we use the notion of entity sets. According to this notion, the entity set of a procedure contains all procedures (subroutines & functions) that are invoked by the procedure, all attributes that are accessed by it and the procedure itself.

Thus, having defined the notion of entity sets, we calculate the Jaccard Distance between two entity sets A and B as shown below:


Jaccard_Distance(A,B)= 1 - |A ∩ B| / |A ∪ B|          (1)

To better comprehend the above methodology, we will exemplify it, giving an example, presented below. In this example, we have a module with 4 attributes:

	a1 = id
	a2 = email
	a3 = password
	a4 = name

and 4 procedures:

	p1 = change_email
	p2 = get_email
	p3 = init_user
	p4 = delete_user

![alt text](https://i.imgur.com/VOOpb9m.png?raw=true "Figure 1: Source Code of the Example")

Table 1 represents the distance matrix for this example. 

![alt text](https://i.imgur.com/DTcg4ZS.png?raw=true "Table 1: Distance Matrix of the Module of Figure 1")

![alt text](https://i.imgur.com/QgVS42N.png?raw=true "Figure 2: Dendrogram Resulting from the Application of Agglomerative Clustering Algorithm for the Module of Figure 1.")


Figure 2 shows the dendrogram produced by the clustering algorithm. Dendrogram helps us to define the number of clusters, given a threshold value for the minimum distance among entities.


![alt text](https://i.imgur.com/ZAFW01v.png?raw=true "Figure 3: Graph corresponding to the Module of Figure 1.")

Figure 3 indicates a graph representation of our module. The four nodes represent the module’s procedures. It is comprehensible from the graph that there are two discrete clusters shown in circles. We were able to identify both clusters using the Agglomerative Clustering algorithm and a distance threshold value of 0.7.
According to the two preconditions required for assuring a certain degree of functionality, the module proposed to be extracted should:

1. contain at least one procedure

2. have direct access to the attributes of initial module (those attributes may need to be transferred to the new module)

## Instructions

### Python

This script finds the first CSV available in the same directory & takes it as input. Try run it with the example CSV provided.

Any version of Python3 is mandatory.

```
pip install -r requirements.txt
```

```
python AgglomerativeClustering.py <minimum_distance_threshold_value>
```

### Java

JDK version 11+ is mandatory.

It is a Maven project, just import the java/Clustering directory in an IDE (i.e. Eclipse, Intellij IDEA). In the current version, CSV file path must be set by initializing the PATH variable, inside the Main class.
