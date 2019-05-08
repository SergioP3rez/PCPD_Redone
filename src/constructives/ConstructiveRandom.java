package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import structure.PCPDInstance;
import structure.PCPDSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConstructiveRandom implements Constructive<PCPDInstance, PCPDSolution> {
    @Override
    public PCPDSolution constructSolution(PCPDInstance instance) {
        Random rnd = RandomManager.getRandom();
        PCPDSolution sol = new PCPDSolution(instance);
        int n = instance.getN();
//        ArrayList<Integer> selectedNodes = (ArrayList<Integer>)(sol.getSelectedNodes());
        List<Integer> candidates = IntStream.range(0,n-1).boxed().collect(Collectors.toList());
        Collections.shuffle(candidates, rnd);
        for (int i = 0; i < instance.getP(); i++) {
            sol.addToSelectedNodes(candidates.get(i));
        }
        return sol;
    }
}
