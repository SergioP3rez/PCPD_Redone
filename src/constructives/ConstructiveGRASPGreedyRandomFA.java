package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import structure.PCPDInstance;
import structure.PCPDSolution;
import utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ConstructiveGRASPGreedyRandomFA implements Constructive<PCPDInstance, PCPDSolution> {

    private class Candidate {
        int id;
        double cost;

        public Candidate(int id, double cost) {
            this.id = id;
            this.cost = cost;
        }
    }

    private double alpha;
    private double alphaFA;
    private Random rnd;

    public ConstructiveGRASPGreedyRandomFA(double alpha, double alphaFA) {
        this.alpha = alpha;
        this.alphaFA = alphaFA;
        this.rnd = new Random(RandomManager.getRandom().nextInt());
    }

    @Override
    public PCPDSolution constructSolution(PCPDInstance instance) {
        PCPDSolution sol = new PCPDSolution(instance);
        int n = sol.getInstance().getN();

        int firstSelected = rnd.nextInt(n);
        sol.addToSelectedNodes(firstSelected);

        List<Candidate> cl = createCandidateListToFA(sol);

        //Inicio GRASP
        double realAlpha = ((alpha >= 0) ? alpha : rnd.nextDouble());
        double realAlphaFA = ((alphaFA >= 0) ? alphaFA : rnd.nextDouble());

        while (!sol.esFactible()) {
            double gmin = cl.get(0).cost;
            double gmax = cl.get(cl.size() - 1).cost;
            double th = gmin + realAlpha * (gmax - gmin);
            int limit = 0;

            while (limit < cl.size() && cl.get(limit).cost <= th) {
                limit++;
            }

            int selected = rnd.nextInt(limit);
            Candidate c = cl.remove(selected);

            int nodeToSelect = c.id;
            sol.addToSelectedNodes(nodeToSelect);
            //sol.getMaxDistanceBetweenSelected();
            for (Candidate candidate : cl) {
                sol.addToSelectedNodes(candidate.id);
                candidate.cost = realAlphaFA * sol.getMinDistanceOutToIn() - (1 - realAlphaFA) * sol.getMaxDistanceBetweenSelected();
                sol.removeFromSelectedNodes(candidate.id);
            }
            sol.getMaxDistanceBetweenSelected();
            cl.sort(Comparator.comparingDouble(cd -> cd.cost));

        }
//        System.out.println(sol);
        return sol;
    }


    private List<Candidate> createCandidateListToFA(PCPDSolution sol) {
        int n = sol.getInstance().getN();
        List<Candidate> cl = new ArrayList<>(n);
        Random rnd = RandomManager.getRandom();
        double realAlphaFA = ((alphaFA >= 0) ? alphaFA : rnd.nextDouble());
        for (int i = 0; i < n; i++) {
            if (sol.isSelected(i)) continue;
            sol.addToSelectedNodes(i);
            double newFA = realAlphaFA * sol.getMinDistanceOutToIn() - (1 - realAlphaFA) * sol.getMaxDistanceBetweenSelected();
            cl.add(new Candidate(i, newFA));
            sol.removeFromSelectedNodes(i);
        }

//        cl.sort((c1, c2 )-> Utils.compareDouble(c2.cost, c1.cost));
        cl.sort(Comparator.comparingDouble(c -> c.cost));
        return cl;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ", " + alpha+", FA: "+alphaFA;
    }

}
