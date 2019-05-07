package algorithms;


import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.Timer;
import gurobi.*;
import structure.PCPDInstance;

public class ModeloSerio implements Algorithm<PCPDInstance> {

    private int timeLimit;

    public ModeloSerio(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public Result execute(PCPDInstance instance) {
        System.out.println(instance.getName());
        Result r = new Result(instance.getName());
        GRBEnv env = null;
        int n = instance.getN();
        int p = instance.getP();
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
            GRBVar[][] x = new GRBVar[n+1][n+1];
            GRBVar[] y = new GRBVar[n+1];
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    x[i-1][j-1] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "x["+(i-1)+"]["+(j-1)+"]");
                }
                y[i-1] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "y["+(i-1)+"]");
            }

            // OBJECTIVE FUNCTION
//            GRBVar z = model.addVar(0.0, GRB.INFINITY, 1.0, GRB.INTEGER, "z");
//            GRBLinExpr obj = new GRBLinExpr();
//            obj.addTerm(1.0, z);
//            model.setObjective(obj, GRB.MINIMIZE);

            GRBVar w = model.addVar(0.0, GRB.INFINITY, 1.0, GRB.INTEGER, "w");
            GRBLinExpr obj = new GRBLinExpr();
            obj.addTerm(1.0, w);
            model.setObjective(obj, GRB.MAXIMIZE);


            // CONSTRAINTS

//            for (int i = 1; i <= n; i++) {
//                GRBLinExpr r0 = new GRBLinExpr();
//                for (int j = 1; j <= n; j++) {
//                    int d = instance.getDist(i,j);
//                    r0.addTerm(d, x[i][j]);
//                }
//                model.addConstr(r0, GRB.LESS_EQUAL, z, "r0_"+i);
//            }

            for (int i = 1; i <= n; i++) {
                GRBLinExpr r1 = new GRBLinExpr();
                for (int j = 1; j <= n; j++) {
                    r1.addTerm(1, x[i-1][j-1]);
                }
                model.addConstr(r1, GRB.EQUAL, 1, "r1_"+i);
            }

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    model.addConstr(x[i-1][j-1], GRB.LESS_EQUAL, y[j-1], "r3_"+(i-1)+"_"+(j-1));
                }
            }

            GRBLinExpr r4 = new GRBLinExpr();
            for (int j = 1; j <= n; j++) {
                r4.addTerm(1.0, y[j-1]);
            }
            model.addConstr(r4, GRB.EQUAL, p, "r4");

            int M = 0;
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    int[][] distances = instance.getDistances();
                    M = Math.max(distances[i-1][j-1], M);
                }
            }

            for (int i = 1; i < n; i++) {
                for (int j = i+1; j <= n; j++) {
                    GRBLinExpr rightTerm = new GRBLinExpr();
                    rightTerm.addConstant(2);
                    rightTerm.addTerm(-1, y[i-1]);
                    rightTerm.addTerm(-1, y[j-1]);
                    GRBLinExpr r5 = new GRBLinExpr();
                    r5.multAdd(M, rightTerm);
                    r5.addConstant(instance.getDistances()[i-1][j-1]);
                    model.addConstr(w, GRB.LESS_EQUAL, r5, "r5_"+(i-1)+"_"+(j-1));
                }
            }

            model.update();
            Timer.initTimer();
            model.optimize();

            int status = model.get(GRB.IntAttr.Status);
            System.out.print(status+"\t");
            double secs = Timer.getTime()/1000.0;
            System.out.print(secs+"\t");
            if (status != GRB.INFEASIBLE) {
                double of = model.get(GRB.DoubleAttr.ObjVal);
                System.out.println(of);
                r.add("OF", of);
                //System.out.println("YVAR");
                for (int j = 1; j <= n; j++) {
                    //System.out.println(j+" --> "+y[j].get(GRB.DoubleAttr.X));
                    if(y[j-1].get(GRB.DoubleAttr.X)==1){
                        System.out.println(j-1);
                    }
                }
                /*for (GRBLinExpr constraint : constraints) {
                    System.out.println("CONST:"+constraint.getValue());
                }*/
               /* System.out.println("Selected: ");
                for (int j = 1; j <= n; j++) {
                    if (y[j].get(GRB.DoubleAttr.X) == 1) {
                        System.out.print("S["+j+"]: ");
                        for (int i = 1; i <= n; i++) {
                            if (i == j) continue;
                            if (x[i][j].get(GRB.DoubleAttr.X) == 1) {
                                System.out.print("["+i+","+ instance.getDistances()[i-1][j-1] +"]");
                            }
                        }
                        System.out.println();
                    }
                }*/
            } else {
                System.out.println("INFEASIBLE");
                r.add("OF", -1);
            }
            r.add("Time (s)", secs);
            r.add("Status", status);

        } catch (GRBException e) {
            e.printStackTrace();
        }

        return r;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }
}
