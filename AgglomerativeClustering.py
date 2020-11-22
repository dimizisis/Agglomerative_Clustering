
import pandas as pd
import numpy as np
from sklearn.cluster import AgglomerativeClustering
import pathlib
import sys
import glob

PATH = pathlib.Path(__file__).parent.absolute()
INFILENAME = sys.argv[1]
THRESHOLD = float(sys.argv[1])
LINKAGE_TYPE = 'average'

def read_data():
    '''Reads data from specific sheet of excel file, and returns a Pandas Dataframe.'''
    try:
        # read the first csv file found by glob
        try:
            filename = glob.glob('*.csv')[0]
        except Exception as e:
            print('No CSV files provided!')
            exit(-1)
        procedures = pd.read_csv(f'{PATH}\\{filename}', sep=',', encoding='utf-8')
        procedures.columns = ['Procedure', 'Attributes', 'Invocations']
    except Exception as e:
        print(e)
        exit(-1)
    return procedures.to_dict('records')

def create_lists(procedures):
    '''Converts attributes & invoked methods to lists (each word before a comma will be a list element)'''
    for proc in procedures:
        proc['Attributes'] = proc['Attributes'].split(';') if pd.notnull(proc['Attributes']) else ['']
        proc['Invocations'] = proc['Invocations'].split(';') if pd.notnull(proc['Invocations']) else ['']

def create_entity_sets(procedures):
    '''Creates entity sets. Adds each procedure's name to its entity set, following the methodology's extension'''
    entity_sets = list() 
    [entity_sets.append({'procedure': proc['Procedure'], 'set': [proc['Procedure']] + proc['Attributes'] + proc['Invocations']}) for proc in procedures]
    return entity_sets

def jaccard_similarity(list1, list2):
    '''Jaccard's similarity computation (needed for jaccard's distance computation)'''
    s1 = set(list1)
    s2 = set(list2)
    return len(s1.intersection(s2)) / len(s1.union(s2))

def jaccard_distance(list1, list2):
    '''Jaccard's distance computation'''
    return 1 - jaccard_similarity(list1, list2)

def create_distance_matrix(entity_sets):
    '''Creates the distance matrix from given list of entity sets'''
    names = [es['procedure'] for es in entity_sets]
    matrix = np.ndarray((len(entity_sets), len(entity_sets)))
    for i in range(len(entity_sets)):
        for j in range(len(entity_sets)):
            if i == j:
                matrix[i][j] = 0.0
                continue
            distance = jaccard_distance(entity_sets[i]['set'], entity_sets[j]['set'])
            matrix[i][j] = float("{:.2f}".format(distance))
    return pd.DataFrame(matrix, columns=names, index=names)

def perform_agglomerative_clustering(distance_matrix, cluster_count=None, distance_threshold=None):
    '''Performs agglomerative hirrarchical clustering, given the distance matrix & the cluster count'''
    # Compute full tree must be always true, if distance_threshold is present
    compute_full_tree = True if distance_threshold is not None else False
    # Affinity always precomputed, when we have distance matrix & linkage 'average' or 'complete'
    model = AgglomerativeClustering(affinity='precomputed', compute_full_tree=compute_full_tree, n_clusters=cluster_count, 
                                        linkage=LINKAGE_TYPE, distance_threshold=distance_threshold).fit(distance_matrix)                             
    return model.labels_

def print_results(final_df):
    clusters_no = len(pd.unique(final_df['Cluster']))
    print(clusters_no)
    for i in range(clusters_no):
        for index, row in final_df.iterrows():
            if i == row['Cluster']:
                print(f'{index} ', end='')
        print()

############################################################################################################################################
############################################################################################################################################

# List of dicts, which will contain the exported data (results & dendrograms) for each file
dfs = list()
# For each sheet that contains data
procedures = read_data()
create_lists(procedures)
# Create entity sets
entity_sets = create_entity_sets(procedures)
# Export distance matrix, according to entity sets (Jaccard Distance)
distance_matrix = create_distance_matrix(entity_sets)
# Export model's labels
labels = perform_agglomerative_clustering(distance_matrix, distance_threshold=THRESHOLD)
# Final dataframe
final_df = pd.DataFrame(labels, index=[set['procedure'] for set in entity_sets], columns=['Cluster'])
# Print results in appropriate way
print_results(final_df)
