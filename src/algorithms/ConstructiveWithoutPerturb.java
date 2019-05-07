package algorithms;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.Timer;
import structure.PCPDInstance;
import structure.PCPDSolution;
import structure.Pareto;

import java.util.ArrayList;
import java.util.Calendar;

public class ConstructiveWithoutPerturb implements Algorithm<PCPDInstance> {
    private ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives;
    private ArrayList<Improvement<PCPDSolution>> ls;
    // private CountDownLatch latch;
    // private ReentrantLock lock;
    private int iters;
    private int constructions;

    public ConstructiveWithoutPerturb(ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives, ArrayList<Improvement<PCPDSolution>> ls, int iters, int constructions) {
        this.constructives = constructives;
        this.ls = ls;
        this.iters = iters;
        this.constructions = constructions;
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
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String date = String.format("%04d-%02d-%02d", year, month, day);
        int consreal = 1;

        for (int i = 0; i < iters; i++) {
            int constructive = 1;
            for (Constructive<PCPDInstance, PCPDSolution> c : constructives) {
                for (int j = 0; j < constructions / 4; j++) {
                    int thN = j;
                    int cons = consreal;
                    //pool.submit(() -> {
                    PCPDSolution sol = null;
                    sol = c.constructSolution(instance);
                    sol.updateMinDistanceOutToIn();
                    sol.updateMaxDistanceBetweenSelected();
                    //lock.lock();
                    Pareto.add(sol);

                    PCPDSolution solLS1FO1 = new PCPDSolution(sol);
                    PCPDSolution solLS2FO1 = new PCPDSolution(sol);
                    PCPDSolution solLS1FO2 = new PCPDSolution(sol);
                    PCPDSolution solLS2FO2 = new PCPDSolution(sol);

                    /*ls.get(0).improve(solLS1FO1);
                    ls.get(1).improve(solLS1FO1);

                    ls.get(0).improve(solLS2FO1);
                    ls.get(1).improve(solLS2FO1);

                    ls.get(0).improve(solLS1FO2);
                    ls.get(1).improve(solLS1FO2);

                    ls.get(0).improve(solLS2FO2);
                    ls.get(1).improve(solLS2FO2);*/


                    //lock.unlock();
                    //latch.countDown();
                    // });
                }
                consreal++;
                constructive++;

            }
        }

        Pareto.saveToFile("experiments/" + date + "/" + this.toString() + "_large_reduced_075/pareto_" + instance.getName());
        double secs = Timer.getTime() / 1000.0;
        r.add("Time (s)", secs);
        System.out.println("Pareto: " + Pareto.toText());
        System.out.println("Time: " + secs);
        return r;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }

    @Override
    public String toString(){
        StringBuilder stb = new StringBuilder();
        Constructive<PCPDInstance, PCPDSolution> c = constructives.get(0);


        stb.append(c.getClass().getSimpleName());

        return stb.toString();
    }
}
