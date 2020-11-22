
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AgglomerativeClustering {

    static final String OUTPATH = "";

    List<Map<?, ?>> data;
    private double threshold;
    private List<List<?>> entitySets;
    private double[][] distanceMatrix;
    private String[] procNames;
    private List<Cluster> clusters; // for flat clustering (with threshold)
    private Cluster cluster; // for default clustering

    public AgglomerativeClustering(List<Map<?, ?>> data, double threshold) {
        this.data = data;
        this.threshold = threshold;
        this.procNames = getProceduresNames();
        this.entitySets = createEntitySets();
        this.distanceMatrix = createDistanceMatrix();
        this.clusters = null;
        this.cluster = null;
    }

    /**
     * Performs Hierrarchical Agglomerative Clustering, given the distance matrix
     * and a threshold (ranging from 0.1 to 1.0)
     */
    public void performClustering(boolean flat) {
        if (flat) {
            cluster = new DefaultClusteringAlgorithm()
                    .performClustering(distanceMatrix, procNames,
                            new AverageLinkageStrategy());
            return;
        }

        clusters = new DefaultClusteringAlgorithm()
                            .performFlatClustering(distanceMatrix, procNames,
                                    new AverageLinkageStrategy(), threshold);
    }

    /**
     * Prints distance matrix
     */
    public void printDistanceMatrix(){
        System.out.println(Arrays.deepToString(distanceMatrix).replace("], ", "]\n")
                                                              .replace("[[", "[")
                                                              .replace("]]", "]"));
    }

    /**
     * Prints all entity sets (for each procedure)
     */
    public void printEntitySets(){
        System.out.println(entitySets);
    }

    /**
     * Prints results (must be called after the
     * clustering is performed)
     */
    public void printResults(){

        if (clusters == null && cluster == null) {
            System.out.println("Clustering must be performed before printing the results " +
                                "(try calling performClustering() first).");
            return;
        }
        DendrogramPanel dp = createDendrogram();
        if (clusters != null)
            for (Cluster cluster : clusters) {
                printCluster(cluster);
                dp.setModel(cluster);
                saveDendrogramImage(dp, cluster.getName());
            }
        else {
            printCluster(cluster);
            dp.setModel(cluster);
            saveDendrogramImage(dp, cluster.getName());
        }
    }

    /**
     * Creates dendrogram panel, according to
     * agglomerative clustering results (clusters)
     * and returns it.
     *
     * @return dendrogram panel (JPanel)
     */
    private DendrogramPanel createDendrogram(){
        DendrogramPanel dp = new DendrogramPanel();
        dp.setScaleValueDecimals(2);
        return dp;
    }

    /**
     * Prints a cluster's info
     * @param  cluster cluster that its info will be shown
     */
    private void printCluster(Cluster cluster) {
        if (cluster.getChildren().isEmpty()) {
            System.out.println("Cluster: " + cluster.getName() + " with no children");
            return;
        }
        System.out.println("Cluster: " + cluster.getName());
        System.out.println("With Children: " + cluster.getChildren());
    }

    /**
     * Creates entity sets (all attributes and methods invoked plus method name)
     * (creation is done when instance is created)
     */
    private List<List<?>> createEntitySets() {
        List<List<?>> entitySets = new ArrayList<>();
        for (Map<?, ?> m : data){
            String procedure = (String) m.keySet().toArray()[0];
            List<String> set = (List<String>) m.get(procedure);
            set.add(procedure);
            entitySets.add(set);
        }
        return entitySets;
    }

    /**
     * Creates a list containing all the procedures' names
     * and returns it.
     *
     * @return list (string) with procedures' names
     */
    private String[] getProceduresNames() {
        List<Object> proceduresNames = new ArrayList<>();
        for (Map<?, ?> m : data)
            proceduresNames.add(m.keySet().toArray()[0]);
        return proceduresNames.toArray(new String[0]);
    }

    /**
     * Calculates Jaccard's Similarity between two sets
     * and returns the result
     *
     * @param  a the first set
     * @param  b the second set
     *
     * @return double ranging from 0.0 to 1.0
     */
    private double calcJaccardSimilarity(Collection<?> a, Collection<?> b) {
        Collection<Object> intersection = new HashSet<>(a);
        Collection<Object> union = new HashSet<>(a);

        intersection.retainAll(b);
        union.addAll(b);

        return  (double) intersection.size()/ (double) union.size();
    }

    /**
     * Calculates Jaccard's Distance (1-Jaccard's Similarity)
     * between two sets and returns the result
     *
     * @param  a the first set
     * @param  b the second set
     *
     * @return double ranging from 0.0 to 1.0
     */
    private double calcJaccardDistance(Collection<?> a, Collection<?> b) {
        return 1 - calcJaccardSimilarity(a, b);
    }

    /**
     * Creates the distance matrix of given data, using
     * Jaccard's Distance.
     * (calculation is done when instance is created)
     *
     * @return double matrix (nxn) with the distances
     */
    private double[][] createDistanceMatrix() {
        DecimalFormat df2 = new DecimalFormat("#.##");
        int length = procNames.length;
        double distance;
        double[][] matrix = new double[length][length];

        for (int i = 0; i < length; ++i) {
            for (int j = 0; j < length; ++j) {
                if (i == j) {
                    matrix[i][j] = 0.0;
                    continue;
                }
                distance = calcJaccardDistance(entitySets.get(i), entitySets.get(j));
                matrix[i][j] = Double.parseDouble(df2.format(distance)
                                                     .replace(",", "."));
            }
        }
        return matrix;
    }

    /**
     * Creates the dendrogram and saves it as an
     * image. This method is called by printResults()
     * method.
     *
     * @param  panel the JPanel object, containing the dendrogram
     * @param  outname the output file name of the image
     *
     */
    private void saveDendrogramImage(DendrogramPanel panel, String outname){
        BufferedImage imagebuf=null;
        final String format = "JPG";
        try {
            panel.setBounds(new Rectangle(1200, 800));
            imagebuf = new Robot().createScreenCapture(panel.getBounds());
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        Graphics2D graphics2D = imagebuf.createGraphics();
        panel.paint(graphics2D);
        try {
            ImageIO.write(imagebuf,format, new File(OUTPATH + outname + "."+format));
        } catch (Exception e) {
            System.out.println("Error saving the dendrogram!");
        }
        System.out.println("Image containing the dendrogram saved.");
    }
}
