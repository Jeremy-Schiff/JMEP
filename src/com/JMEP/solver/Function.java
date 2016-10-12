package com.JMEP.solver;

import java.util.List;

/**
 * A function has a name and a way to evaluate a list of parameters.
 *
 * @param <ValueType> The Type of the number that the function will take in and will result from the evaluation.
 */
public abstract class Function<ValueType> {
    //the name of the function
    private final String name;

    /**
     * Creates a function with the following:
     *
     * @param functionName The name of the function.
     */
    public Function(String functionName) {
        name = functionName;
    }

    /**
     * A getter for the name of the function.
     *
     * @return The function's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Evaluates the following:
     *
     * @param parameters The parameters to use in the evaluation.
     * @return The result of those parameters when placed in the function.
     * @throws EvaluationException If there is an error in the evaluation
     */
    public abstract ValueType evaluate(List<ValueType> parameters) throws EvaluationException;
}