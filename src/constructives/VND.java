package constructives;

import grafo.optilib.metaheuristics.Improvement;
import structure.PCPDSolution;
import structure.Pareto;

public class VND implements Improvement<PCPDSolution> {
    private Improvement<PCPDSolution>[] localSearchs;

    public VND(Improvement<PCPDSolution>[] localSearchs) {
        this.localSearchs = localSearchs;
    }

    @Override
    public void improve(PCPDSolution sol) {
        int k = 0;
        while (k < localSearchs.length) {
            localSearchs[k].improve(sol);
            Pareto.add(sol);
            if (Pareto.isModifiedSinceLastAsk()) {
                k = 0;
            } else {
                k += 1;
            }
        }
    }
}
