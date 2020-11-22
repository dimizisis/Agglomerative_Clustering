
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class Main {

    static final String SPLITTER = ",";
    static final String PATH = "";
    static final double THRESHOLD = 0.64;

    public static void main(String[] args) {
        List<Map<?, ?>> data = read_data(PATH, SPLITTER);
        AgglomerativeClustering clustering = new AgglomerativeClustering(data, THRESHOLD);
        clustering.printDistanceMatrix();
        clustering.printEntitySets();

        clustering.performClustering(true);
        clustering.printResults();
    }

    static List<Map<?, ?>> read_data(String path, String splitter){
        List<Map<?, ?>> procedures = new ArrayList<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            br.readLine(); // headers
            while ((line = br.readLine()) != null){
                String[] proc = line.split(splitter);
                Map<String, List<?>> map = new HashMap<>();
                List<String> entityList = new ArrayList<>();
                try {
                    entityList.addAll(asList(proc[1].split(";")));
                    entityList.addAll(asList(proc[2].split(";")));

                    map.put(proc[0], entityList);
                }catch (Exception e1) {
                    try {
                        entityList.addAll(asList(proc[1].split(";")));
                        entityList.add("");

                        map.put(proc[0], entityList);
                    } catch (Exception e2) {
                        entityList.add("");
                        entityList.add("");
                        map.put(proc[0], entityList);
                    }
                }
                procedures.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return procedures;
    }
}
