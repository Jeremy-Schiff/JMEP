package com.JMEP.solver;

import java.util.Map;

/**
 * Solves a mathematical expression in String form
 *
 * @param <ValueType> The Type of the Solver that the function will take in and will result from the evaluation.
 */
public abstract class Solver<ValueType> {
    //the evaluator to use in solving
    private final Evaluator<ValueType> evaluator;
    //the parser to use in solving
    private final Parser<ValueType> parser;

    /**
     * Constructs a Solver with no operators, no synonyms, and the following function:
     *
     * @param implicitMultiplicationFunction The function name to be used in implicit multiplication ie: 3(4)
     */
    protected Solver(Function<ValueType> implicitMultiplicationFunction) {
        evaluator = new Evaluator<>();
        parser = new Parser<>(this, implicitMultiplicationFunction.getName());
        addFunction(implicitMultiplicationFunction);
    }

    /**
     * Adds a synonym to the Solver
     *
     * @param synonym The string to be replaced
     * @param result  The string to replace it with
     */
    public void addSynonym(String synonym, String result) {
        parser.addSynonym(synonym, result);
    }

    /**
     * /**
     * Adds a operator to this parser
     *
     * @param opName   The name of the operator
     * @param funcName The name of the function the operator represents
     * @param type     The type of operator the operator is
     */
    public void addOperator(char opName, String funcName, OperatorType type) {
        parser.addOperator(opName, funcName, type);
    }

    /**
     * Adds a function to the solver
     *
     * @param func The function to add
     */
    public void addFunction(Function<ValueType> func) {
        evaluator.addFunction(func);
        parser.addFunction(func.getName());
    }

    /**
     * Solves a mathematical expression
     *
     * @param problem The expression to solve
     * @return A number representing the solution
     * @throws EvaluationException If there is an error in evaluation
     * @throws ParsingException    If there is an error in parsing
     */
    public ValueType solve(String problem) throws EvaluationException, ParsingException {
        Evaluable<ValueType> parsedProblem = parser.parse(problem);
        return parsedProblem.evaluate(evaluator);
    }

    /**
     * Returns a value from a String representation. Note most implementations will want "" to return some sort of 0 in
     * order to make -X represent subtract(0,X). Likewise, the subtract function should be able to evaluate that to the opposite
     * of X
     *
     * @param value The String representation of the value
     * @return A number representing the String
     * @throws ParsingException If the String cannot be converted into a value
     */
    public abstract ValueType toValue(String value) throws ParsingException;

    /**
     * Returns the wrappers around a value. For example, in a vector space, < and > can be wrappers
     *
     * @return A map of opening wrappers to closing wrappers
     */
    public abstract Map<Character, Character> getNumberWrappers();
}
