package com.JMEP.examples.VectorSolver;

import com.JMEP.solver.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An example of the solver class that uses DoubleVectors
 */
public class VectorSolver extends Solver<DoubleVector> {

    public VectorSolver() {
        super(multiplicationFunction());
        addOperator('*', "multiply", OperatorType.ParametersBeforeAndAfterOperator);
        addOperator('+', "add", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<DoubleVector>("add") {
            @Override
            public DoubleVector evaluate(List<DoubleVector> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return parameters.get(0).add(parameters.get(1));
            }
        });
        addOperator('-', "subtract", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<DoubleVector>("subtract") {
            @Override
            public DoubleVector evaluate(List<DoubleVector> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return parameters.get(0).subtract(parameters.get(1));
            }
        });
    }

    private static Function<DoubleVector> multiplicationFunction() {
        return new Function<DoubleVector>("multiply") {
            @Override
            public DoubleVector evaluate(List<DoubleVector> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return parameters.get(0).multiply(parameters.get(1));
            }
        };
    }

    @Override
    public DoubleVector toValue(String value) throws ParsingException {
        if (value.equals("")) {
            return new DoubleVector(new double[]{});
        }
        return DoubleVector.valueOf(value);
    }

    @Override
    public Map<Character, Character> getNumberWrappers() {
        Map<Character, Character> out = new HashMap<>();
        out.put('<', '>');
        return out;
    }
}
