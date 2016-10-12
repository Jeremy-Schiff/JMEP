package com.JMEP.solver;

/**
 * An EvaluableNum is a value that can be evaluated to a number.
 *
 * @param <ValueType> The Type of the number that will result from the evaluation.
 */
class EvaluableNum<ValueType> extends Evaluable<ValueType> {
    //the value of this evaluable
    private final ValueType value;

    /**
     * Creates an EvaluableNum with the following:
     *
     * @param number The value this will evaluate to.
     */
    EvaluableNum(ValueType number) {
        value = number;
    }

    @Override
    public ValueType evaluate(Evaluator<ValueType> evaluator) {
        return value;
    }
}
