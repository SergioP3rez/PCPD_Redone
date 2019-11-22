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
    private Random rnd;

    public OneByOneLSFA(double alpha){
        this.alpha = alpha;
        this.rnd = new Random(RandomManager.getRandom().nextInt());
    }

    @Override
    public void improve(PCPDSolution sol) {

        double realAlpha = ((alpha >= 0) ? alpha : rnd.nextDouble());
        int n = sol.getInstance().getN();

        boolean improved = true;
        double actualAggregatedValue = realAlpha * sol.getMinDistanceOutToIn() - (1-realAlpha)*sol.getMaxDistanceBetweenSelected();

        while (improved){
            improved = false;
            List<Integer> selected = sol.getSelectedNodes();
            int p = sol.getInstance().getP();
            
//            Collections.shuffle(selected, RandomManager.getRandom());
            int bestNodeToAdd = -1;
            int bestNodeToQuit = -1;
             //TODO: Hay que ver si comprobamos el que i y selected.get(j) no son el mismo,
             //  aunque creo que no pasará porque el isSelected descarta esa posibildad
            for (int i = 0; i < n; i++) {
                if(sol.isSelected(i)) continue;
                for (int j = 0; j < p; j++) {


                    int nodeToRemove = selected.get(j);
                    sol.removeFromSelectedNodes(nodeToRemove);
                    sol.addToSelectedNodes(i);

                    Pareto.add(sol);
                    double newAggregatedValue = (realAlpha * sol.getMinDistanceOutToIn()) - (1-realAlpha)*sol.getMaxDistanceBetweenSelected();

                    if(Utils.compareDouble(newAggregatedValue, actualAggregatedValue) < 0){
                        actualAggregatedValue = newAggregatedValue;
                        bestNodeToQuit = nodeToRemove;
                        bestNodeToAdd = i;
                        improved = true;
                    }
                    sol.removeFromSelectedNodes(i);
                    sol.addToSelectedNodes(nodeToRemove);

                }

            }
            sol.addToSelectedNodes(bestNodeToAdd);
            sol.removeFromSelectedNodes(bestNodeToQuit);

            if (Timer.timeReached()) {
                return;
            }
        }

    }

    @Override
    public String toString() {return this.getClass().getSimpleName()+"("+alpha+")";}
}
