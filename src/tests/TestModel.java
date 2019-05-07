package tests;

import structure.PCPDInstance;
import structure.PCPDSolution;

public class TestModel {


    public static void main(String[] args){
        //PCPDInstance instance1 = new PCPDInstance("/Users/sergio/Proyectos IntelliJ/PCPD/instances/pruebas/pmed1_n10_p5.txt");
        PCPDInstance instance1 = new PCPDInstance("/Users/sergio/Proyectos IntelliJ/PCPD/instances/pruebas/pmed1_n100_p50.txt");
        //PCPDInstance instance2 = new PCPDInstance("/Users/sergio/Proyectos IntelliJ/PCPD/instances/pruebas/pmed2_n30_p10.txt");
        //PCPDInstance instance3 = new PCPDInstance("/Users/sergio/Proyectos IntelliJ/PCPD/instances/pruebas/pmed3_n20_p10.txt");
        PCPDSolution sol1 = new PCPDSolution(instance1);
        //PCPDSolution sol2 = new PCPDSolution(instance2);
        //PCPDSolution sol3 = new PCPDSolution(instance3);

        sol1.comprobadorSoluciones("0\n" +
                "4\n" +
                "9\n" +
                "13\n" +
                "15\n" +
                "16\n" +
                "19\n" +
                "22\n" +
                "30\n" +
                "32\n" +
                "33\n" +
                "35\n" +
                "37\n" +
                "38\n" +
                "39\n" +
                "40\n\n" +
                "42\n" +
                "44\n" +
                "45\n" +
                "46\n" +
                "47\n" +
                "49\n" +
                "51\n" +
                "52\n" +
                "54\n" +
                "56\n" +
                "60\n" +
                "61\n" +
                "62\n" +
                "63\n" +
                "66\n" +
                "68\n" +
                "69\n" +
                "70\n" +
                "73\n" +
                "75\n" +
                "76\n" +
                "78\n" +
                "79\n" +
                "81\n" +
                "83\n" +
                "85\n" +
                "88\n" +
                "90\n" +
                "91\n" +
                "93\n" +
                "95\n" +
                "96\n" +
                "98\n" +
                "99");
        System.out.println(sol1);
        /*sol2.comprobadorSoluciones("0\n" +
                "3\n" +
                "8\n" +
                "12\n" +
                "14\n" +
                "16\n" +
                "18\n" +
                "21\n" +
                "27\n" +
                "29");
        System.out.println(sol2);
        sol3.comprobadorSoluciones("0\n" +
                "1\n" +
                "2\n" +
                "3\n" +
                "8\n" +
                "11\n" +
                "13\n" +
                "16\n" +
                "17\n" +
                "19");
        System.out.println(sol3);*/

        System.out.println("Sol1MinMaxDistanceOutToIn: "+sol1.getMinDistanceOutToIn());
        System.out.println("Sol1MaxMinDistanceBetweenSelected: "+sol1.getMaxDistanceBetweenSelected());
        /*System.out.println("Sol2Min: "+sol2.getMinDistanceBetweenSelected());
        System.out.println("Sol2Max: "+sol2.getMaxDistanceOutToIn());
        System.out.println("Sol3Min: "+sol3.getMinDistanceBetweenSelected());
        System.out.println("Sol3Max: "+sol3.getMaxDistanceOutToIn());*/

    }
}
