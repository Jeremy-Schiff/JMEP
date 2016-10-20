package com.JMEP.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

/**
 * A ProtoFunction is an intermediate step between a String and an Evaluable.
 * It is merely for use, it has no real mathematical meaning.
 */
class ProtoFunction {
    //the list of parameters to this function
    private List<ProtoFunction> params = new ArrayList<>();
    //the name of this function or value
    private String name;

    /**
     * Constructs a ProtoFunction with the following:
     *
     * @param func   The name of the function
     * @param parser The parser to use in construction
     */
    public ProtoFunction(String func, Parser parser) {
        @SuppressWarnings("unchecked") List<String> sections = parser.splitParenthesesSections(func);
        if (sections.size() > 1) {
            //this must be implicit multiplication like 3(4)
            name = parser.implicitMultiplicationFunctionName();
            params.addAll(sections.stream().map(section -> new ProtoFunction(section, parser)).collect(Collectors.toList()));
        } else if (func.indexOf(Parser.openingParentheses) != -1) {
            //this must be a single function like f(X)
            name = func.substring(0, func.indexOf(Parser.openingParentheses));
            //if this ends with a closing parentheses, remove that when passing parameters
            if (func.charAt(func.length() - 1) == Parser.closedParentheses) {
                func = func.substring(0, func.length() - 1);
            }
            String paramString = func.substring(func.indexOf(Parser.openingParentheses) + 1);
            params = toParamList(paramString, parser);
        } else {
            //this is just a number like 3
            name = func;
        }
    }

    /**
     * Creates a list of parameters based on:
     *
     * @param params The string containing the parameters
     * @param parser The parser to use in construction
     * @return A list of ProtoFunctions representing parameters
     */
    private static List<ProtoFunction> toParamList(String params, Parser parser) {
        List<ProtoFunction> paramList = new ArrayList<>();
        @SuppressWarnings("unchecked") SortedSet<Integer> commaIndices = parser.getCommasOutsideOfWrappers(params);
        int lastCommaIndex = 0;
        if (commaIndices.size() != 0) {
            for (int commaIndex : commaIndices) {
                String nextParam = params.substring(lastCommaIndex, commaIndex);
                paramList.add(new ProtoFunction(nextParam, parser));
                //pass over the comma
                lastCommaIndex = commaIndex + 1;
            }
        }
        paramList.add(new ProtoFunction(params.substring(lastCommaIndex), parser));
        return paramList;
    }

    /**
     * Checks if this ProtoFunction has been simplified completely ie: there are no parameters
     *
     * @return True if it is simple, false otherwise
     */
    public boolean isSimple() {
        return params.isEmpty();
    }

    /**
     * A getter for the data (name or value) of this function
     *
     * @return The data String
     */
    public String getData() {
        return name;
    }

    /**
     * Updates the data (name or value) of this function
     *
     * @param data The new data String
     */
    public void updateData(String data) {
        name = data;
    }

    /**
     * Returns the parameters of this function
     *
     * @return A list of parameters
     */
    public List<ProtoFunction> getParams() {
        return params;
    }
}
