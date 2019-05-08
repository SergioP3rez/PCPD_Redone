package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import structure.PCPDInstance;
import structure.PCPDSolution;
import structure.Pareto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConstructiveGRASPRandomGreedyFO2 implements Constructive<PCPDInstance, PCPDSolution>{

    private double alpha;

    public ConstructiveGRASPRandomGreedyFO2(double alpha) {
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

        while (!sol.esFactible()) {
            int numberOfCandidates = (int) Math.ceil(realAlpha * cl.size());
            int nodeToSelect = 0;

            int maxPromissing = Integer.MIN_VALUE;
            int actualMax;

            for (int i = 0; i < numberOfCandidates; i++) {
                int idNodeToSelect = rnd.nextInt(cl.size());
                actualMax = sol.minimumDistanceBetweenSelected(i);
                if(actualMax > maxPromissing){
                    nodeToSelect = cl.get(idNodeToSelect);
                    maxPromissing = actualMax;
                }
            }
            cl.remove(Integer.valueOf(nodeToSelect));
            sol.addToSelectedNodes(nodeToSelect);
        }

        Pareto.add(sol);
        return sol;
    }
}
