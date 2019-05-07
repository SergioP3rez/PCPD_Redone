package experiments;


import algorithms.AlgConstructiveFO1;
import algorithms.MO_VNS;
import constructives.*;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Experiment;
import structure.PCPDInstance;
import structure.PCPDInstanceFactory;
import structure.PCPDSolution;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class ExperimentAlgorithm {

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);

        PCPDInstanceFactory factory = new PCPDInstanceFactory();

//        String dir = ((args.length == 0) ? "instances/pruebasVNS" : (args[1] + "/"));
        String dir = ((args.length == 0) ? "instances/testchungos" : (args[1] + "/"));
        String outDir = "experiments/" + date;
        File outDirCreator = new File(outDir);
        outDirCreator.mkdirs();
        String[] extensions = new String[]{".txt"};
        ArrayList<Constructive<PCPDInstance, PCPDSolution>> constructives = new ArrayList<>();

//        constructives.add(new ConstructiveGRASPRandomGreedyFO1(0.75)); //0
//        constructives.add(new ConstructiveGRASPGreedyRandomFO2(0.75)); //1
//        constructives.add(new ConstructiveGRASPRandomGreedyFO1(0.75)); //2
//        constructives.add(new ConstructiveGRASPRandomGreedyFO2(0.75)); //3
//        constructives.add(new ConstructiveRandom());
        constructives.add(new ConstructiveGRASPRandomGreedyFO1(-1));

        ArrayList<Improvement<PCPDSolution>> localSearchs = new ArrayList<>();
        localSearchs.add(new OneByOneLSFO1());
        localSearchs.add(new OneByOneLSFO2());


        Improvement<PCPDSolution>[] ls = new Improvement[2];
        ls[0] = new OneByOneLSFO1();
        ls[1] = new OneByOneLSFO2();
        Algorithm<PCPDInstance>[] execution = new Algorithm[]{
                new AlgConstructiveFO1(new ConstructiveGRASPRandomGreedyFO1(0.25), 100),
              //  new AlgConstructiveFO2(new ConstructiveGRASPGreedyRandomFO2(0.25), 100),

                //new IteratedLocalSearch(constructives, new OneByOneLSFA(-1.0), 10, 100, 0.25),
//                new EpsilonConstraintSerio(1800),
//                new IteratedGreedy(constructives, localSearchs, 10, 100, 0.25),
//                new IteratedGreedy(constructives, localSearchs, 10, 100, 0.5),
//                new IteratedGreedy(constructives, localSearchs, 10, 100, 0.75),
////                new IteratedGreedy(constructives, localSearchs, 10, 100, -1.0),
//                new EpsilonConstraintSerio(1800)
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(0.25, 0.25), new VND(ls), 100, 0.05, 0.5),
//                new MO_VNS(constructives, new VND(ls), 100, 0.05, 0.5),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(0.75, 0.25), new VND(ls), 100, 0.05, 0.5),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(-1.0, 0.25), new VND(ls), 100, 0.05, 0.2),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(-1.0, -1.0), new VND(ls), 100, 0.05, 0.3),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(-1.0, -1.0), new VND(ls), 100, 0.05, 0.4),
//                new MO_VNS(new ConstructiveGRASPRandomGreedyFA(-1.0, 0.25), new VND(ls), 100, 0.05, 0.5)
//                new ConstructiveWithoutPerturb(constructives, localSearchs, 10, 100),

                //new ModeloSerio(1800),

        };

        for (int i = 0; i < execution.length; i++) {
            //String outputFile = outDir+"/"+instanceSet+"_"+execution[i].toString()+".xlsx";
            String outputFile = outDir + "/" + execution[i].toString() + "_pruebas_IGreedy.xlsx";
            Experiment<PCPDInstance, PCPDInstanceFactory> experiment = new Experiment<>(execution[i], factory);
            experiment.launch(dir, outputFile, extensions);

        }

    }
}
