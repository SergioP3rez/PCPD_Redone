package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import structure.PCPDInstance;
import structure.PCPDSolution;
import structure.Pareto;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConstructiveGRASPRandomGreedyFO1 implements Constructive<PCPDInstance, PCPDSolution>{

    private double alpha;
    private int[] distanceToNearestCenter;

    public ConstructiveGRASPRandomGreedyFO1(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public PCPDSolution constructSolution(PCPDInstance instance) {
        Random rnd = RandomManager.getRandom();
        PCPDSolution sol = new PCPDSolution(instance);
        int n = instance.getN();

        List<Integer> cl = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            cl.add(i);
        }

        double realAlpha = ((alpha >= 0) ? alpha : rnd.nextDouble());


        int firstSelected = rnd.nextInt(n);
        int idFirst = cl.remove(firstSelected);

        sol.addToSelectedNodes(idFirst);
        this.distanceToNearestCenter = new int[n];
        for (int i = 0; i < n; i++) {
            distanceToNearestCenter[i] = sol.getInstance().getDistances()[i][firstSelected];
        }

        while (!sol.esFactible()) {
            int numberOfCandidates = (int) Math.ceil(realAlpha * cl.size());
            int nodeToSelect = 0;
            //PARA LA F1
            int minPromissing = Integer.MAX_VALUE;
            int actualMin;
            //FIN PARA LA F1
            for (int i = 0; i < numberOfCandidates; i++) {
                int idNodeToSelect = rnd.nextInt(cl.size());
                actualMin = sol.maxDistanceOutToIn(cl.get(idNodeToSelect), distanceToNearestCenter);
                if(actualMin < minPromissing){
                    nodeToSelect = cl.get(idNodeToSelect);
                    minPromissing = actualMin;
                }
            }
            cl.remove(Integer.valueOf(nodeToSelect));
            sol.addToSelectedNodes(nodeToSelect);
        }

        Pareto.add(sol);
        return sol;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName()+", "+alpha+")";
    }
}
