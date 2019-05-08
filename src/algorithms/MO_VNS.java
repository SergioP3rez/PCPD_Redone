package algorithms;

import constructives.VND;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.Timer;
import structure.PCPDInstance;
import structure.PCPDSolution;
import structure.Pareto;
import utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MO_VNS implements Algorithm<PCPDInstance> {
    private ArrayList<Constructive<PCPDInstance, PCPDSolution>> c;
    private Improvement<PCPDSolution> ls;
    private int nConstructions;
    private int itersVNS;
    private double kStep;
    private double kMax;
    private PCPDSolution best;

    public MO_VNS(ArrayList<Constructive<PCPDInstance, PCPDSolution>> c, Improvement<PCPDSolution> ls, int nConstructions, double kStep, double kMax, int itersVNS) {
        this.c = c;
        this.ls = ls;
        this.nConstructions = nConstructions;
        this.kStep = kStep;
        this.kMax = kMax;
        this.itersVNS = itersVNS;
    }

    @Override
    public Result execute(PCPDInstance instance) {
        best = null;
        System.out.println(instance.getName()+"\t");
        Result r = new Result(instance.getName());
        Pareto.reset();
        Timer.initTimer(1800*1000);
        for (Constructive<PCPDInstance, PCPDSolution> constructive : c) {
            for (int i = 0; i < nConstructions; i++) {

                PCPDSolution sol = constructive.constructSolution(instance);
//                System.out.println(sol.getMinDistanceOutToIn()+"\t"+sol.getMaxDistanceBetweenSelected());
                Pareto.add(sol);
            }
        }

        ArrayList<PCPDSolution> paretoConstructive = new ArrayList<>(Pareto.getFront());
        for (PCPDSolution solution : paretoConstructive) {
            ls.improve(new PCPDSolution(solution));
        }

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);
        Pareto.saveToFile("experiments/" + date + "/" + this.toString() + "/pareto_only_constLS" + instance.getName());

        List<PCPDSolution> E = new ArrayList<>(Pareto.getFront());

        double k = kStep;
        int iters = 0;
        for (int i = 0; i < itersVNS; i++) {
            while (Utils.compareDouble(k, kMax) <= 0) {
                Pareto.reset();
                this.shake(E, k);
                List<PCPDSolution> E_prime = new ArrayList<>(Pareto.getFront());
                for (PCPDSolution solution : E_prime) {
                    ls.improve(new PCPDSolution(solution));
                }
                List<PCPDSolution> E_second_prime = new ArrayList<>(Pareto.getFront());
                k = NC(k, E, E_second_prime);
                E = new ArrayList<>(Pareto.getFront());
                iters++;
            }
        }

        double secs = Timer.getTime() / 1000.0;

        Pareto.saveToFile("experiments/" + date + "/" + this.toString() + "/pareto_" + instance.getName());
        r.add("Time (s)", secs);
        r.add("VNS_Time (s)", secs);
        r.add("VNS_Iters", iters);
        r.add("VNS_k_step", kStep);
        r.add("VNS_k_max", kMax);
        System.out.println("Time (s): " + secs);
        return r;
    }


    private double NC(double k, List<PCPDSolution> e_prime, List<PCPDSolution> e_second_prime) {
        Pareto.reset();
        for (PCPDSolution sol : e_prime) {
            Pareto.add(sol);
        }
        Pareto.isModifiedSinceLastAsk();
        for (PCPDSolution solution : e_second_prime) {
            Pareto.add(solution);
        }
        double toRet;
        toRet = (Pareto.isModifiedSinceLastAsk()) ? kStep : k + kStep;
//        if(toRet==kStep){ System.out.println("MEJORA");}else{ System.out.println("no mejora. toRet = "+toRet);}
        return toRet;
    }

    private void shake(List<PCPDSolution> paretoFront, double k) {
        for (PCPDSolution sol : paretoFront) {
            PCPDSolution aux = new PCPDSolution(sol);
            aux.shake(k);
            Pareto.add(aux);
        }

    }

    @Override
    public Solution getBestSolution() {
        return best;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "("  + ", k = " + kMax + ")";
    }
}
