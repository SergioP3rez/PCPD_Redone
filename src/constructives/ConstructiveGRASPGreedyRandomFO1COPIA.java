package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import structure.PCPDInstance;
import structure.PCPDSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ConstructiveGRASPGreedyRandomFO1COPIA implements Constructive<PCPDInstance, PCPDSolution> {

    private class Candidate {
        int id;
        int cost;

        public Candidate(int id, int cost) {
            this.id = id;
            this.cost = cost;
        }
    }

    private double alpha;
    private int[] distanceToNearestCenter;

    public ConstructiveGRASPGreedyRandomFO1COPIA(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public PCPDSolution constructSolution(PCPDInstance instance) {
        Random rnd = RandomManager.getRandom();
        PCPDSolution sol = new PCPDSolution(instance);
        int n = sol.getInstance().getN();

        int firstSelected = rnd.nextInt(n);
        sol.addToSelectedNodes(firstSelected);
        this.distanceToNearestCenter = new int[n];
        for (int i = 0; i < n; i++) {
            distanceToNearestCenter[i] = sol.getInstance().getDistances()[i][firstSelected];
        }

        List<Candidate> cl = createCandidateListToF1(sol);

        //Inicio GRASP
        double realAlpha = ((alpha >= 0) ? alpha : rnd.nextDouble());

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

            for (Candidate i : cl) {
                int id = i.id;
                int distanceToNewCenter = sol.getInstance().getDistances()[id][nodeToSelect];
                if(distanceToNearestCenter[id] > distanceToNewCenter) distanceToNearestCenter[id] = distanceToNewCenter;
            }

            for (Candidate candidate : cl) {
                sol.addToSelectedNodes(candidate.id);
                candidate.cost = sol.getMinDistanceOutToIn();
                sol.removeFromSelectedNodes(candidate.id);
            }

            cl.sort(Comparator.comparingInt(can -> can.cost));
        }
        return sol;
    }


    private List<Candidate> createCandidateListToF1(PCPDSolution sol) {
        int n = sol.getInstance().getN();
        List<Candidate> cl = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            if (sol.isSelected(i)) continue;
            sol.addToSelectedNodes(i);
            int minDistance = sol.getMinDistanceOutToIn();
            Candidate c = new Candidate(i, minDistance);
            cl.add(c);
            sol.removeFromSelectedNodes(i);
        }

        cl.sort(Comparator.comparingInt(c -> c.cost));

        return cl;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+", "+alpha;
    }

}
