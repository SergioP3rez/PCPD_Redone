package experiments;


import algorithms.*;
import constructives.*;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Experiment;
import grafo.optilib.tools.RandomManager;
import structure.PCPDInstance;
import structure.PCPDInstanceFactory;
import structure.PCPDSolution;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class ExperimentAlgorithm {
    public static int contevs = 0;
    public static void main(String[] args) {

        RandomManager.setSeed(1995);
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);

        PCPDInstanceFactory factory = new PCPDInstanceFactory();

//        String dir = ((args.length == 0) ? "instances/pruebasVNS" : (args[1] + "/"));
//        String dir = ((args.length == 0) ? "instances/constructive_experiments" : (args[1] + "/"));
        String dir = ((args.length == 0) ? "instancias_todas_ampliada/" : (args[1] + "/"));
//        String dir = ((args.length == 0) ? "instances/" : (args[1] + "/"));
//        String dir = ((args.length == 0) ? "instances/final_experiments" : (args[1] + "/"));
//        String dir = ((args.length == 0) ? "instances/pr" : (args[1] + "/"));
//        String dir = ((args.length == 0) ? "instances/small" : (args[1] + "/"));
//        String dir = ((args.length == 0) ? "instances/testchungos" : (args[1] + "/"));
        String outDir = "experiments/" + date;
        File outDirCreator = new File(outDir);
        outDirCreator.mkdirs();
        String[] extensions = new String[]{".txt"};
        ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives = new ArrayList<>();
//        ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives2 = new ArrayList<>();

//        constructives.add(new ConstructiveGRASPGreedyRandomFO1COPIA(0.1)); //0
//        constructives.add(new ConstructiveGRASPGreedyRandomFO2(0.1)); //1
//        constructives.add(new ConstructiveGRASPRandomGreedyFO1(0.1)); //2
//        constructives.add(new ConstructiveGRASPRandomGreedyFO2(0.1)); //3
//        constructives.add(new ConstructiveRandom());
//        constructives.add(new ConstructiveGRASPRandomGreedyFA(-1.0, -1.0));

        //IteratedGreedy
        /*
        constructives.add(new ConstructiveGRASPRandomGreedyFA(-1.0, -1.0));
        constructives.add(new ConstructiveGRASPRandomGreedyFA(1, -1.0));
        constructives.add(new ConstructiveGRASPRandomGreedyFA(0.25, -1.0));
        constructives.add(new ConstructiveGRASPRandomGreedyFA(0.5, -1.0));
        constructives.add(new ConstructiveGRASPRandomGreedyFA(0.75, -1.0));*/

        //Previos : 1- Ejecucion separada constructivos, bloque por bloque, 100 cons (Exps normales de toda la vida)
        //Previos : 2- Ejecucion todos los constructivos 25 cons
        //Previos : 3 y 4- Igual que 1 y 2 pero con LS

        //IteratedGreedyOnlyConst
        constructives.add(new ConstructiveGRASPGreedyRandomFA(-1.0, 0));
        constructives.add(new ConstructiveGRASPGreedyRandomFA(-1.0, 0.25));
        constructives.add(new ConstructiveGRASPGreedyRandomFA(-1.0, 0.5));
        constructives.add(new ConstructiveGRASPGreedyRandomFA(-1.0, 0.75));
        constructives.add(new ConstructiveGRASPGreedyRandomFA(-1.0, 1));
////
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.25, 0));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.25, 0.25));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.25, 0.5));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.25, 0.75));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.25, 1));

//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.5, 0));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.5, 0.25));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.5, 0.5));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.5, 0.75));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.5, 1));
//
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.75, 0));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.75, 0.25));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.75, 0.5));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.75, 0.75));
//        constructives.add(new ConstructiveGRASPGreedyRandomFA(0.75, 1));

//        constructives2.add(new ConstructiveGRASPGreedyRandomFA(-1.0, -1.0));

        ArrayList<Improvement<PCPDSolution>> localSearchs = new ArrayList<>();
        localSearchs.add(new OneByOneLSFO1()); //0
        localSearchs.add(new OneByOneLSFO2()); //1
        localSearchs.add(new OneByOneLSFA(0)); //2
        localSearchs.add(new OneByOneLSFA(0.25)); //3
        localSearchs.add(new OneByOneLSFA(0.5)); //4
        localSearchs.add(new OneByOneLSFA(0.75)); //5
        localSearchs.add(new OneByOneLSFA(1.0)); //6

        ArrayList<Improvement<PCPDSolution>> localSearchs2 = new ArrayList<>();
        localSearchs2.add(new OneByOneLSFO1()); //0
        localSearchs2.add(new OneByOneLSFO2()); //1
        localSearchs2.add(new OneByOneLSFAFirstImp(0)); //2
        localSearchs2.add(new OneByOneLSFAFirstImp(0.25)); //3
        localSearchs2.add(new OneByOneLSFAFirstImp(0.5)); //4
        localSearchs2.add(new OneByOneLSFAFirstImp(0.75)); //5
        localSearchs2.add(new OneByOneLSFAFirstImp(1.0)); //6

        Improvement<PCPDSolution>[] ls = new Improvement[2];
        ls[0] = new OneByOneLSFO1();
        ls[1] = new OneByOneLSFO2();
        Algorithm<PCPDInstance>[] execution = new Algorithm[]{
//                new AlgConstructiveFO1(new ConstructiveGRASPRandomGreedyFA(-1.0, -1.0), 100),
//                new AlgConstructiveFO2(new ConstructiveGRASPGreedyRandomFO2(0), 100),
//
                //new IteratedLocalSearch(constructives, new OneByOneLSFA(-1.0), 10, 100, 0.25),
//                new EpsilonConstraintSerio(1800),

//                new IteratedGreedy(constructives, localSearchs, 5, 100, 0.05),
//                new IteratedGreedy(constructives, localSearchs, 5, 100, 0.1),
//                new IteratedGreedy(constructives, localSearchs, 5, 100, 0.15),
//                new IteratedGreedy(constructives, localSearchs, 5, 100, 0.2),
//                new IteratedGreedy(constructives, localSearchs, 5, 100, 0.3),


//                new IteratedGreedyOnlyConst(constructives, localSearchs, 100, 20),
//                new IteratedGreedyOnlyConst(constructives2, localSearchs, 100, 20),

//                new IteratedGreedyOnlyConst(constructives, localSearchs, 10, 5),
//                new IteratedGreedyOnlyConst(constructives, localSearchs, 10, 5),
//                new IteratedGreedyOnlyConst(constructives, localSearchs, 10, 5),

//                new IteratedGreedy(constructives, localSearchs, 100, 35, 0.1),
//                new IteratedGreedy(constructives, localSearchs2, 3, 35, 0.2),
//                new IteratedGreedy(constructives, localSearchs, 100, 35, 0.3),
//                new IteratedGreedy(constructives, localSearchs, 100, 35, 0.4),
//                new IteratedGreedy(constructives, localSearchs, 100, 35, 0.5),

//                new ParallelIteratedGreedyRandomPert(constructives, localSearchs2, 3, 35, 0.2),
                new ParallelIteratedGreedy(constructives, localSearchs, 10, 100, 0.3),

//                new ParallelIteratedGreedy(constructives, localSearchs, 100, 35, 0.3)

//                new IteratedGreedy(constructives, localSearchs, 10, 100, 0.75),
//                new IteratedGreedy(constructives, localSearchs, 10, 100, -1.0),
//                new EpsilonConstraintSerio(1800)
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(0.25, 0.25), new VND(ls), 100, 0.05, 0.5),

//                new MO_VNS(constructives, new TwoInOneLS(ls), 500, 0.05, 0.5, 10),
//                new MO_VNS(constructives, new TwoInOneLS(ls), 500, 0.05, 0.1, 10),
//                new MO_VNS(constructives, new TwoInOneLS(ls), 500, 0.05, 0.2, 10),
//                new MO_VNS(constructives, new TwoInOneLS(ls), 500, 0.05, 0.3, 10),
//                new MO_VNS(constructives, new TwoInOneLS(ls), 500, 0.05, 0.4, 10),

//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(0.75, 0.25), new VND(ls), 100, 0.05, 0.5),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(-1.0, 0.25), new VND(ls), 100, 0.05, 0.2),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(-1.0, -1.0), new VND(ls), 100, 0.05, 0.3),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(-1.0, -1.0), new VND(ls), 100, 0.05, 0.4),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(-1.0, 0.25), new VND(ls), 100, 0.05, 0.5)
//                new ConstructiveWithoutPerturb(constructives, localSearchs, 10, 100),

//                new ModeloSerio(1800),

        };

        for (int i = 0; i < execution.length; i++) {
            //String outputFile = outDir+"/"+instanceSet+"_"+execution[i].toString()+".xlsx";
            String outputFile = outDir + "/" + execution[i].toString() + "_PIG_Final.xlsx";
            Experiment<PCPDInstance, PCPDInstanceFactory> experiment = new Experiment<>(execution[i], factory);
            experiment.launch(dir, outputFile, extensions);

        }

    }
}
