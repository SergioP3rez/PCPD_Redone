package constructives;

import grafo.optilib.metaheuristics.Improvement;
import structure.PCPDSolution;
import structure.Pareto;

public class TwoInOneLS implements Improvement<PCPDSolution> {

    private Improvement<PCPDSolution>[] ls;
    public TwoInOneLS(Improvement<PCPDSolution>[] ls){
        this.ls = ls;
    }
    @Override
    public void improve(PCPDSolution sol) {
        PCPDSolution solLS1 = new PCPDSolution(sol);
        PCPDSolution solLS2 = new PCPDSolution(sol);

        ls[0].improve(solLS1);
        ls[1].improve(solLS2);
        Pareto.add(solLS1);
        Pareto.add(solLS2);
    }
}
