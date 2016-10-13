package com.JMEP.examples.LogicSolver;


import com.JMEP.solver.EvaluationException;
import com.JMEP.solver.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A series of tests for the LogicSolver.
 */
class LogicSolverTests {
    private static final LogicSolver solver = new LogicSolver();

    public static void main(String[] args) throws Exception {
        test("False⇒True", true);
        test("False^True", false);
        test("False∨True", true);
        test("False⇔False", true);
        test("~False", true);
        testIsTautology("P^Q", false);
        testIsTautology("P∨~P", true);
        System.out.println("Tests Complete");
    }

    private static List<Map<String, String>> getTruthTable(List<String> variables) {
        if (variables.size() == 1) {
            List<Map<String, String>> out = new ArrayList<>();
            Map<String, String> truth = new HashMap<>();
            truth.put(variables.get(0), "True");
            Map<String, String> unTruth = new HashMap<>();
            unTruth.put(variables.get(0), "False");
            out.add(truth);
            out.add(unTruth);
            return out;
        }
        String firstVar = variables.get(0);
        variables.remove(0);
        List<Map<String, String>> out = new ArrayList<>();
        List<Map<String, String>> previous = getTruthTable(variables);
        for (Map<String, String> oldMap : previous) {
            Map<String, String> truth = new HashMap<>();
            truth.put(firstVar, "True");
            truth.putAll(oldMap);
            Map<String, String> unTruth = new HashMap<>();
            unTruth.put(firstVar, "False");
            unTruth.putAll(oldMap);
            out.add(truth);
            out.add(unTruth);
        }
        return out;
    }

    private static boolean isTautology(String test) throws ParsingException, EvaluationException {
        List<String> vars = solver.getUndefinedVariables(test);
        for (Map<String, String> entry : getTruthTable(vars)) {
            for (Map.Entry<String, String> mapVal : entry.entrySet()) {
                solver.addVariable(mapVal.getKey(), mapVal.getValue());
            }
            if (!solver.solve(test)) {
                solver.clearVariables();
                return false;
            }
            solver.clearVariables();
        }
        return true;
    }

    private static void testIsTautology(String test, boolean correct) {
        try {
            boolean result = isTautology(test);
            if (result != correct) {
                System.out.println("Failed: " + test + " gave:" + result + " want:" + correct);
            }
        } catch (Exception e) {
            System.out.println("Failed: " + test + " exception:");
            e.printStackTrace();
        }
    }

    private static void test(String test, boolean correct) {
        try {
            boolean result = solver.solve(test);
            if (result != correct) {
                System.out.println("Failed: " + test + " gave:" + result + " want:" + correct);
            }
        } catch (Exception e) {
            System.out.println("Failed: " + test + " exception:");
            e.printStackTrace();
        }
    }
}
