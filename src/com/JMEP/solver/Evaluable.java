package com.JMEP.solver;

/**
 * An Evaluable is a thing that can be evaluated: either a number or a function with parameters.
 *
 * @param <ValueType> The Type of the number that will result from the evaluation.
 */
public abstract class Evaluable<ValueType> {
    /**
     * Evaluates this evaluable then returns a value.
     *
     * @param evaluator The evaluator to be used to evaluate this.
     * @return The value this evaluates to.
     * @throws EvaluationException If there is an error in the evaluation.
     */
    public abstract ValueType evaluate(Evaluator<ValueType> evaluator) throws EvaluationException;
}
