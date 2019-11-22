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

import java.sql.Time;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelIteratedGreedyRandomPert implements Algorithm<PCPDInstance> {
    private ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives;
    private ArrayList<Improvement<PCPDSolution>> ls;
    private CountDownLatch latch;
    private int iters;
    private int constructions;
    private double perturbationPercentage;
    private Random rnd;

    private class Candidate {
        int id;
        int cost;

        public Candidate(int id, int cost) {
            this.id = id;
            this.cost = cost;
        }
    }

    public ParallelIteratedGreedyRandomPert(ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives, ArrayList<Improvement<PCPDSolution>> ls, int iters, int constructions, double perturbationPercentage) {
        this.constructives = constructives;
        this.ls = ls;
        this.iters = iters;
        this.constructions = constructions;
        this.perturbationPercentage = perturbationPercentage;

    }

    @Override
    public Result execute(PCPDInstance instance) {
        this.rnd = new Random(RandomManager.getRandom().nextInt());
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Pareto.reset();
        System.out.print(instance.getName() + "\n");
        Result r = new Result(instance.getName());
        Timer.initTimer(1800 * 1000);

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String date = String.format("%04d-%02d-%02d", year, month, day);
        CountDownLatch latchConst = new CountDownLatch(constructions * constructives.size());
        for (Constructive<PCPDInstance, PCPDSolution> c : constructives) {
            for (int j = 0; j < constructions; j++) {

                PCPDSolution sol = c.constructSolution(instance);
                Pareto.add(sol);
                pool.submit(() -> {
                    PCPDSolution solLocal = new PCPDSolution(sol);
                    ls.get(0).improve(solLocal);
                    latchConst.countDown();
                });
                pool.submit(() -> {
                    PCPDSolution solLocal = new PCPDSolution(sol);
                    ls.get(1).improve(solLocal);
                    latchConst.countDown();
                });
            }

        }
        try {
            latchConst.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Termino de construir "+ Timer.getTime()/1000 + "s");
        Pareto.saveToFile("experiments/" + date + "/" + this.toString() + "/Constructivo/pareto_" + instance.getName());

        int i = 0;
        while (i <= iters) {
            //Volcar pareto y hacer perturbaciones
            List<PCPDSolution> paretoFront = new ArrayList<>();
            for (PCPDSolution solu : Pareto.getFront()) {
                paretoFront.add(new PCPDSolution(solu));
            }
            CountDownLatch latchIG = new CountDownLatch(paretoFront.size() * (ls.size()-2)*2);


            for (PCPDSolution sol : paretoFront) {
                PCPDSolution[] perturbedSolutions = perturbeSol(sol);
                Pareto.add(perturbedSolutions[0]);
                Pareto.add(perturbedSolutions[1]);

                pool.submit(() -> {
                    PCPDSolution sol1FA_025 = new PCPDSolution(perturbedSolutions[0]);
                    ls.get(3).improve(sol1FA_025);
                    Pareto.add(sol1FA_025);
                    latchIG.countDown();
                });

                pool.submit(() -> {
                    PCPDSolution sol1FA_0 = new PCPDSolution(perturbedSolutions[0]);
                    ls.get(2).improve(sol1FA_0);
                    Pareto.add(sol1FA_0);
                    latchIG.countDown();
                });
                pool.submit(() -> {
                    PCPDSolution sol1FA_05 = new PCPDSolution(perturbedSolutions[0]);
                    ls.get(4).improve(sol1FA_05);
                    Pareto.add(sol1FA_05);
                    latchIG.countDown();
                });

                pool.submit(() -> {
                    PCPDSolution sol1FA_075 = new PCPDSolution(perturbedSolutions[0]);
                    ls.get(5).improve(sol1FA_075);
                    Pareto.add(sol1FA_075);
                    latchIG.countDown();
                });

                pool.submit(() -> {
                    PCPDSolution sol1FA_1 = new PCPDSolution(perturbedSolutions[0]);
                    ls.get(6).improve(sol1FA_1);
                    Pareto.add(sol1FA_1);
                    latchIG.countDown();
                });

                pool.submit(() -> {
                    PCPDSolution sol2FA_025 = new PCPDSolution(perturbedSolutions[1]);
                    ls.get(3).improve(sol2FA_025);
                    Pareto.add(sol2FA_025);
                    latchIG.countDown();
                });

                pool.submit(() -> {
                    PCPDSolution sol2FA_0 = new PCPDSolution(perturbedSolutions[1]);
                    ls.get(2).improve(sol2FA_0);
                    Pareto.add(sol2FA_0);
                    latchIG.countDown();
                });
                pool.submit(() -> {
                    PCPDSolution sol2FA_05 = new PCPDSolution(perturbedSolutions[1]);
                    ls.get(4).improve(sol2FA_05);
                    Pareto.add(sol2FA_05);
                    latchIG.countDown();
                });

                pool.submit(() -> {
                    PCPDSolution sol2FA_075 = new PCPDSolution(perturbedSolutions[1]);
                    ls.get(5).improve(sol2FA_075);
                    Pareto.add(sol2FA_075);
                    latchIG.countDown();
                });

                pool.submit(() -> {
                    PCPDSolution sol2FA_1 = new PCPDSolution(perturbedSolutions[1]);
                    ls.get(6).improve(sol2FA_1);
                    Pareto.add(sol2FA_1);
                    latchIG.countDown();
                });

            }
            try {
                latchIG.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            //Pareto.saveToFile("/Users/sergio/Proyectos IntelliJ/PCPD/experiments/"+this.toString()+"/pareto_"+instance.getName());

            if (Timer.timeReached()) {
                break;
            }

            if (Pareto.isModifiedSinceLastAsk()) {
                i = 0;
            } else {
                i++;
            }
        }


        pool.shutdown();

        try{
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        double secs = Timer.getTime() / 1000.0;
        Pareto.saveToFile("experiments/" + date + "/" + this.toString() + "TrasPerturbar/pareto_" + instance.getName());

        r.add("Time (s)", secs);


        //System.out.println("Sol: "+best+"\tMin distance Out to in: "+best.getMinDistanceOutToIn()+"\tMax distance between selected: "+best.getMaxDistanceBetweenSelected()+"\t"+best.getSelectedNodes()+"\t"+secs+"\t");
        System.out.println("Pareto: " + Pareto.toText());
        System.out.println("Time: " + secs);
        return r;
    }

    private PCPDSolution[] perturbeSol(PCPDSolution sol) {

        int p = sol.getInstance().getP();
        PCPDSolution[] toRet;
        List<Integer> selected = new ArrayList<>(sol.getSelectedNodes());

        for (int i = 0; i < Math.ceil(perturbationPercentage * p); i++) {
            //DESTRUCCIÓN
            int ran2Index = rnd.nextInt(p - 1);
            int ran2 = selected.get(ran2Index);
            sol.removeFromSelectedNodes(ran2);
        }

        toRet = reconstruct(new PCPDSolution(sol));


        return toRet;
    }

    private PCPDSolution[] reconstruct(PCPDSolution sol) {
//
//        ArrayList<Integer> candidatesFO1 = (ArrayList<Integer>) createCandidateListRandom(sol);
//        ArrayList<Integer> candidatesFO2 = (ArrayList<Integer>) createCandidateListRandom(sol);

        ArrayList<Candidate> candidatesFO1 = (ArrayList<Candidate>) createCandidateListToF1(sol);
        ArrayList<Candidate> candidatesFO2 = (ArrayList<Candidate>) createCandidateListToF2(sol);

        PCPDSolution solToImproveWithLS1 = new PCPDSolution(sol);
        PCPDSolution solToImproveWithLS2 = new PCPDSolution(sol);
        int i = 0;

        while (!solToImproveWithLS1.esFactible()) {
            solToImproveWithLS1.addToSelectedNodes(candidatesFO1.get(i).id);
            i++;
        }

        i = 0;
        while (!solToImproveWithLS2.esFactible()) {
            solToImproveWithLS2.addToSelectedNodes(candidatesFO2.get(i).id);
            i++;
        }


        PCPDSolution[] reconstruidas = new PCPDSolution[2];
        reconstruidas[0] = solToImproveWithLS1;
        reconstruidas[1] = solToImproveWithLS2;

//        solToImproveWithLS1.updateMaxDistanceBetweenSelected();
//        solToImproveWithLS1.updateMinDistanceOutToIn();
//
//        solToImproveWithLS2.updateMaxDistanceBetweenSelected();
//        solToImproveWithLS2.updateMinDistanceOutToIn();

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

    private List<Integer> createCandidateListRandom(PCPDSolution sol) {
        int n = sol.getInstance().getN();
        List<Integer> cl = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(cl, rnd);
        return cl;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }

    @Override
    public String toString() {
       /* StringBuilder stb = new StringBuilder();
        for (Constructive<PCPDInstance, PCPDSolution> c : constructives) {
            stb.append(c.getClass().getSimpleName()).append(", ");
        }
        stb.setLength(stb.length() - 2);*/
        //return this.getClass().getSimpleName() + "(" + stb.toString() + "," + ls + "," + iters + "," + constructions + ", " + perturbationPercentage + ")";
        return this.getClass().getSimpleName() + "(" + iters + "," + constructions + ", " + perturbationPercentage + ")";
    }
}
