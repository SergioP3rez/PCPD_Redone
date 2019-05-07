package structure;

import grafo.optilib.structure.Instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PCPDInstance implements Instance {
    private String name;
    private List<Integer> locations;
    private int n;
    private int m;
    private Set<Integer> graph;
    private int p;
    private int[][] distances;

    public PCPDInstance(String path) {
        readInstance(path);
    }

    @Override
    public void readInstance(String path) {
        try {
            name = path.substring(path.lastIndexOf('/') + 1);
            BufferedReader bf = new BufferedReader(new FileReader(path));
            String line = bf.readLine();
            String[] tokens = line.split("\\s+");
            n = Integer.parseInt(tokens[0]);
            m = Integer.parseInt(tokens[1]);
            p = Integer.parseInt(tokens[2]);
            locations = new ArrayList<>(p);
            distances = new int[n][n];

            while ((line = bf.readLine()) != null) {
                tokens = line.split("\\s+");
                distances[Integer.parseInt(tokens[0])-1][Integer.parseInt(tokens[1])-1] = Integer.parseInt(tokens[2]);
                distances[Integer.parseInt(tokens[1])-1][Integer.parseInt(tokens[0])-1] = Integer.parseInt(tokens[2]);
            }
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getP() {
        return p;
    }

    public String getName() {
        return name;
    }

    public int[][] getDistances() {
        return distances;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append("n: ").append(n).append("\n");
        stb.append("p: ").append(p).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                stb.append(distances[i][j]).append(" ");
            }
            stb.append("\n");
        }
        return stb.toString();
    }
}
