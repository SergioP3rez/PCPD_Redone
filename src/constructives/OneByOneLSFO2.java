package constructives;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import structure.PCPDSolution;
import structure.Pareto;

import java.util.Collections;
import java.util.List;

public class OneByOneLSFO2 implements Improvement<PCPDSolution> {

    @Override
    public void improve(PCPDSolution solution) {

        boolean improved = true;

        while (improved) {

        }

    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
