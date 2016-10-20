package com.JMEP.examples.DoubleArraySolver;

import com.JMEP.solver.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An example of the solver class that uses Double Arrays.
 */
public class DoubleArraySolver extends Solver<Double[]> {

    public DoubleArraySolver() {
        super(multiplicationFunction());
        addSynonym("times", "*");
        addOperator('*', "multiply", OperatorType.ParametersBeforeAndAfterOperator);
        addOperator('+', "sum", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Double[]>("sum") {
            @Override
            public Double[] evaluate(List<Double[]> parameters) throws EvaluationException {
                if (parameters.isEmpty()) {
                    throw new EvaluationException();
                }
                Double[] sum = parameters.get(0);
                parameters.remove(0);
                for (Double[] toAdd : parameters) {
                    if (toAdd.length != sum.length) {
                        throw new EvaluationException();
                    }
                    for (int index = 0; index < sum.length; index++) {
                        sum[index] += toAdd[index];
                    }
                }
                return sum;
            }
        });
        addOperator('-', "difference", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Double[]>("difference") {
            @Override
            public Double[] evaluate(List<Double[]> parameters) throws EvaluationException {
                if (parameters.size() < 2) {
                    throw new EvaluationException();
                }
                Double[] out = parameters.get(0);
                //special empty case
                if (Arrays.equals(out, new Double[]{})) {
                    out = new Double[parameters.get(1).length];
                    for (int index = 0; index < out.length; index++) {
                        out[index] = (double) 0;
                    }
                }
                Double[] toSubtract = parameters.get(1);
                if (toSubtract.length != out.length) {
                    throw new EvaluationException();
                }
                for (int index = 0; index < out.length; index++) {
                    out[index] -= toSubtract[index];
                }
                return out;
            }
        });
    }

    private static Function<Double[]> multiplicationFunction() {
        return new Function<Double[]>("multiply") {
            @Override
            public Double[] evaluate(List<Double[]> parameters) throws EvaluationException {
                if (parameters.isEmpty()) {
                    throw new EvaluationException();
                }
                Double[] product = new Double[parameters.get(0).length];
                for (int index = 0; index < product.length; index++) {
                    product[index] = (double) 1;
                }
                for (Double[] toMultiply : parameters) {
                    if (toMultiply.length != product.length) {
                        throw new EvaluationException();
                    }
                    for (int index = 0; index < product.length; index++) {
                        product[index] *= toMultiply[index];
                    }
                }
                return product;
            }
        };
    }

    @Override
    public Double[] toValue(String value) throws ParsingException {
        if (value.equals("")) {
            return new Double[]{};
        }
        try {
            value = value.replace("[", "");
            value = value.replace(" ", "");
            value = value.replace("]", "");
            String[] doubles = value.split(",");
            Double[] out = new Double[doubles.length];
            for (int index = 0; index < doubles.length; index++) {
                out[index] = Double.valueOf(doubles[index]);
            }
            return out;
        } catch (Exception e) {
            throw new ParsingException();
        }
    }

    @Override
    public Map<Character, Character> getNumberWrappers() {
        HashMap<Character, Character> out = new HashMap<>();
        out.put('[', ']');
        return out;
    }
}
