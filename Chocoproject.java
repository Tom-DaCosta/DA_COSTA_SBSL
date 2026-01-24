import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Solveur pour un Carré Latin Spatialement Équilibré (SBLS) sous Choco.
 * Modèle "simple" : carrés latins classiques
 * Modèle SBLS : avec contrainte d'équilibrage des distances de Manhattan
 */
public class Chocoproject {

    public static void main(String[] args) {
        int[] ns = (args != null && args.length > 0)
                ? Arrays.stream(args).mapToInt(Integer::parseInt).toArray()
                : new int[] { 2, 3, 4, 5, 6, 7 };

        System.out.println("Expérience Choco SBLS (distance de Manhattan)");
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("OS  : " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("CPU : " + System.getenv().getOrDefault("PROCESSOR_IDENTIFIER", "unknown"));
        System.out.println();

        for (int n : ns) {
            System.out.println("=== n = " + n + " ===");

            Result simple = solveLatinSquare(n);
            System.out.printf("SIMPLE (Carré latin):  temps=%.3fs  noeuds=%d  solutions=%d%n",
                    simple.timeSeconds, simple.nodes, simple.solutions);

            Result sbls = solveSBLS(n);
            System.out.printf("SBLS               :  temps=%.3fs  noeuds=%d  solutions=%d%n",
                    sbls.timeSeconds, sbls.nodes, sbls.solutions);

            System.out.println();
        }
    }

    /**
     * Méthode simple : carré latin + bris de symétrie
     */
    static Result solveLatinSquare(int n) {
        Model model = new Model("LatinSquare_n" + n);
        IntVar[][] x = latinVars(model, n);

        postLatinConstraints(model, x, n);
        postSymmetryBreaking(model, x, n);

        Solver solver = model.getSolver();
        solver.setSearch(Search.minDomLBSearch(ArrayUtils.flatten(x)));

        long t0 = System.nanoTime();
        int sol = 0;
        while (solver.solve()) {
            sol++;
            // printSquare(x);
        }
        long t1 = System.nanoTime();

        return new Result((t1 - t0) / 1e9, solver.getNodeCount(), sol);
    }

    /**
     * Modèle SBLS : carré latin + équilibrage des distances
     */
    static Result solveSBLS(int n) {
        Model model = new Model("SBLS_n" + n);
        IntVar[][] x = latinVars(model, n);

        postLatinConstraints(model, x, n);
        postSymmetryBreaking(model, x, n);
        IntVar K = postDistanceBalanceConstraints(model, x, n);

        Solver solver = model.getSolver();
        solver.setSearch(Search.domOverWDegSearch(ArrayUtils.flatten(x)));

        long t0 = System.nanoTime();
        int sol = 0;
        while (solver.solve()) {
            sol++;
            // System.out.println("K=" + K.getValue());
            // printSquare(x);
        }
        long t1 = System.nanoTime();

        return new Result((t1 - t0) / 1e9, solver.getNodeCount(), sol);
    }

    /** Crée n*n IntVar avec domaine 0..n-1 */
    static IntVar[][] latinVars(Model model, int n) {
        IntVar[][] x = new IntVar[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                x[r][c] = model.intVar("x_" + r + "_" + c, 0, n - 1);
            }
        }
        return x;
    }

    /** Contraintes de carré latin : allDifferent sur lignes et colonnes */
    static void postLatinConstraints(Model model, IntVar[][] x, int n) {
        for (int r = 0; r < n; r++) {
            model.allDifferent(x[r], "AC").post();
        }
        for (int c = 0; c < n; c++) {
            IntVar[] col = new IntVar[n];
            for (int r = 0; r < n; r++)
                col[r] = x[r][c];
            model.allDifferent(col, "AC").post();
        }
    }

    /** Bris de symétrie : première ligne et colonne = 0..n-1 */
    static void postSymmetryBreaking(Model model, IntVar[][] x, int n) {
        for (int c = 0; c < n; c++)
            model.arithm(x[0][c], "=", c).post();
        for (int r = 0; r < n; r++)
            model.arithm(x[r][0], "=", r).post();
    }

    /** Contraintes d'équilibrage SBLS */
    static IntVar postDistanceBalanceConstraints(Model model, IntVar[][] x, int n) {
        int cells = n * n;
        int[] rr = new int[cells];
        int[] cc = new int[cells];
        IntVar[] xv = ArrayUtils.flatten(x);

        for (int i = 0; i < cells; i++) {
            rr[i] = i / n;
            cc[i] = i % n;
        }

        // Booléens d'occupation : occ[p][v] <=> (x[p] == v)
        BoolVar[][] occ = model.boolVarMatrix("occ", cells, n);
        for (int p = 0; p < cells; p++) {
            for (int v = 0; v < n; v++) {
                model.reifyXeqC(xv[p], v, occ[p][v]);
            }
        }

        // Constante d'équilibre K
        int maxDist = 2 * (n - 1);
        int K_ub = n * n * maxDist;
        IntVar K = model.intVar("K", 0, K_ub);

        // Contraintes de somme pour chaque paire (a, b)
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                List<IntVar> ands = new ArrayList<>();
                List<Integer> wts = new ArrayList<>();

                for (int p = 0; p < cells; p++) {
                    for (int q = 0; q < cells; q++) {
                        int dist = Math.abs(rr[p] - rr[q]) + Math.abs(cc[p] - cc[q]);
                        if (dist == 0)
                            continue;

                        BoolVar z = model.boolVar();
                        model.addClausesBoolAndArrayEqVar(new BoolVar[] { occ[p][a], occ[q][b] }, z);

                        ands.add(z);
                        wts.add(dist);
                    }
                }

                // Somme pondérée == K
                model.scalar(ands.toArray(new IntVar[0]),
                        wts.stream().mapToInt(i -> i).toArray(),
                        "=", K).post();
            }
        }
        return K;
    }

    static void printSquare(IntVar[][] x) {
        int n = x.length;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                System.out.print(x[r][c].getValue() + (c + 1 == n ? "" : " "));
            }
            System.out.println();
        }
        System.out.println();
    }

    static class Result {
        final double timeSeconds;
        final long nodes;
        final int solutions;

        Result(double timeSeconds, long nodes, int solutions) {
            this.timeSeconds = timeSeconds;
            this.nodes = nodes;
            this.solutions = solutions;
        }
    }
}
