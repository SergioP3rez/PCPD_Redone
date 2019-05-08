package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import structure.PCPDInstance;
import structure.PCPDSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ConstructiveGRASPGreedyRandomFO2 implements Constructive<PCPDInstance, PCPDSolution> {

    private class Candidate {
        int id;
        int cost;

        public Candidate(int id, int cost) {
            this.id = id;
            this.cost = cost;
        }
    }

    private double alpha;

    public ConstructiveGRASPGreedyRandomFO2(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public PCPDSolution constructSolution(PCPDInstance instance) {
        Random rnd = RandomManager.getRandom();
        PCPDSolution sol = new PCPDSolution(instance);
        int n = sol.getInstance().getN();

        int firstSelected = rnd.nextInt(n);
        sol.addToSelectedNodes(firstSelected);

        List<Candidate> cl = createCandidateListToF2(sol);

        //Inicio GRASP
        double realAlpha = ((alpha >= 0) ? alpha : rnd.nextDouble());

        while (!sol.esFactible()) {
            double gmax = cl.get(0).cost;
            double gmin = cl.get(cl.size() - 1).cost;
            double th = gmax - realAlpha * (gmax - gmin);
            int limit = 0;

            while (limit < cl.size() && th <= cl.get(limit).cost ) {
                limit++;
            }

            int selected = rnd.nextInt(limit);
            Candidate c = cl.remove(selected);

            int nodeToSelect = c.id;
            sol.addToSelectedNodes(nodeToSelect);
            for (Candidate candidate : cl) {
                candidate.cost = sol.minimumDistanceBetweenSelected(candidate.id);
            }
            cl.sort((c1, c2 )-> Integer.compare(c2.cost, c1.cost));

        }
        System.out.println(sol);
        return sol;
    }


    private List<Candidate> createCandidateListToF2(PCPDSolution sol) {
        int n = sol.getInstance().getN();
        List<Candidate> cl = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            if (sol.isSelected(i)) continue;
            Candidate c = new Candidate(i, sol.minimumDistanceBetweenSelected(i));
            cl.add(c);
        }

        cl.sort((c1, c2 )-> Integer.compare(c2.cost, c1.cost));

        return cl;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+", "+alpha;
    }

}
