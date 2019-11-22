package constructives;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import structure.PCPDSolution;
import structure.Pareto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OneByOneLSFO1 implements Improvement<PCPDSolution> {

    @Override
    public void improve(PCPDSolution sol) {

        boolean improved = true;

        while (improved) {
            improved = false;
            int n = sol.getInstance().getN();
            int p = sol.getInstance().getP();

            int bestNodeToAdd = -1;
            int bestNodeToQuit = -1;
            int actualMinDistance = sol.getMinDistanceOutToIn();
            List<Integer> selected = new ArrayList<>(sol.getSelectedNodes());
//            Collections.shuffle(selected, RandomManager.getRandom());
            for (int i = 0; i < n; i++) {
                if(sol.isSelected(i)) continue;
                for (int j = 0; j < p; j++) {
                    int nodeToUnselect = selected.get(j);
                    sol.removeFromSelectedNodes(nodeToUnselect);
                    sol.addToSelectedNodes(i);
                    Pareto.add(sol);
                    int postDistance = sol.getMinDistanceOutToIn();
                    if(postDistance < actualMinDistance){
                        actualMinDistance = postDistance;
                        bestNodeToAdd = i;
                        bestNodeToQuit = nodeToUnselect;
                    }
                    sol.removeFromSelectedNodes(i);
                    sol.addToSelectedNodes(nodeToUnselect);
                }
            }

            sol.addToSelectedNodes(bestNodeToAdd);
            sol.removeFromSelectedNodes(bestNodeToQuit);
            if(Timer.timeReached()) return;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
