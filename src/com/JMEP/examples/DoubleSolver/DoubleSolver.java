package com.JMEP.examples.DoubleSolver;

import com.JMEP.solver.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An example of the solver class that uses Doubles
 */
public class DoubleSolver extends Solver<Double> {

    public DoubleSolver() {
        super(multiplicationFunction());
        addFunction(new Function<Double>("sin") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                return Math.sin(parameters.get(0));
            }
        });
        addFunction(new Function<Double>("cos") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                return Math.cos(parameters.get(0));
            }
        });
        addFunction(new Function<Double>("tan") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                return Math.tan(parameters.get(0));
            }
        });
        addFunction(new Function<Double>("log") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                return Math.log10(parameters.get(0));
            }
        });
        addFunction(new Function<Double>("ln") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                return Math.log(parameters.get(0));
            }
        });
        addOperator('!', "factorial", OperatorType.ParameterBeforeOperator);
        addFunction(new Function<Double>("factorial") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                int number = (int) (double) parameters.get(0);
                int product = 1;
                for (int i = 1; i <= number; i++) {
                    product *= i;
                }
                return (double) product;
            }
        });
        addOperator('^', "power", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Double>("power") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return Math.pow(parameters.get(0), parameters.get(1));
            }
        });
        addSynonym("times", "*");
        addOperator('*', "multiply", OperatorType.ParametersBeforeAndAfterOperator);
        addOperator('/', "divide", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Double>("divide") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return parameters.get(0) / parameters.get(1);
            }
        });
        addOperator('+', "sum", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Double>("sum") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.isEmpty()) {
                    throw new EvaluationException();
                }
                double sum = 0;
                for (double toAdd : parameters) {
                    sum += toAdd;
                }
                return sum;
            }
        });
        addOperator('-', "difference", OperatorType.ParametersBeforeAndAfterOperator);
        addFunction(new Function<Double>("difference") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.isEmpty()) {
                    throw new EvaluationException();
                }
                return parameters.get(0) - (parameters.get(1));
            }
        });
    }

    private static Function<Double> multiplicationFunction() {
        return new Function<Double>("multiply") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.isEmpty()) {
                    throw new EvaluationException();
                }
                double product = 1;
                for (double toMultiply : parameters) {
                    product *= toMultiply;
                }
                return product;
            }
        };
    }

    @Override
    public Double toValue(String value) throws ParsingException {
        if (value.equals("")) {
            return 0.0;
        }
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            throw new ParsingException();
        }
    }

    @Override
    public Map<Character, Character> getNumberWrappers() {
        return new HashMap<>();
    }
}
