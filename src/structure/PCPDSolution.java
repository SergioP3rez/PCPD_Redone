package structure;

import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PCPDSolution implements Solution {

    private PCPDInstance instance;
    private int distances[][];
    private List<Integer> selectedNodes;
    private int minDistanceOutToIn;
    private int maxDistanceBetweenSelected;
    private boolean updated;
    public PCPDSolution(PCPDInstance instance) {
        this.instance = instance;
        this.distances = this.instance.getDistances();
        selectedNodes = new ArrayList<>(instance.getP());
        this.minDistanceOutToIn = Integer.MAX_VALUE;
        this.maxDistanceBetweenSelected = Integer.MIN_VALUE;
        updated = false;
    }

    public PCPDSolution(PCPDInstance instance, ArrayList<Integer> selectedNodes, int maxDistanceOutToIn, int minDistanceBetweenSelected) {
        this.instance = instance;
        this.distances = this.instance.getDistances();
        this.selectedNodes = selectedNodes;
        this.minDistanceOutToIn = maxDistanceOutToIn;
        this.maxDistanceBetweenSelected = minDistanceBetweenSelected;
        this.updated = false;
    }

    public void copy(PCPDSolution sol) {

        this.instance = sol.instance;
        this.selectedNodes = new ArrayList<>(sol.selectedNodes);
        this.distances = sol.getInstance().getDistances();
        this.minDistanceOutToIn = sol.minDistanceOutToIn;
        this.maxDistanceBetweenSelected = sol.maxDistanceBetweenSelected;
        this.updated = sol.updated;
    }

    public PCPDSolution(PCPDSolution sol) {
        copy(sol);
    }

    public PCPDInstance getInstance() {
        return instance;
    }


    public int maxDistanceOutToIn(int node, int[] distanceToNearestCenter){//Esto es lo que hay que minimizar
        int maxDistance = Integer.MIN_VALUE;
        int n = instance.getN();
        selectedNodes.add(node);
        for (int i = 0; i < n; i++) {
            if(selectedNodes.contains(i)) continue;
            int distanceBetween2 = distances[node][i];

            if (distanceBetween2 > maxDistance) {
                maxDistance = Math.max(maxDistance, distanceToNearestCenter[i]);
            }

        }
        selectedNodes.remove(Integer.valueOf(node));
        return maxDistance;
    }


    public int minimumDistanceBetweenSelected(){//Esto es lo que hay que maximizar
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < selectedNodes.size(); i++) {
            for (int j = 0; j < selectedNodes.size(); j++) {
                if (i==j) continue;
                int distanceBetween2 = distances[i][j];
                if (distanceBetween2 < minDistance){
                    minDistance = distanceBetween2;
                }
            }

        }
        return minDistance;
    }

    public void updateMinDistanceOutToIn() {

        int n = this.getInstance().getN();
        this.minDistanceOutToIn = Integer.MIN_VALUE;

        //i es el nodo no seleccionado y j el seleccionado
        for (int i = 0; i < n; i++) {
            int maxDistanceInOut = Integer.MAX_VALUE;
            if(this.selectedNodes.contains(i)) continue;
            for (int j = 0; j < this.selectedNodes.size(); j++) {
                int actDistance = distances[i][this.selectedNodes.get(j)];
                if(actDistance < maxDistanceInOut){
                    maxDistanceInOut = actDistance;
                }
            }
            this.minDistanceOutToIn = Math.max(this.minDistanceOutToIn, maxDistanceInOut);
        }

    }

    public void updateMaxDistanceBetweenSelected() {

        //int minDistanceSelected = this.maxDistanceBetweenSelected;
        int minDistanceSelected = Integer.MAX_VALUE;
        for (int i = 0; i < selectedNodes.size(); i++) {
            for (int j = 0; j < selectedNodes.size(); j++) {
                if(i==j) continue;
                int actDistance = distances[this.selectedNodes.get(i)][this.selectedNodes.get(j)];
                if(actDistance < minDistanceSelected){
                    minDistanceSelected = actDistance;
                }
            }
        }
        this.maxDistanceBetweenSelected =  minDistanceSelected;
    }

    public boolean esFactible() {
       return selectedNodes.size()==this.instance.getP();
    }

    public int getMinDistanceOutToIn() {
        if(!updated){
            updateMinDistanceOutToIn();
            updateMaxDistanceBetweenSelected();
            updated = true;
        }
        return minDistanceOutToIn;
    }

    public int getMaxDistanceBetweenSelected() {
        if(!updated){
            updateMaxDistanceBetweenSelected();
            updateMinDistanceOutToIn();
            updated = true;
        }
        return maxDistanceBetweenSelected;
    }

    public void comprobadorSoluciones(String s){
        String[] tok = s.split("\\s+");
        for (int i = 0; i < tok.length; i++) {
            this.selectedNodes.add(Integer.parseInt(tok[i]));
        }
    }

    public void addToSelectedNodes(int firstSelected) {
        selectedNodes.add(firstSelected);
        updated = false;
    }

    public boolean isSelected(int i) {
        return selectedNodes.contains(i);
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append("Selected: [");
        for (int i = 0; i < selectedNodes.size()-1; i++) {
            stb.append(selectedNodes.get(i)+", ");
        }
        stb.append(selectedNodes.get(selectedNodes.size()-1)+"]\tNon selected: [");
        for (int i = 0; i < this.instance.getN()-1; i++) {
            if(!selectedNodes.contains(i)){
                stb.append(i+", ");
            }
        }
        if(!selectedNodes.contains(this.instance.getN()-1)){
            stb.append(this.instance.getN()-1+"]\n");
        }else{
            stb.delete(stb.length()-2, stb.length());
            stb.append("]\n");
        }

        return stb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PCPDSolution solution = (PCPDSolution) o;
        return (this.selectedNodes==solution.selectedNodes);
    }

    @Override
    public int hashCode() {
        return selectedNodes.hashCode();
    }

    public void shake(double k) {
        Random rnd = RandomManager.getRandom();
        List<Integer> nonSelectedNodes = new ArrayList<>();
        int n = instance.getN();
        for (int i = 0; i < n; i++) {
            if(!selectedNodes.contains(i)){
                nonSelectedNodes.add(i);
            }
        }
        for (int i = 0; i < k; i++) {
            int nodeId = rnd.nextInt(this.selectedNodes.size());
            int nodeToSelectId = rnd.nextInt(nonSelectedNodes.size());

            int nodeToSelect = nonSelectedNodes.remove(nodeToSelectId);
            int node = selectedNodes.remove(nodeId);

            nonSelectedNodes.add(node);
            selectedNodes.add(nodeToSelect);
        }
    }

}




