package com.JMEP.solver;

/**
 * An Operator is a character that performs a function on the parameters before and/or after it like '+' or '!'.
 */
class Operator {
    //the character of the operator (ie '+')
    private final char operatorName;
    //the name of the function this operator represents
    private final String functionName;
    //the parser to use in evaluating this operator
    private final Parser parser;
    //the type of operator this is, based on what parameters it uses
    private final OperatorType type;

    /**
     * Constructs an operator with the following:
     *
     * @param name         The operator's name
     * @param function     The function the operator represents
     * @param parserToUse  The parser to use in evaluating this operator
     * @param operatorType The type of operator this is, based on what parameters it uses
     */
    public Operator(char name, String function, Parser parserToUse, OperatorType operatorType) {
        operatorName = name;
        functionName = function;
        type = operatorType;
        parser = parserToUse;
    }

    /**
     * A getter for the operator's name.
     *
     * @return The name of the operator
     */
    public char getOperatorName() {
        return operatorName;
    }

    /**
     * Evaluates the first index of this operator in the given String
     *
     * @param problem The String to evaluate
     * @return A modified version of the String that has updated based on this operator
     */
    private String evaluateFirstIndex(String problem) {
        int operatorIndex = problem.indexOf(operatorName);
        //the section prior to the function
        String startSection = problem.substring(0, operatorIndex);
        //the parameter before the operator
        String beforeParamSection = "";
        //the parameter after the operator
        String afterParamSection = "";
        //the section after the function
        String endSection = problem.substring(operatorIndex + 1);
        if (type == OperatorType.ParameterAfterOperator || type == OperatorType.ParametersBeforeAndAfterOperator) {
            int paramIndex = 0;
            boolean seenNonOperator = false;
            boolean completed = false;
            //find where the parameter breaks at
            while (paramIndex < endSection.length() && !completed) {
                char test = endSection.charAt(paramIndex);
                if (parser.getOperators().contains(test)) {
                    //we cannot break the parameter on an operator that isn't preceded by a non operator to solve issues with operators next to eachother
                    completed = seenNonOperator;
                } else {
                    //we have seen a non operator
                    seenNonOperator = true;
                }
                if (parser.getSeperators().contains(test)) {
                    //break the parameter here
                    completed = true;
                }
                paramIndex++;
            }
            //the above algorithm overcounts by one
            paramIndex--;
            //if we are at opening wrapper like '(', the parameter goes until the ending wrapper
            if (parser.getOpeners().contains(endSection.charAt(paramIndex))) {
                String functArea = endSection.substring(0, paramIndex);
                //don't separate things like f(x) into f, (x)
                if (functArea.isEmpty() || parser.hasFunction(functArea)) {
                    paramIndex = parser.getIndexOfCloser(paramIndex, endSection) + 1;
                }
            }
            afterParamSection = endSection.substring(0, paramIndex);
            endSection = endSection.substring(paramIndex);
        }
        //do the same algorithm as above, but in reverse
        if (type == OperatorType.ParameterBeforeOperator || type == OperatorType.ParametersBeforeAndAfterOperator) {
            boolean seenNonOperator = false;
            boolean completed = false;
            int paramIndex = startSection.length() - 1;
            while (paramIndex >= 0 && !completed) {
                char test = startSection.charAt(paramIndex);
                if (parser.getOperators().contains(test)) {
                    completed = seenNonOperator;
                } else {
                    seenNonOperator = true;
                }
                if (parser.getSeperators().contains(test)) {
                    completed = true;
                }
                paramIndex--;
            }
            paramIndex++;
            if (parser.getClosers().contains(startSection.charAt(paramIndex))) {
                paramIndex = parser.getIndexOfOpener(paramIndex, startSection) - 1;
            }
            beforeParamSection = startSection.substring(paramIndex + 1);
            startSection = startSection.substring(0, paramIndex + 1);

        }
        String commaSection = "";
        //we need a comma if both parameters are to be used
        if (type == OperatorType.ParametersBeforeAndAfterOperator) {
            commaSection = Parser.comma + "";
        }
        return startSection + Parser.openingParentheses + functionName + Parser.openingParentheses + beforeParamSection
                + commaSection + afterParamSection + Parser.closedParentheses + Parser.closedParentheses + endSection;
    }

    /**
     * Resolves all indices of this operator
     *
     * @param problem The String to evaluate
     * @return A modified version of the String that has updated based on this operator
     */
    public String evaluate(String problem) {
        //as long as the operator is in the String, update it
        while (problem.indexOf(operatorName) != -1) {
            problem = evaluateFirstIndex(problem);
        }
        return problem;
    }
}