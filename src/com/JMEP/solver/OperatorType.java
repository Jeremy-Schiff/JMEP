package com.JMEP.solver;

/**
 * The type of operator based on the parameters is uses
 */
public enum OperatorType {
    //use only the parameter before the operator ie: !
    ParameterBeforeOperator,
    //use only the parameter after the operator ie: d/dt
    ParameterAfterOperator,
    //use both the parameters ie: +
    ParametersBeforeAndAfterOperator
}

