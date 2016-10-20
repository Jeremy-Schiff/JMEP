package com.JMEP.examples.DoubleArraySolver;

import com.JMEP.solver.EvaluationException;
import com.JMEP.solver.Function;
import com.JMEP.solver.OperatorType;
import com.JMEP.solver.ParsingException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * A series of tests for the DoubleArraySolver.
 */
public class DoubleArraySolverTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void toValue() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        assertArrayEquals(solver.toValue(""), new Double[]{});
        assertArrayEquals(solver.toValue("[1]"), new Double[]{(double) 1});
        assertArrayEquals(solver.toValue("[1, 2]"), new Double[]{(double) 1, (double) 2});
        assertArrayEquals(solver.toValue("[1,2,1]"), new Double[]{(double) 1, (double) 2, (double) 1});
        exception.expect(ParsingException.class);
        solver.toValue("a");
    }

    @Test
    public void getNumberWrappers() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        Map<Character, Character> expectedMap = new HashMap<>();
        expectedMap.put('[', ']');
        assertEquals(solver.getNumberWrappers(), expectedMap);
    }

    @Test
    public void addSynonym() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        solver.addSynonym("what", "");
        solver.addSynonym("is", "");
        solver.addSynonym("the", "");
        solver.addSynonym("product", "multiply");
        solver.addSynonym("of", "(");
        solver.addSynonym("and", ",");
        solver.addSynonym(" ", "");
        assertArrayEquals(solver.solve("what is the product of [3,4] and [4,5]"), new Double[]{(double) 12, (double) 20});
        assertArrayEquals(solver.solve("what is the sum of [3,4] and [4,5]"), new Double[]{(double) 7, (double) 9});
    }

    @Test
    public void addVariable() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        solver.addVariable("E", "[" + Math.E + "]");
        assertArrayEquals(solver.solve("E-[" + Math.E + "]"), new Double[]{(double) 0});
    }

    @Test
    public void clearVariables() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        solver.addVariable("E", "[" + Math.E + "]");
        assertArrayEquals(solver.solve("E-[" + Math.E + "]"), new Double[]{(double) 0});
        solver.clearVariables();
        Set<String> expected = new HashSet<>();
        expected.add("E");
        assertEquals(solver.getUndefinedVariables("E-[" + Math.E + "]"), expected);
    }

    @Test
    public void addOperator() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        solver.addFunction(new Function<Double[]>("magnitude") {
            @Override
            public Double[] evaluate(List<Double[]> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                int sum = 0;
                for (double val : parameters.get(0)) {
                    sum += val * val;
                }
                return new Double[]{Math.sqrt(sum)};
            }
        });
        solver.addOperator('★', "magnitude", OperatorType.ParameterAfterOperator);
        //test the operator
        assertArrayEquals(solver.solve("★[2,2]"), new Double[]{Math.sqrt((double) 8)});
    }

    @Test
    public void addFunction() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        solver.addFunction(new Function<Double[]>("magnitude") {
            @Override
            public Double[] evaluate(List<Double[]> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                int sum = 0;
                for (double val : parameters.get(0)) {
                    sum += val * val;
                }
                return new Double[]{Math.sqrt(sum)};
            }
        });
        assertArrayEquals(solver.solve("magnitude([2,2])"), new Double[]{Math.sqrt((double) 8)});
    }

    @Test
    public void solve() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        //test perfectly formed single function
        assertArrayEquals(solver.solve("sum([3,4],[4,5])"), new Double[]{(double) 7, (double) 9});
        //test unclosed parentheses at end
        assertArrayEquals(solver.solve("sum([3,4],[4,5]"), new Double[]{(double) 7, (double) 9});
        //test three inputs into the function
        assertArrayEquals(solver.solve("sum([3,4],[4,5],[5,6]"), new Double[]{(double) 12, (double) 15});
        //test parentheses around the function
        assertArrayEquals(solver.solve("(sum([3,4],[4,5]))"), new Double[]{(double) 7, (double) 9});
        //test parentheses around the input
        assertArrayEquals(solver.solve("sum(([3,4]),([4,5]))"), new Double[]{(double) 7, (double) 9});
        //test both sets of parentheses
        assertArrayEquals(solver.solve("(sum(([3,4]),([4,5])))"), new Double[]{(double) 7, (double) 9});
        //test multiple unclosed ending parentheses
        assertArrayEquals(solver.solve("(sum(([3,4]),([4,5]"), new Double[]{(double) 7, (double) 9});
        //test well formed functions inside functions
        assertArrayEquals(solver.solve("difference([2,3],sum([3,4],[4,5]))"), new Double[]{(double) -5, (double) -6});
        //test singular missing ending parentheses
        assertArrayEquals(solver.solve("difference([2,3],sum([3,4],[4,5])"), new Double[]{(double) -5, (double) -6});
        //test double missing parentheses
        assertArrayEquals(solver.solve("difference([2,3],sum([3,4],[4,5]"), new Double[]{(double) -5, (double) -6});
        //test various implicit multiplication methods
        assertArrayEquals(solver.solve("[3,4]([4,5])"), new Double[]{(double) 12, (double) 20});
        assertArrayEquals(solver.solve("[3,4]([4,5]"), new Double[]{(double) 12, (double) 20});
        assertArrayEquals(solver.solve("([3,4])([4,5])"), new Double[]{(double) 12, (double) 20});
        assertArrayEquals(solver.solve("([3,4])([4,5]"), new Double[]{(double) 12, (double) 20});
        assertArrayEquals(solver.solve("([3,4])[4,5]"), new Double[]{(double) 12, (double) 20});
        assertArrayEquals(solver.solve("[3,4]([4,5])[6,7]"), new Double[]{(double) 72, (double) 140});
        //test implicit multiplication in front of functions
        assertArrayEquals(solver.solve("[2,3](sum(([3,4]),([4,5]"), new Double[]{(double) 14, (double) 27});
        //test operators that take parameters before and after, with various parentheses
        assertArrayEquals(solver.solve("[3,4]+[4,5]"), new Double[]{(double) 7, (double) 9});
        assertArrayEquals(solver.solve("([3,4])+[4,5]"), new Double[]{(double) 7, (double) 9});
        assertArrayEquals(solver.solve("([3,4])+([4,5])"), new Double[]{(double) 7, (double) 9});
        assertArrayEquals(solver.solve("([3,4])+([4,5]"), new Double[]{(double) 7, (double) 9});
        //test operators that take parameters before and after as parameters
        assertArrayEquals(solver.solve("multiply(([3,4])+([4,5]),[2,3]"), new Double[]{(double) 14, (double) 27});
        //test order of operations
        assertArrayEquals(solver.solve("[0,0,0]+[1,1,1]*[1,1,1]"), new Double[]{(double) 1, (double) 1, (double) 1});
        assertArrayEquals(solver.solve("[1,1,1]*[1,1,1]+[0,0,0]"), new Double[]{(double) 1, (double) 1, (double) 1});
        //test negation
        assertArrayEquals(solver.solve("-[1,1,1]"), new Double[]{(double) -1, (double) -1, (double) -1});
        assertArrayEquals(solver.solve("--[1,1,1]"), new Double[]{(double) 1, (double) 1, (double) 1});
    }

    @Test
    public void getUndefinedVariables() throws Exception {
        DoubleArraySolver solver = new DoubleArraySolver();
        Set<String> expected = new HashSet<>();
        expected.add("E");
        expected.add("A");
        assertEquals(solver.getUndefinedVariables("A+E"), expected);
        solver.addVariable("E", "[" + Math.E + "]");
        expected.remove("E");
        assertEquals(solver.getUndefinedVariables("A+E"), expected);
        assertEquals(solver.getUndefinedVariables("A+E*AE"), expected);
        expected.remove("A");
        assertArrayEquals(solver.solve("E-[" + Math.E + "]"), new Double[]{(double) 0});
    }
}