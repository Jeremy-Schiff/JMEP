package com.JMEP.examples.LogicSolver;

import com.JMEP.solver.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
