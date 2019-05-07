package algorithms;

import constructives.VND;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import structure.PCPDInstance;
import structure.PCPDSolution;

import java.util.ArrayList;

public class MO_VNS implements Algorithm<PCPDInstance> {
    public MO_VNS(ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives, VND vnd, int i, double v, double v1) {

    }

    @Override
    public Result execute(PCPDInstance instance) {
        return null;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }
}
