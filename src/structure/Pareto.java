package structure;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Pareto {

    private static List<PCPDSolution> front;
    private static boolean modifiedSinceLastAsk;

    public static void reset() {
        front = new ArrayList<>(1000);
        modifiedSinceLastAsk = false;
    }

    public static int compareFloat(float f1, float f2) {
        if (Math.abs(f1-f2) < 0.0001) {
            return 0;
        } else if (f1 < f2) {
            return -1;
        } else {
            return +1;
        }
    }

    public synchronized static boolean add(PCPDSolution newSol) {
        //System.out.println("newSol: minDistanceInOut ->  "+newSol.getMinDistanceOutToIn()+" maxDistanceBetween -> "+newSol.getMaxDistanceBetweenSelected());
        List<Integer> dominated = new ArrayList<>();
        boolean enter = true;
        int idx = 0;
        for (PCPDSolution frontSol : front) {
            int compMin = Integer.compare(newSol.getMaxDistanceBetweenSelected(), frontSol.getMaxDistanceBetweenSelected());
            int compMax = Integer.compare(newSol.getMinDistanceOutToIn(), frontSol.getMinDistanceOutToIn());


            if (compMin <= 0 && compMax >= 0) {
                // newSol esta dominada por una ya incluida en el frente
                enter = false;
                break;
            } else if (compMin >= 0 && compMax <= 0) {
                // newSol domina a la incluida
                dominated.add(idx);
            }
            idx++;
        }
        int removed = 0;
        for (int idRem : dominated) {
            front.remove(idRem-removed);
            removed++;
        }
        if (enter) {
            front.add(new PCPDSolution(newSol));
            modifiedSinceLastAsk = true;
        }

        return enter;
    }

    public static synchronized boolean isModifiedSinceLastAsk() {
        boolean ret = modifiedSinceLastAsk;
        modifiedSinceLastAsk = false;
        return ret;
    }

    public static synchronized List<PCPDSolution> getFront(){
        return front;
    }

    public static String toText() {
        StringBuilder stb = new StringBuilder();
        for (PCPDSolution sol : front) {
           stb.append(sol.getSelectedNodes()+"\t").append("MinDistanceOutToIn: "+sol.getMinDistanceOutToIn()+"\t").append("MaxDistBetweenSelected: "+sol.getMaxDistanceBetweenSelected()).append("\n");
        }
        return stb.toString();
    }

    public static String toText2() {
        StringBuilder stb = new StringBuilder();
        for (PCPDSolution sol : front) {
            stb.append(sol.getMinDistanceOutToIn() + "\t"+ sol.getMaxDistanceBetweenSelected()).append("\n");
        }
        return stb.toString();
    }

    public static void saveToFile(String path) {
        if (path.lastIndexOf('/') > 0) {
            File folder = new File(path.substring(0, path.lastIndexOf('/')));
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        try {
            PrintWriter pw = new PrintWriter(path);
            pw.print(toText());
            pw.close();
            PrintWriter pw2 = new PrintWriter(path.replaceAll("\\.txt","_onlyPareto.txt"));
            pw2.print(toText2());
            pw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
