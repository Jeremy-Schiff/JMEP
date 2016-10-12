package com.JMEP.solver;

import java.util.List;

/**
 * An EvaluableFunction is a function and a set of parameters that can be evaluated to a number.
 *
 * @param <ValueType> The Type of the number that will result from the evaluation.
 */
class EvaluableFunction<ValueType> extends Evaluable<ValueType> {
    //the name of the function to use
    private final String name;
    //the list of parameters to be placed into the function
    private final List<Evaluable<ValueType>> parameters;

    /**
     * Creates an EvaluableFunction based on the following:
     *
     * @param functionName        The name of the function to use in evaluation
     * @param evaluableParameters The parameters to place into the function
     */
    public EvaluableFunction(String functionName, List<Evaluable<ValueType>> evaluableParameters) {
        name = functionName;
        parameters = evaluableParameters;
    }

    @Override
    public ValueType evaluate(Evaluator<ValueType> evaluator) throws EvaluationException {
        return evaluator.evaluate(name, parameters);
    }
}