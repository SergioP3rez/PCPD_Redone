package algorithms;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.Timer;
import structure.PCPDInstance;
import structure.PCPDSolution;
import structure.Pareto;

public class AlgConstructiveFO2 implements Algorithm<PCPDInstance> {
    private PCPDSolution best;
    private Constructive<PCPDInstance, PCPDSolution> c;
    private int iters;

    public AlgConstructiveFO2(Constructive<PCPDInstance, PCPDSolution> c, int iters){
        this.c = c;
        this.iters = iters;
    }
    @Override
    public Result execute(PCPDInstance instance) {
        best = null;
        Result r = new Result(instance.getName());
        Pareto.reset();
        System.out.print(instance.getName()+"\t");
        Timer.initTimer(1800*1000);
        for (int i = 0; i < iters; i++) {
            PCPDSolution sol = c.constructSolution(instance);
            Pareto.add(sol);
            if(best == null || sol.getMaxDistanceBetweenSelected() > best.getMaxDistanceBetweenSelected()){
                best = sol;
            }

            if(Timer.timeReached()) break;
        }


        double secs = Timer.getTime()/1000.0;
        r.add("MIN MAX DISTANCE", best.getMinDistanceOutToIn());
        r.add("MAX MIN DISTANCE", best.getMaxDistanceBetweenSelected());
        r.add("Time (s)", secs);

        System.out.println("Sol: "+best+"\tMin distance Out to in: "+best.getMinDistanceOutToIn()+"\tMax distance between selected: "+best.getMaxDistanceBetweenSelected()+"\t"+best+"\t"+secs+"\t");
        return r;
    }

    @Override
    public Solution getBestSolution() {
        return best;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+c+","+iters+")";
    }
}
