package algorithms;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import structure.PCPDInstance;
import structure.PCPDSolution;
import structure.Pareto;

import java.util.*;

public class IteratedGreedyOnlyConst implements Algorithm<PCPDInstance> {
    private ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives;
    private ArrayList<Improvement<PCPDSolution>> ls;
    // private CountDownLatch latch;
    // private ReentrantLock lock;
    private int iters;
    private int constructions;
    private double perturbationPercentage;

    private class Candidate {
        int id;
        int cost;

        public Candidate(int id, int cost) {
            this.id = id;
            this.cost = cost;
        }
    }

    public IteratedGreedyOnlyConst(ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives, ArrayList<Improvement<PCPDSolution>> ls, int iters, int constructions) {
        this.constructives = constructives;
        this.ls = ls;
        this.iters = iters;
        this.constructions = constructions;
        this.perturbationPercentage = perturbationPercentage;
    }

    @Override
    public Result execute(PCPDInstance instance) {
        //ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Pareto.reset();
        System.out.print(instance.getName() + "\n");
        Result r = new Result(instance.getName());
        Timer.initTimer(1800 * 1000);
        //latch = new CountDownLatch(constructions);
        //lock = new ReentrantLock();
        int consreal = 1;


        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String date = String.format("%04d-%02d-%02d", year, month, day);

        int constructive = 1;
        for (Constructive<PCPDInstance, PCPDSolution> c : constructives) {
            for (int j = 0; j < constructions; j++) {
                int thN = j;
                int cons = consreal;
                //pool.submit(() -> {
                PCPDSolution sol = null;
                sol = c.constructSolution(instance);
                //lock.lock();
                Pareto.add(sol);
                //TODO: Aplicar a cada solución su LS correspondiente

                PCPDSolution solLS1FO1 = new PCPDSolution(sol);
                PCPDSolution solLS2FO1 = new PCPDSolution(sol);
                PCPDSolution solLS1FO2 = new PCPDSolution(sol);
                PCPDSolution solLS2FO2 = new PCPDSolution(sol);

                ls.get(0).improve(solLS1FO1);
                ls.get(1).improve(solLS2FO1);

                ls.get(0).improve(solLS1FO2);
                ls.get(1).improve(solLS2FO2);

                ls.get(0).improve(solLS2FO1);
                ls.get(1).improve(solLS2FO2);

                ls.get(0).improve(solLS1FO1);
                ls.get(1).improve(solLS1FO2);
            }
            Pareto.saveToFile("experiments/" + date + "/" + this.toString() + "/Constructivo/pareto_"+ c.toString() + instance.getName());
            consreal++;
            constructive++;

        }

        System.out.println("TERMINO DE CONSTRUIR");
        Pareto.saveToFile("experiments/" + date + "/" + this.toString() + "/Constructivo/pareto_" + instance.getName());


        double secs = Timer.getTime() / 1000.0;

        r.add("Time (s)", secs);


        //System.out.println("Sol: "+best+"\tMin distance Out to in: "+best.getMinDistanceOutToIn()+"\tMax distance between selected: "+best.getMaxDistanceBetweenSelected()+"\t"+best.getSelectedNodes()+"\t"+secs+"\t");
        System.out.println("Pareto: " + Pareto.toText());
        System.out.println("Time: " + secs);
        return r;
    }

    private PCPDSolution[] reconstruct(PCPDSolution sol) {

        int p = sol.getInstance().getP();

        ArrayList<Candidate> candidatesFO1 = (ArrayList<Candidate>) createCandidateListToF1(sol);
        ArrayList<Candidate> candidatesFO2 = (ArrayList<Candidate>) createCandidateListToF2(sol);

        PCPDSolution solToImproveWithLS1 = new PCPDSolution(sol);
        PCPDSolution solToImproveWithLS2 = new PCPDSolution(sol);

        while (!solToImproveWithLS1.esFactible()) solToImproveWithLS1.addToSelectedNodes(candidatesFO1.remove(0).id);
        while (!solToImproveWithLS2.esFactible()) solToImproveWithLS2.addToSelectedNodes(candidatesFO2.remove(0).id);


        PCPDSolution[] reconstruidas = new PCPDSolution[2];
        reconstruidas[0] = solToImproveWithLS1;
        reconstruidas[1] = solToImproveWithLS2;

        solToImproveWithLS1.updateMaxDistanceBetweenSelected();
        solToImproveWithLS1.updateMinDistanceOutToIn();

        solToImproveWithLS2.updateMaxDistanceBetweenSelected();
        solToImproveWithLS2.updateMinDistanceOutToIn();

        Pareto.add(solToImproveWithLS1);
        Pareto.add(solToImproveWithLS2);

        return reconstruidas;

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

    private List<Candidate> createCandidateListToF2(PCPDSolution sol) {
        int n = sol.getInstance().getN();
        List<Candidate> cl = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            if (sol.isSelected(i)) continue;
            Candidate c = new Candidate(i, sol.minimumDistanceBetweenSelected(i));
            cl.add(c);
        }

        cl.sort((c1, c2) -> Integer.compare(c2.cost, c1.cost));

        return cl;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }

    @Override
    public String toString() {
        /*StringBuilder stb = new StringBuilder();
        /*for (Constructive<PCPDInstance, PCPDSolution> c : constructives) {
            stb.append(c.getClass().getSimpleName()).append(", ");
        }

        stb.setLength(stb.length() - 2);
         */
        //return this.getClass().getSimpleName() + "(" + stb.toString() + "," + ls + "," + iters + "," + constructions + ", " + perturbationPercentage + ")";
        return this.getClass().getSimpleName() + "(" + iters + "," + constructions + ", " + perturbationPercentage + "," + constructives.get(0) + ")";
    }
}
