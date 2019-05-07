package algorithms;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.Timer;
import gurobi.*;
import structure.PCPDInstance;
import structure.PCPDSolution;
import structure.Pareto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EpsilonConstraintSerio implements Algorithm<PCPDInstance> {

    private int timeLimit;
    private int timeExcededNTimes;

    public EpsilonConstraintSerio(int timeLimit) {
        this.timeLimit = timeLimit;
        this.timeExcededNTimes = 0;
    }

    @Override
    public Result execute(PCPDInstance instance) {
        int n = instance.getN();
        int p = instance.getP();
        System.out.println(instance.getName());
        int M = 0;
        int epsMin = Integer.MAX_VALUE;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (i == j) continue;
                M = Math.max(instance.getDistances()[i - 1][j - 1], M);
                epsMin = Math.min(instance.getDistances()[i - 1][j - 1], epsMin);
            }
        }

        Timer.initTimer();
        Pareto.reset();
        int epsMax = (int) PDispersionSolver.solve(instance, 1800);
//        int eps = 0;

        Result r = new Result(instance.getName());
        for (int eps = epsMin; eps <= epsMax; eps++) {
//            System.out.print("EPS="+eps+"\t");
            GRBEnv env;

            try {
                env = new GRBEnv("log_bpmd.txt");
                env.set(GRB.DoubleParam.TimeLimit, timeLimit);
                env.set(GRB.IntParam.LogToConsole, 0);
                // Starts writing nodes to disk when reaching XXX M
                //            env.set(GRB.DoubleParam.NodefileStart, 0.05);
                // Reduce the number of threads to reduce memory usage
                //            env.set(GRB.IntParam.Threads, 1);
                // Presolve 0 off 1 conservative 2 aggresive
                //            env.set(GRB.IntParam.Presolve, 0);
                GRBModel model = new GRBModel(env);

                // VARIABLES
                GRBVar[][] x = new GRBVar[n + 1][n + 1];
                GRBVar[] y = new GRBVar[n + 1];
                for (int i = 1; i <= n; i++) {
                    for (int j = 1; j <= n; j++) {
                        x[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "x[" + i + "][" + j + "]");
                    }
                    y[i] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "y[" + i + "]");
                }
                GRBVar w = model.addVar(0.0, GRB.INFINITY, 1.0, GRB.INTEGER, "w");

//                 OBJECTIVE FUNCTION
                GRBVar z = model.addVar(0.0, GRB.INFINITY, 1.0, GRB.INTEGER, "z");
                GRBLinExpr obj = new GRBLinExpr();
                obj.addTerm(1.0, z);
                model.setObjective(obj, GRB.MINIMIZE);


                // CONSTRAINTS

                for (int i = 1; i <= n; i++) {
                    GRBLinExpr r0 = new GRBLinExpr();
                    for (int j = 1; j <= n; j++) {
                        int d = instance.getDistances()[i - 1][j - 1];
                        r0.addTerm(d, x[i][j]);
                    }
                    model.addConstr(r0, GRB.LESS_EQUAL, z, "r0_" + i);
                }

                for (int i = 1; i <= n; i++) {
                    GRBLinExpr r1 = new GRBLinExpr();
                    for (int j = 1; j <= n; j++) {
                        r1.addTerm(1, x[i][j]);
                    }
                    model.addConstr(r1, GRB.EQUAL, 1, "r1_" + i);
                }

                for (int i = 1; i <= n; i++) {
                    for (int j = 1; j <= n; j++) {
                        model.addConstr(x[i][j], GRB.LESS_EQUAL, y[j], "r3_" + i + "_" + j);
                    }
                }

                GRBLinExpr r4 = new GRBLinExpr();
                for (int j = 1; j <= n; j++) {
                    r4.addTerm(1.0, y[j]);
                }
                model.addConstr(r4, GRB.EQUAL, p, "r4");

                List<GRBLinExpr> feasibleSols = new ArrayList<>();
                for (int i = 1; i < n; i++) {
                    for (int j = i + 1; j <= n; j++) {
                        GRBLinExpr rightTerm = new GRBLinExpr();
                        rightTerm.addConstant(2);
                        rightTerm.addTerm(-1, y[i]);
                        rightTerm.addTerm(-1, y[j]);
                        GRBLinExpr r5 = new GRBLinExpr();
                        r5.multAdd(M, rightTerm);
                        r5.addConstant(instance.getDistances()[i - 1][j - 1]);
                        feasibleSols.add(r5);
                        model.addConstr(w, GRB.LESS_EQUAL, r5, "r5_" + i + "_" + j);
                    }
                }
                model.addConstr(w, GRB.GREATER_EQUAL, eps, "eps");

                model.update();

                model.optimize();

                int status = model.get(GRB.IntAttr.Status);
                System.out.print(status + "\t");

                //System.out.print(secs + "\t");
                if (status != GRB.INFEASIBLE) {

                    if(status==GRB.TIME_LIMIT) timeExcededNTimes++;

                    double of = model.get(GRB.DoubleAttr.ObjVal);
                    //                double wVal = w.get(GRB.DoubleAttr.X);
                    double f2 = Integer.MAX_VALUE;
                    for (GRBLinExpr feasibleSol : feasibleSols) {
                        f2 = Math.min(f2, feasibleSol.getValue());
                    }

                    ArrayList<Integer> selectedNodes = new ArrayList<>();

                    for (int j = 1; j <= n; j++) {
                        //System.out.println(j+" --> "+y[j].get(GRB.DoubleAttr.X));
                        if(Math.rint(y[j].get(GRB.DoubleAttr.X))==1){
                            selectedNodes.add(j-1);
                        }
                    }
                    System.out.println(instance.getName()+" Selected: "+selectedNodes+" Max: "+f2+" Min: "+of);

                    Pareto.add(new PCPDSolution(instance, selectedNodes,(int) of, (int) f2));
//                    System.out.println("F1=" + of + "\tF2=" + f2);

                    System.out.println(f2 + "\t" + of);
                } else {
//                    r.add("f1", -1);
//                    r.add("f2", -1);
                }

                //r.add("Status", status);

                model.dispose();
                env.dispose();
            } catch (GRBException e) {
                e.printStackTrace();
            }
        }
        double secs = Timer.getTime() / 1000.0;
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);
        Pareto.saveToFile("experiments/"+date+"/"+this.toString()+"/pareto_"+instance.getName());
        r.add("Time (s)", secs);
        r.add("Times time exceeded", timeExcededNTimes);
        return r;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+timeLimit+")";
    }
}
