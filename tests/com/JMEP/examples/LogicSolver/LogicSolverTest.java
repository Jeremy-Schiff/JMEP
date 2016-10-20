package com.JMEP.examples.LogicSolver;

import com.JMEP.solver.EvaluationException;
import com.JMEP.solver.Function;
import com.JMEP.solver.OperatorType;
import com.JMEP.solver.ParsingException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * A series of tests for the LogicSolver.
 */
public class LogicSolverTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getNumberWrappers() throws Exception {
        LogicSolver solver = new LogicSolver();
        Map<Character, Character> expectedMap = new HashMap();
        assertEquals(solver.getNumberWrappers(), expectedMap);
    }

    @Test
    public void toValue() throws Exception {
        LogicSolver solver = new LogicSolver();
        assertEquals(solver.toValue("false"), false);
        assertEquals(solver.toValue("true"), true);
        exception.expect(ParsingException.class);
        solver.toValue("a");
    }

    @Test
    public void addSynonym() throws Exception {
        LogicSolver solver = new LogicSolver();
        solver.addSynonym(" if and only if ", "⇔");
        assertEquals(solver.solve("false if and only if true"), false);

    }

    @Test
    public void addVariable() throws Exception {
        LogicSolver solver = new LogicSolver();
        solver.addVariable("P", "false");
        assertEquals(solver.solve("P⇒true"), true);
    }

    @Test
    public void clearVariables() throws Exception {
        LogicSolver solver = new LogicSolver();
        solver.addVariable("P", "false");
        assertEquals(solver.solve("P⇒true"), true);
        solver.clearVariables();
        Set<String> expected = new HashSet<>();
        expected.add("P");
        assertEquals(solver.getUndefinedVariables("P⇒true"), expected);
    }

    @Test
    public void addOperator() throws Exception {
        LogicSolver solver = new LogicSolver();
        solver.addFunction(new Function<Boolean>("exclusiveOR") {
            @Override
            public Boolean evaluate(List<Boolean> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return (parameters.get(0) && !parameters.get(1)) || (!parameters.get(0) && parameters.get(1));
            }
        });
        solver.addOperator('★', "exclusiveOR", OperatorType.ParametersBeforeAndAfterOperator);
        //test the operator
        assertEquals(solver.solve("true★true"), false);
    }

    @Test
    public void addFunction() throws Exception {
        LogicSolver solver = new LogicSolver();
        solver.addFunction(new Function<Boolean>("exclusiveOR") {
            @Override
            public Boolean evaluate(List<Boolean> parameters) throws EvaluationException {
                if (parameters.size() != 2) {
                    throw new EvaluationException();
                }
                return (parameters.get(0) && !parameters.get(1)) || (!parameters.get(0) && parameters.get(1));
            }
        });
        //test the funciton
        assertEquals(solver.solve("exclusiveOR(true,true)"), false);
    }

    @Test
    public void solve() throws Exception {
        LogicSolver solver = new LogicSolver();
        //test perfectly formed single function
        assertEquals(solver.solve("and(true,true)"), true);
        //test unclosed parentheses at end
        assertEquals(solver.solve("and(true,true"), true);
        //test parentheses around the function
        assertEquals(solver.solve("(and(true,true))"), true);
        //test parentheses around the input
        assertEquals(solver.solve("and((true),(true))"), true);
        //test both sets of parentheses
        assertEquals(solver.solve("(and((true),(true)))"), true);
        //test multiple unclosed ending parentheses
        assertEquals(solver.solve("and((true),(true"), true);
        //test well formed functions inside functions
        assertEquals(solver.solve("or(false,and(true,true))"), true);
        //test singular missing ending parentheses
        assertEquals(solver.solve("or(false,and(true,true)"), true);
        //test double missing parentheses
        assertEquals(solver.solve("or(false,and(true,true"), true);
        //test operators that take parameters before and after, with various parentheses
        assertEquals(solver.solve("true^false"), false);
        assertEquals(solver.solve("(true)^false"), false);
        assertEquals(solver.solve("(true)^(false)"), false);
        assertEquals(solver.solve("(true)^(false"), false);
        //test operators that take parameters before and after as parameters
        assertEquals(solver.solve("or((true)^(false),false"), false);
        //test order of operations
        assertEquals(solver.solve("true^false∨true"), true);
        assertEquals(solver.solve("true∨true^false"), true);
        //test negation
        assertEquals(solver.solve("~true"), false);
        assertEquals(solver.solve("~~true"), true);
        //ensure the other functions work as expected
        assertEquals(solver.solve("true⇒false"), false);
        assertEquals(solver.solve("false⇔true"), false);
    }

    @Test
    public void getUndefinedVariables() throws Exception {
        LogicSolver solver = new LogicSolver();
        Set<String> expected = new HashSet<>();
        expected.add("E");
        expected.add("A");
        assertEquals(solver.getUndefinedVariables("or(A,E)"), expected);
        solver.addVariable("E", "true");
        expected.remove("E");
        assertEquals(solver.getUndefinedVariables("or(A,E)"), expected);
        assertEquals(solver.getUndefinedVariables("and(or(A,E),A)"), expected);
        expected.remove("A");
        assertEquals(solver.getUndefinedVariables("not(E)"), expected);
    }

    @Test
    public void isSatisifiable() throws Exception {
        LogicSolver solver = new LogicSolver();
        assertEquals(solver.isSatisifiable("~p"), true);
        assertEquals(solver.isSatisifiable("~p^p"), false);
        //test multiple variables
        assertEquals(solver.isSatisifiable("~p^p^q"), false);
    }

}