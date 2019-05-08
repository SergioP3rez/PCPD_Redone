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

public class ConstructiveGRASPRandomGreedyFA implements Constructive<PCPDInstance, PCPDSolution> {

    private double alpha;
    private double alphaFA;

    public ConstructiveGRASPRandomGreedyFA(double alpha, double alphaFA) {
        this.alpha = alpha;
        this.alphaFA = alphaFA;
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
        double realAlphaFA = ((alphaFA >= 0) ? alphaFA : rnd.nextDouble());


        int firstSelected = rnd.nextInt(n);
        int idFirst = cl.remove(firstSelected);

        sol.addToSelectedNodes(idFirst);
        while (!sol.esFactible()) {
            int numberOfCandidates = (int) Math.ceil(realAlpha * cl.size());
            int nodeToSelect = 0;

            double minPromissing = Integer.MAX_VALUE;
            double actualMin;

            for (int i = 0; i < numberOfCandidates; i++) {
                int idNodeToSelect = rnd.nextInt(cl.size());
                sol.addToSelectedNodes(cl.get(idNodeToSelect));
                actualMin = realAlphaFA * sol.getMinDistanceOutToIn() - (1 - realAlphaFA) * sol.getMaxDistanceBetweenSelected();
                if (Utils.compareDouble(actualMin, minPromissing) < 0) {
                    nodeToSelect = cl.get(idNodeToSelect);
                    minPromissing = actualMin;
                }
                sol.removeFromSelectedNodes(cl.get(idNodeToSelect));
            }
            cl.remove(Integer.valueOf(nodeToSelect));
            sol.addToSelectedNodes(nodeToSelect);

        }

        Pareto.add(sol);
        return sol;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ", " + alpha + ", FA: " + alphaFA;
    }
}
