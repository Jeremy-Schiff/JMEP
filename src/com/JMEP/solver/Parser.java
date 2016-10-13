package com.JMEP.solver;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Parses a String input into a mathematical expression
 *
 * @param <ValueType> The Type of the number that the parser will user.
 */
class Parser<ValueType> {

    public static final char openingParentheses = '(';
    public static final char closedParentheses = ')';
    public static final char comma = ',';
    //a list of the synonyms to be used in parsing ie replacing all instances of "sum" to "add"
    private final Map<String, String> synonyms = new HashMap<>();
    //a list of the variables to be used in parsing ie replacing all instances of "x" to "3"
    private final Map<String, String> variables = new HashMap<>();
    //a list of the operators this parser needs to account for
    private final List<Operator> operators = new ArrayList<>();
    //a list of the function names in this parser
    private final List<String> functions = new ArrayList<>();
    //the solver that is using this parser
    private final Solver<ValueType> solver;
    //the name of the function to be used in implicit multiplication ie: 3(4)
    private final String implicitMultFuncName;

    /**
     * Creates a parser with the following:
     *
     * @param owningSolver                       the solver that is using this parser
     * @param implicitMultiplicationFunctionName the name of the function to be used in implicit multiplication ie: 3(4)
     */
    public Parser(Solver<ValueType> owningSolver, String implicitMultiplicationFunctionName) {
        solver = owningSolver;
        implicitMultFuncName = implicitMultiplicationFunctionName;
    }

    /**
     * Adds a synonym to this parser
     *
     * @param synonym The string to be replaced
     * @param result  The string to replace it with
     */
    public void addSynonym(String synonym, String result) {
        synonyms.put(synonym, result);
    }

    /**
     * Adds a variable to this parser
     *
     * @param synonym The string to be replaced
     * @param result  The string to replace it with
     */

    public void addVariable(String synonym, String result) {
        variables.put(synonym, result);
    }

    /**
     * Clears all variable values in the solver
     */
    public void clearVariables() {
        variables.clear();
    }

    /**
     * Adds a function to this parser
     *
     * @param functionName The name of the function
     */
    public void addFunction(String functionName) {
        functions.add(functionName);
    }

    /**
     * Adds a operator to this parser
     *
     * @param opName       The name of the operator
     * @param funcName     The name of the function the operator represents
     * @param operatorType The type of operator the operator is
     */
    public void addOperator(char opName, String funcName, OperatorType operatorType) {
        operators.add(new Operator(opName, funcName, this, operatorType));
    }

    /**
     * Gives a set of characters that can separate values
     *
     * @return A set of such characters
     */
    public Set<Character> getSeperators() {
        Set<Character> seperators = new HashSet<>();
        seperators.addAll(getClosers());
        seperators.addAll(getOpeners());
        seperators.add(comma);
        return seperators;
    }

    /**
     * Gives a set of operators that can be used in parsing
     *
     * @return A set of such characters
     */
    public Set<Character> getOperators() {
        return operators.stream().map(Operator::getOperatorName).collect(Collectors.toSet());
    }

    /**
     * Gives a map from openers to closer in number wrappers
     *
     * @return A map of such wrappers.
     */
    private Map<Character, Character> getWrappers() {
        Map<Character, Character> out = solver.getNumberWrappers();
        out.put(openingParentheses, closedParentheses);
        return out;
    }

    /**
     * Gives a set of closing wrappers that can be used in parsing
     *
     * @return A set of such characters
     */
    public Set<Character> getClosers() {
        Set<Character> out = new HashSet<>();
        out.addAll(getWrappers().values());
        return out;
    }

    /**
     * Gives a set of opening wrappers that can be used in parsing
     *
     * @return A set of such characters
     */
    public Set<Character> getOpeners() {
        Set<Character> out = new HashSet<>();
        out.addAll(getWrappers().keySet());
        return out;
    }

    /**
     * Gives the function to be used in implicit multiplication
     *
     * @return the name of the function
     */
    public String implicitMultiplicationFunctionName() {
        return implicitMultFuncName;
    }

    /**
     * Gives a set of indices corresponding to commas that are not inside of wrappers
     *
     * @param problem      The string to search
     * @param openWrapper  The character representing the opening wrapper
     * @param closeWrapper The character representing the closing wrapper
     * @return The set of indices
     */
    private SortedSet<Integer> getCommasOutsideOfWrappers(String problem, char openWrapper, char closeWrapper) {
        SortedSet<Integer> output = new TreeSet<>();
        int index = 0;
        int unclosedOpeners = 0;
        while (index < problem.length()) {
            char test = problem.charAt(index);
            if (test == openWrapper) {
                unclosedOpeners++;
            }
            if (test == closeWrapper) {
                unclosedOpeners--;
            }
            if (test == comma && unclosedOpeners == 0) {
                output.add(index);
            }
            index++;
        }
        return output;
    }

    /**
     * Gives a set of indices corresponding to commas that are not inside of wrappers
     *
     * @param problem  The string to search
     * @param wrappers A map of opening wrappers to closing wrappers
     * @return The set of indices
     */
    private SortedSet<Integer> getCommasOutsideOfWrappers(String problem, Map<Character, Character> wrappers) {
        //get the corresponding set for each wrapper pair
        Set<SortedSet<Integer>> commas = wrappers.entrySet().stream().map(entry -> getCommasOutsideOfWrappers(problem, entry.getKey(), entry.getValue())).collect(Collectors.toSet());
        SortedSet<Integer> output = null;
        //perform intersection on the sets
        for (SortedSet<Integer> set : commas) {
            if (output == null) {
                output = set;
            }
            output.retainAll(set);
        }
        if (output == null) {
            output = new TreeSet<>();
        }
        return output;
    }

    /**
     * Gives a set of indices corresponding to commas that are not inside of wrappers
     *
     * @param problem The string to search
     * @return The set of indices
     */
    public SortedSet<Integer> getCommasOutsideOfWrappers(String problem) {
        return getCommasOutsideOfWrappers(problem, getWrappers());
    }

    /**
     * Returns the index of the corresponding closer
     *
     * @param openingIndex  The index with the opener
     * @param stringToCheck The String to examine
     * @return The index of the closer
     */
    public int getIndexOfCloser(int openingIndex, String stringToCheck) {
        int numberOfOpeners = 1;
        int numberOfClosers = 0;
        int currentIndex = openingIndex + 1;
        char opener = stringToCheck.charAt(openingIndex);
        char closer = getWrappers().get(opener);
        //iterate until we reach the closer or end of string
        while ((currentIndex < stringToCheck.length()) && (numberOfClosers != numberOfOpeners)) {
            if (stringToCheck.charAt(currentIndex) == opener) {
                numberOfOpeners++;
            }
            if (stringToCheck.charAt(currentIndex) == closer) {
                numberOfClosers++;
            }
            currentIndex++;
        }
        if (numberOfClosers != numberOfOpeners)
            return currentIndex;
        //the algorithm overcounted by one
        return currentIndex - 1;
    }

    /**
     * Returns the index of the corresponding opener
     *
     * @param closingIndex  The index with the closer
     * @param stringToCheck The String to examine
     * @return The index of the opener
     */
    public int getIndexOfOpener(int closingIndex, String stringToCheck) {
        int numberOfOpeners = 0;
        int numberOfClosers = 1;
        int currentIndex = closingIndex - 1;
        char closer = stringToCheck.charAt(closingIndex);
        char opener = ' ';
        //find the corresponding opening char
        for (Map.Entry<Character, Character> entry : getWrappers().entrySet()) {
            if (entry.getValue() == closer) {
                opener = entry.getKey();
            }
        }
        //iterate until we reach the opener or beginning of string
        while ((currentIndex > 0) && (numberOfClosers != numberOfOpeners)) {
            if (stringToCheck.charAt(currentIndex) == opener) {
                numberOfOpeners++;
            }
            if (stringToCheck.charAt(currentIndex) == closer) {
                numberOfClosers++;
            }
            currentIndex--;
        }
        if (numberOfClosers != numberOfOpeners)
            return currentIndex;
        //the algorithm undercounted by one
        return currentIndex + 1;
    }

    /**
     * Splits the String into sections based on parentheses
     *
     * @param unsplit The String to split
     * @return A list of sections
     */
    public List<String> splitParenthesesSections(String unsplit) {
        List<String> split = new ArrayList<>();
        //iterate until we hit the end of the string
        for (int looper = 0; looper < unsplit.length(); looper++) {
            char test = unsplit.charAt(looper);
            if (getOpeners().contains(test)) {
                int closingIndex = getIndexOfCloser(looper, unsplit);
                if (closingIndex != unsplit.length()) {
                    split.add(unsplit.substring(0, closingIndex + 1));
                    unsplit = unsplit.substring(closingIndex + 1);
                    //reset the count
                    looper = -1;
                } else {
                    //we have a closer at the end
                    split.add(unsplit.substring(0, closingIndex));
                    unsplit = "";
                }
            }
        }
        //if we have no parentheses, there is only one section
        if (!unsplit.isEmpty()) {
            split.add(unsplit);
        }
        return split;
    }

    /**
     * Check if the String could represents a function with or without something before it like 3f(x) or f(x)
     *
     * @param functArea The String to check
     * @return True if the string represents a function and false otherwise
     */
    public boolean hasFunction(String functArea) {
        for (String potentialName : functions) {
            if (functArea.endsWith(potentialName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses an input into an Evaluable
     *
     * @param input The input to be parsed
     * @return The Evaluable the input represents
     * @throws ParsingException If the input cannot be parsed
     */
    public Evaluable<ValueType> parse(String input) throws ParsingException {
        ProtoFunction cleanedInput = cleanInput(input);
        return parseCleanedInput(cleanedInput);
    }

    /**
     * Cleans the input into a format in which it can be parsed
     *
     * @param rawInput The input to be cleaned
     * @return a ProtoFunction of the input
     * @throws ParsingException If the input cannot be parsed
     */
    private ProtoFunction cleanInput(String rawInput) throws ParsingException {
        try {
            rawInput = openingParentheses + rawInput + closedParentheses;
            for (Map.Entry<String, String> entry : synonyms.entrySet()) {
                rawInput = rawInput.replaceAll(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                rawInput = rawInput.replaceAll(entry.getKey(), entry.getValue());
            }
            for (Operator op : operators) {
                rawInput = op.evaluate(rawInput);
            }
            return new ProtoFunction(rawInput, this);
        } catch (Exception e) {
            throw new ParsingException();
        }
    }

    /**
     * Parses a ProtoFunction into an evaluable
     *
     * @param problem The ProtoFunction the input represents
     * @return An evaluable created by parsing the ProtoFunction
     * @throws ParsingException If the input cannot be parsed
     */
    private Evaluable<ValueType> parseCleanedInput(ProtoFunction problem) throws ParsingException {
        if (problem.isSimple()) {
            return new EvaluableNum<>(solver.toValue(problem.getData()));
        }
        String functName;
        List<Evaluable<ValueType>> evaluableParameters = new ArrayList<>();
        String potFunctName = problem.getData();
        String functionPart = "";
        //get the longest function this could represent
        for (String potentialName : functions) {
            if (potFunctName.endsWith(potentialName)) {
                functionPart = potentialName;
            }
        }
        //get the part of the potential function that isn't in the function
        String rest = potFunctName.substring(0, potFunctName.lastIndexOf(functionPart));
        if (functionPart.isEmpty()) {
            rest = potFunctName;
        }
        if (!rest.isEmpty()) {
            //implicit multiplication is going on as in 3f(X)
            functName = implicitMultiplicationFunctionName();
            evaluableParameters.add(new EvaluableNum<>(solver.toValue(rest)));
            problem.updateData(functionPart);
            evaluableParameters.add(parseCleanedInput(problem));
        } else {
            //this is just a function that can be converted as is
            functName = functionPart;
            for (ProtoFunction param : problem.getParams()) {
                evaluableParameters.add(parseCleanedInput(param));
            }
        }
        return new EvaluableFunction<>(functName, evaluableParameters);
    }

    /**
     * Checks what variables must be defined in order to solve a mathematical expression
     *
     * @param problem The expression to check
     * @return A List of variables to define
     */
    public List<String> getUndefinedVariables(String problem) throws ParsingException {
        ProtoFunction cleanedInput = cleanInput(problem);
        return getUndefinedVariables(cleanedInput);
    }

    /**
     * Checks what variables must be defined in order to parse a ProtoFunction
     *
     * @param problem The expression to check
     * @return A List of variables to define
     */
    private List<String> getUndefinedVariables(ProtoFunction problem) throws ParsingException {
        if (problem.isSimple()) {
            List<String> out = new ArrayList();
            try {
                new EvaluableNum<>(solver.toValue(problem.getData()));
            } catch (Exception e) {
                out.add(problem.getData());
            }
            return out;
        }
        List<String> variablesToAdd = new ArrayList<>();
        String potFunctName = problem.getData();
        String functionPart = "";
        //get the longest function this could represent
        for (String potentialName : functions) {
            if (potFunctName.endsWith(potentialName)) {
                functionPart = potentialName;
            }
        }
        //get the part of the potential function that isn't in the function
        String rest = potFunctName.substring(0, potFunctName.lastIndexOf(functionPart));
        if (functionPart.isEmpty()) {
            rest = potFunctName;
        }
        if (!rest.isEmpty()) {
            try {
                new EvaluableNum<>(solver.toValue(rest));
            } catch (ParsingException e) {
                variablesToAdd.add(rest);
            }
            problem.updateData(functionPart);
            variablesToAdd.addAll(getUndefinedVariables(problem));
        } else {
            for (ProtoFunction param : problem.getParams()) {
                variablesToAdd.addAll(getUndefinedVariables(param));
            }
        }
        return variablesToAdd;
    }
}