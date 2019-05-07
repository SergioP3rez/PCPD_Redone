package algorithms;

import grafo.optilib.tools.Timer;
import gurobi.*;
import structure.PCPDInstance;

public class PDispersionSolver {

    public static double solve(PCPDInstance instance, int timeLimit) {
        GRBEnv env;
        int n = instance.getN();
        int p = instance.getP();
        int M = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                M = Math.max(instance.getDistances()[i-1][j-1], M);
            }
        }
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
                    x[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "x["+i+"]["+j+"]");
                }
                y[i] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "y["+i+"]");
            }

            // OBJECTIVE FUNCTION

            GRBVar w = model.addVar(0.0, GRB.INFINITY, 1.0, GRB.INTEGER, "w");
            GRBLinExpr obj = new GRBLinExpr();
            obj.addTerm(1.0, w);
            model.setObjective(obj, GRB.MAXIMIZE);


            // CONSTRAINTS

            for (int i = 1; i <= n; i++) {
                GRBLinExpr r1 = new GRBLinExpr();
                for (int j = 1; j <= n; j++) {
                    r1.addTerm(1, x[i][j]);
                }
                model.addConstr(r1, GRB.EQUAL, 1, "r1_"+i);
            }

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    model.addConstr(x[i][j], GRB.LESS_EQUAL, y[j], "r3_"+i+"_"+j);
                }
            }

            GRBLinExpr r4 = new GRBLinExpr();
            for (int j = 1; j <= n; j++) {
                r4.addTerm(1.0, y[j]);
            }
            model.addConstr(r4, GRB.EQUAL, p, "r4");



            for (int i = 1; i < n; i++) {
                for (int j = i+1; j <= n; j++) {
                    GRBLinExpr rightTerm = new GRBLinExpr();
                    rightTerm.addConstant(2);
                    rightTerm.addTerm(-1, y[i]);
                    rightTerm.addTerm(-1, y[j]);
                    GRBLinExpr r5 = new GRBLinExpr();
                    r5.multAdd(M, rightTerm);
                    r5.addConstant(instance.getDistances()[i-1][j-1]);
                    model.addConstr(w, GRB.LESS_EQUAL, r5, "r5_"+i+"_"+j);
                }
            }

            model.update();
            Timer.initTimer();
            model.optimize();

            int status = model.get(GRB.IntAttr.Status);
            if (status != GRB.INFEASIBLE) {
                double of = model.get(GRB.DoubleAttr.ObjVal);
                return of;
            } else {
                return -1;
            }

        } catch (GRBException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
