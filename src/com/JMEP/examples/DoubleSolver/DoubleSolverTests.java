package com.JMEP.examples.DoubleSolver;


import com.JMEP.solver.EvaluationException;
import com.JMEP.solver.Function;
import com.JMEP.solver.OperatorType;
import com.JMEP.solver.ParsingException;

import java.util.List;

/**
 * A series of tests for the DoubleSolver.
 */
class DoubleSolverTests {
    private static final DoubleSolver solver = new DoubleSolver();

    public static void main(String[] args) throws Exception {
        solver.addFunction(new Function<Double>("star") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                int number = (int) (double) parameters.get(0) + 1;
                int product = 1;
                for (int i = 1; i <= number; i++) {
                    product *= i;
                }
                return (double) product;
            }
        });
        solver.addOperator('★', "star", OperatorType.ParameterAfterOperator);
        test("add(3,4)", 7);
        test("add(3,4", 7);
        test("add(3,4,5", 12);
        test("(add(3,4))", 7);
        test("add((3),(4))", 7);
        test("(add((3),(4))", 7);
        test("(add((3),(4", 7);
        test("subtract(3,add(4,5))", -6);
        test("subtract(3,add(4,5)", -6);
        test("subtract(3,add(4,5", -6);
        test("add(3,subtract(4,5", 2);
        test("multiply(4,5", 20);
        test("multiply(4,5,6", 120);
        test("4(5)", 20);
        test("4(5", 20);
        test("(4)(5)", 20);
        test("(4)(5", 20);
        test("(4)5", 20);
        test("4(5)6", 120);
        test("5add(3,subtract(4,5", 10);
        test("5(add(3,subtract(4,5", 10);
        test("3!", 6);
        test("add(3,3!)", 9);
        test("add(3!,3)", 9);
        test("add(3!,3)2", 18);
        test("add((3!),3)2", 18);
        test("3!!", 720);
        test("★2", 6);
        test("add(3,★2)", 9);
        test("add(★2,3)", 9);
        test("add(★2,3)2", 18);
        test("add((★2),3)2", 18);
        test("★★2", 5040);
        test("2+2", 4);
        test("(2)+2", 4);
        test("(2)+(2)", 4);
        test("(2)+(2", 4);
        test("multiply((2)+(2),3", 12);
        test("2!+★2", 8);
        test("2!★2", 12);
        test("(★2)(★2)", 36);
        test("★2(★2)", 36);
        test("(★2)★2", 36);
        test("4times5", 20);
        test("3+3*3", 12);
        test("3*3+3", 12);
        test("-3", -3);
        test("--3", 3);
        test("3/4", 0.75);
        test("3^4", 81);
        test("4-3^4-4", -81);
        test("cos(0)", 1);
        test("sin(0)", 0);
        test("tan(0)", 0);
        test("log(10)", 1);
        test("ln(" + Math.E + ")", 1);
        System.out.println("Tests Complete");
    }

    private static void test(String test, double correct) {
        try {
            double result = solver.solve(test);
            if (result != correct) {
                System.out.println("Failed: " + test + " gave:" + result + " want:" + correct);
            }
        } catch (ParsingException e) {
            System.out.println("Failed to parse: " + test + " exception:");
            e.printStackTrace();
        } catch (EvaluationException e) {
            System.out.println("Failed to evaluate: " + test + " exception:");
            e.printStackTrace();
        }
    }
}
