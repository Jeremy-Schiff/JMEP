package com.JMEP.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An evaluator stores Functions and evaluates EvaluableFunctions.
 *
 * @param <ValueType> The Type of the number that will result from the evaluation.
 */
class Evaluator<ValueType> {
    //the functions this Evaluator can use
    private final Map<String, Function<ValueType>> functions = new HashMap<>();

    /**
     * Adds the function into this Evaluator.
     *
     * @param function The function to add.
     */
    public void addFunction(Function<ValueType> function) {
        functions.put(function.getName(), function);
    }

    /**
     * Evaluates the following function:
     *
     * @param functionName The name of the function.
     * @param parameters   The parameters to place into the function.
     * @return The number resulting from the function.
     * @throws EvaluationException If the function cannot be evaluated.
     */
    public ValueType evaluate(String functionName, List<Evaluable<ValueType>> parameters) throws EvaluationException {
        List<ValueType> parameterValues = new ArrayList<>();
        for (Evaluable<ValueType> evaluableParam : parameters) {
            parameterValues.add(evaluableParam.evaluate(this));
        }
        //for parentheses that are alone (ex: (3) should evaluate as 3)
        if (functionName.isEmpty()) {
            return parameterValues.get(0);
        }
        Function<ValueType> func = functions.get(functionName);
        //if we did not find a function
        if (func == null) {
            //we cannot evaluate this expression
            throw new EvaluationException();
        }
        return func.evaluate(parameterValues);
    }
}
