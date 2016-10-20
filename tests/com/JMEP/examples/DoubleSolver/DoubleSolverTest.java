package com.JMEP.examples.DoubleSolver;

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
 * A series of tests for the DoubleSolver.
 */
public class DoubleSolverTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private double epsilon = 0.05;

    @Test
    public void toValue() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        assertEquals(solver.toValue(""), 0, epsilon);
        assertEquals(solver.toValue("1"), 1, epsilon);
        exception.expect(ParsingException.class);
        solver.toValue("a");
    }

    @Test
    public void getNumberWrappers() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        Map<Character, Character> expectedMap = new HashMap();
        assertEquals(solver.getNumberWrappers(), expectedMap);
    }

    @Test
    public void addSynonym() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        solver.addSynonym("what", "");
        solver.addSynonym("is", "");
        solver.addSynonym("the", "");
        solver.addSynonym("product", "multiply");
        solver.addSynonym("of", "(");
        solver.addSynonym("and", ",");
        solver.addSynonym(" ", "");
        assertEquals(solver.solve("what is the product of 3 and 4"), 12, epsilon);
        assertEquals(solver.solve("what is the sum of 3 and 4"), 7, epsilon);
    }

    @Test
    public void addVariable() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        solver.addVariable("E", Math.E + "");
        assertEquals(solver.solve("ln(E)"), 1, epsilon);
    }

    @Test
    public void clearVariables() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        solver.addVariable("E", Math.E + "");
        assertEquals(solver.solve("ln(E)"), 1, epsilon);
        solver.clearVariables();
        Set<String> expected = new HashSet<>();
        expected.add("E");
        assertEquals(solver.getUndefinedVariables("ln(E)"), expected);
    }

    @Test
    public void addOperator() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        solver.addFunction(new Function<Double>("star") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                int number = (int) (double) parameters.get(0) + 1;
                int product = 1;
                for (int i = 1; i <= number; i++) {
                    product *= i;
                }
                return (double) product;
            }
        });
        solver.addOperator('★', "star", OperatorType.ParameterAfterOperator);
        //test the operator
        assertEquals(solver.solve("★2"), 6, epsilon);
        //test different parentheses/operations around the operator
        assertEquals(solver.solve("sum(3,★2)"), 9, epsilon);
        assertEquals(solver.solve("sum(★2,3)"), 9, epsilon);
        assertEquals(solver.solve("sum(★2,3)2"), 18, epsilon);
        assertEquals(solver.solve("sum((★2),3)2"), 18, epsilon);
        assertEquals(solver.solve("★★2"), 5040, epsilon);
        assertEquals(solver.solve("2!+★2"), 8, epsilon);
        assertEquals(solver.solve("2!★2"), 12, epsilon);
        assertEquals(solver.solve("(★2)(★2)"), 36, epsilon);
        assertEquals(solver.solve("★2(★2)"), 36, epsilon);
        assertEquals(solver.solve("(★2)★2"), 36, epsilon);
    }

    @Test
    public void addFunction() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        solver.addFunction(new Function<Double>("star") {
            @Override
            public Double evaluate(List<Double> parameters) throws EvaluationException {
                if (parameters.size() != 1) {
                    throw new EvaluationException();
                }
                int number = (int) (double) parameters.get(0) + 1;
                int product = 1;
                for (int i = 1; i <= number; i++) {
                    product *= i;
                }
                return (double) product;
            }
        });
        assertEquals(solver.solve("star(2)"), 6, epsilon);
    }

    @Test
    public void solve() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        //test perfectly formed single function
        assertEquals(solver.solve("sum(3,4)"), 7, epsilon);
        //test unclosed parentheses at end
        assertEquals(solver.solve("sum(3,4"), 7, epsilon);
        //test three inputs into the function
        assertEquals(solver.solve("sum(3,4,5"), 12, epsilon);
        //test parentheses around the function
        assertEquals(solver.solve("(sum(3,4))"), 7, epsilon);
        //test parentheses around the input
        assertEquals(solver.solve("sum((3),(4))"), 7, epsilon);
        //test both sets of parentheses
        assertEquals(solver.solve("(sum((3),(4))"), 7, epsilon);
        //test multiple unclosed ending parentheses
        assertEquals(solver.solve("(sum((3),(4"), 7, epsilon);
        //test well formed functions inside functions
        assertEquals(solver.solve("difference(3,sum(4,5))"), -6, epsilon);
        //test singular missing ending parentheses
        assertEquals(solver.solve("difference(3,sum(4,5)"), -6, epsilon);
        //test double missing parentheses
        assertEquals(solver.solve("difference(3,sum(4,5"), -6, epsilon);
        //test various implicit multiplication methods
        assertEquals(solver.solve("4(5)"), 20, epsilon);
        assertEquals(solver.solve("4(5"), 20, epsilon);
        assertEquals(solver.solve("(4)(5)"), 20, epsilon);
        assertEquals(solver.solve("(4)(5"), 20, epsilon);
        assertEquals(solver.solve("(4)5"), 20, epsilon);
        assertEquals(solver.solve("4(5)6"), 120, epsilon);
        //test implicit multiplication in front of functions
        assertEquals(solver.solve("5(sum(3,difference(4,5"), 10, epsilon);
        //test operators that only take the parameter before
        assertEquals(solver.solve("3!"), 6, epsilon);
        //test operators as parameters, both first and second
        assertEquals(solver.solve("sum(3,3!)"), 9, epsilon);
        assertEquals(solver.solve("sum(3!,3)"), 9, epsilon);
        //test operators next to each other
        assertEquals(solver.solve("3!!"), 720, epsilon);
        //test operators that take parameters before and after, with various parentheses
        assertEquals(solver.solve("2+2"), 4, epsilon);
        assertEquals(solver.solve("(2)+2"), 4, epsilon);
        assertEquals(solver.solve("(2)+(2)"), 4, epsilon);
        assertEquals(solver.solve("(2)+(2"), 4, epsilon);
        //test operators that take parameters before and after as parameters
        assertEquals(solver.solve("multiply((2)+(2),3"), 12, epsilon);
        //test order of operations
        assertEquals(solver.solve("3+3*3"), 12, epsilon);
        assertEquals(solver.solve("3*3+3"), 12, epsilon);
        //test negation
        assertEquals(solver.solve("-3"), -3, epsilon);
        assertEquals(solver.solve("--3"), 3, epsilon);
        //ensure the other functions work as expected
        assertEquals(solver.solve("3/4"), 0.75, epsilon);
        assertEquals(solver.solve("3^4"), 81, epsilon);
        assertEquals(solver.solve("3^(4-1)"), 27, epsilon);
        assertEquals(solver.solve("4-3^4-4"), -81, epsilon);
        assertEquals(solver.solve("cos(0)"), 1, epsilon);
        assertEquals(solver.solve("sin(0)"), 0, epsilon);
        assertEquals(solver.solve("tan(0)"), 0, epsilon);
        assertEquals(solver.solve("log(10)"), 1, epsilon);
    }

    @Test
    public void getUndefinedVariables() throws Exception {
        DoubleSolver solver = new DoubleSolver();
        Set<String> expected = new HashSet<>();
        expected.add("E");
        expected.add("A");
        assertEquals(solver.getUndefinedVariables("Aln(E)"), expected);
        solver.addVariable("E", Math.E + "");
        expected.remove("E");
        assertEquals(solver.getUndefinedVariables("Aln(E)"), expected);
        assertEquals(solver.getUndefinedVariables("Aln(E)EA"), expected);
        expected.remove("A");
        assertEquals(solver.getUndefinedVariables("ln(E)"), expected);
    }
}