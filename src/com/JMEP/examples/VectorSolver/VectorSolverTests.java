package com.JMEP.examples.VectorSolver;


import com.JMEP.solver.Function;

import java.util.List;

/**
 * A series of tests for the VectorSolver.
 */
class VectorSolverTests {
    private static final VectorSolver solver = new VectorSolver();

    public static void main(String[] args) throws Exception {
        solver.addFunction(new Function<DoubleVector>("projection") {
            @Override
            public DoubleVector evaluate(List<DoubleVector> parameters) {
                double numerator = parameters.get(0).multiply(parameters.get(1)).magnitude();
                double denominator = parameters.get(1).multiply(parameters.get(1)).magnitude();
                return parameters.get(1).scale(numerator / denominator);
            }
        });
        solver.addSynonym(" ", "");
        solver.addSynonym("onto", ",");
        solver.addSynonym("of", "(");
        test("<1>*<2>", new DoubleVector(new double[]{2}));
        test("<1,2>*<2,3>", new DoubleVector(new double[]{8}));
        test("<1,2>(<2,3>)", new DoubleVector(new double[]{8}));
        test("<1,2><2,3>", new DoubleVector(new double[]{8}));
        test("<1,2>+<2,3>", new DoubleVector(new double[]{3, 5}));
        test("-<2,3>", new DoubleVector(new double[]{-2, -3}));
        test("<3,4>-<2,3>", new DoubleVector(new double[]{1, 1}));
        test("projection of <1,2> onto <2,1>", new DoubleVector(new double[]{8.0 / 5, 4.0 / 5}));
        System.out.println("Tests Complete");
    }

    private static void test(String test, DoubleVector correct) {
        try {
            DoubleVector result = solver.solve(test);
            if (!result.equals(correct)) {
                System.out.println("Failed: " + test + " gave:" + result + " want:" + correct);
            }
        } catch (Exception e) {
            System.out.println("Failed: " + test + " exception:");
            e.printStackTrace();
        }
    }
}
