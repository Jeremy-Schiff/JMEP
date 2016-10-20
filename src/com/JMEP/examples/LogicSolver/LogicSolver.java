package com.JMEP.examples.LogicSolver;

import com.JMEP.solver.*;

import java.util.*;

/**
 * An example of the solver class that uses Booleans
 */
public class LogicSolver extends Solver<Boolean> {

    public LogicSolver() {
        addFunction(new Function<Boolean>("not") {
            @Override
            public Boolean evaluate(List<Boolean> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                return !parameters.get(0);
            }
        });
        addOperator('~', "not", OperatorType.ParameterAfterOperator);
        addFunction(new Function<Boolean>("and") {
            @Override
            public Boolean evaluate(List<Boolean> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return parameters.get(0) && parameters.get(1);
            }
        });
        addOperator('^', "and", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Boolean>("or") {
            @Override
            public Boolean evaluate(List<Boolean> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return parameters.get(0) || parameters.get(1);
            }
        });
        addOperator('∨', "or", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Boolean>("implies") {
            @Override
            public Boolean evaluate(List<Boolean> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return !parameters.get(0) || parameters.get(1);
            }
        });
        addOperator('⇒', "implies", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Boolean>("biconditional") {
            @Override
            public Boolean evaluate(List<Boolean> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return parameters.get(0).equals(parameters.get(1));
            }
        });
        addOperator('⇔', "biconditional", OperatorType.ParametersBeforeAndAfterOperator);
    }

    /**
     * Gives all possible True and False values for a set of variables for use in constructing a truth table.
     *
     * @param variables The set of variables to use.
     * @return A list of maps from variables to the values they should hold in that table entry.
     */
    private static List<Map<String, String>> getTruthTableInputs(Set<String> variables) {
        //base case, one true statement one false one
        if (variables.size() == 1) {
            List<Map<String, String>> out = new ArrayList<>();
            Map<String, String> truth = new HashMap<>();
            truth.put(variables.iterator().next(), "True");
            Map<String, String> unTruth = new HashMap<>();
            unTruth.put(variables.iterator().next(), "False");
            out.add(truth);
            out.add(unTruth);
            return out;
        }
        //add the first variable to the map
        String firstVar = variables.iterator().next();
        variables.remove(firstVar);
        List<Map<String, String>> out = new ArrayList<>();
        List<Map<String, String>> previous = getTruthTableInputs(variables);
        //each map now needs to have a true and a false copy
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

    /**
     * Tests if an expression is satisfiable ie: there are a set of values that the variables in the
     * expression could be set to in order to make the expression true.
     *
     * @param test The expression to test.
     * @return True if such a set exists, false otherwise.
     * @throws EvaluationException If there is an error in evaluation
     * @throws ParsingException    If there is an error in parsing
     */
    public boolean isSatisifiable(String test) throws ParsingException, EvaluationException {
        Set<String> variables = getUndefinedVariables(test);
        //iterate over all possible inputs
        for (Map<String, String> entry : getTruthTableInputs(variables)) {
            //use those inputs
            for (Map.Entry<String, String> mapVal : entry.entrySet()) {
                addVariable(mapVal.getKey(), mapVal.getValue());
            }
            //check if the statement is true
            if (solve(test)) {
                clearVariables();
                return true;
            }
            clearVariables();
        }
        return false;
    }

    @Override
    public Map<Character, Character> getNumberWrappers() {
        return new HashMap<>();
    }

    @Override
    public Boolean toValue(String value) throws ParsingException {
        if (value.equalsIgnoreCase("True")) {
            return true;
        }
        if (value.equalsIgnoreCase("False")) {
            return false;
        }
        throw new ParsingException();
    }
}
