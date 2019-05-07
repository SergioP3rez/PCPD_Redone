package constructives;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import structure.PCPDSolution;
import structure.Pareto;
import utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OneByOneLSFA implements Improvement<PCPDSolution> {

    private double alpha;

    public OneByOneLSFA(double alpha){
        this.alpha = alpha;
    }

    @Override
    public void improve(PCPDSolution solution) {

        Random rnd = RandomManager.getRandom();
        double realAlpha = ((alpha >= 0) ? alpha : rnd.nextDouble());
        int n = solution.getInstance().getN();




        boolean improved = true;
        double actualAggregatedValue = realAlpha * solution.getMinDistanceOutToIn() + (1-realAlpha)*solution.getMaxDistanceBetweenSelected();

        while (improved) {
            improved = false;
            List<Integer> selectedNodes = solution.getSelectedNodes();
        }




    }

    @Override
    public String toString() {return this.getClass().getSimpleName();}
}
