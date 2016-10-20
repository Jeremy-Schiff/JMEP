# JMEP (Java Mathematical Expression Parser)

JMEP is a easy to use, highly configurable expression parser for java. It supports:
  - Parsing any custom value type that can be converted to and from Strings
  - Custom functions with an arbitrary number of arguments
  - Custom operators that can take parameters before and/or after them
  - Adding and evaluating variables
  - Adding and using synonyms for better natural-language processing

It also provides ready to go examples including parsers for:
  - Numerical double values
  - Boolean values
  - Arrays of double values

The incredibly customizability provided by JMEP allows it to be used in cases ranging from vector math to SAT checking to string operations.

### General Use

One can either override the Solver class to create their own expression parser, or use one of the prebuilt examples. Both are incredibly easy.

Overriding:
```java
    public YourSolver(){
        //add operators, functions, etc
    }

    public YourType toValue(String val)
    {
        return YourType.valueOf(val);
    }

    @Override
    public Map<Character, Character> getNumberWrappers() {
        return YourType.getWrappers();
    }
```

Then:
```java
    Solver solver = new YourSolver();
    solver.solve("2+2");
```

### Key Abilities

Adding a new function:
```java
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
    solver.solve("star(2)");
```

Adding an operator:
```java
    solver.addOperator('★', "star", OperatorType.ParameterAfterOperator);
    solver.solve("★2");
```

Adding a variable:
```java
    DoubleSolver solver = new DoubleSolver();
    solver.addVariable("E", Math.E + "");
    solver.solve("ln(E)");
```

Adding a synonym:
```java
    DoubleSolver solver = new DoubleSolver();
    solver.addSynonym(" plus ", "+");
    solver.solve("3 plus 4");
```

### License
Apache 2.0