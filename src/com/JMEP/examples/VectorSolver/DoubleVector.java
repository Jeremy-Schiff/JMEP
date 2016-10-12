package com.JMEP.examples.VectorSolver;

import java.util.Arrays;

/**
 * A quick implementation of a vector to test the Solver. This implementation is fairly sparse, and should not be used
 * as-is for anything.
 */
public class DoubleVector {

    private double[] values;

    public DoubleVector(double[] doubles) {
        values = doubles;
    }

    public static DoubleVector valueOf(String value) {
        if (value.equals("")) {
            return new DoubleVector(new double[]{});
        }
        value = value.substring(1, value.length() - 1);
        String[] stringVals = value.split(",");
        double[] vals = new double[stringVals.length];
        for (int looper = 0; looper < vals.length; looper++) {
            vals[looper] = Double.valueOf(stringVals[looper]);
        }
        return new DoubleVector(vals);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof DoubleVector && Arrays.equals(values, ((DoubleVector) other).values);
    }

    public DoubleVector multiply(DoubleVector other) {
        double sum = 0;
        for (int looper = 0; looper < values.length; looper++) {
            sum += values[looper] * other.values[looper];
        }
        return new DoubleVector(new double[]{sum});
    }

    public DoubleVector add(DoubleVector other) {
        double[] outVals = new double[values.length];
        for (int looper = 0; looper < values.length; looper++) {
            outVals[looper] = values[looper] + other.values[looper];
        }
        return new DoubleVector(outVals);
    }

    public DoubleVector subtract(DoubleVector other) {
        if (values.length == 0) {
            values = new double[other.values.length];
        }

        double[] outVals = new double[values.length];

        for (int looper = 0; looper < values.length; looper++) {
            outVals[looper] = values[looper] - other.values[looper];
        }
        return new DoubleVector(outVals);
    }

    public DoubleVector scale(double factor) {
        double[] outVals = new double[values.length];

        for (int looper = 0; looper < values.length; looper++) {
            outVals[looper] = values[looper] * factor;
        }
        return new DoubleVector(outVals);
    }

    public double magnitude() {
        int sum = 0;
        for (double d : values) {
            sum += d * d;
        }
        return Math.sqrt(sum);
    }

    public String toString() {
        if (values.length == 0) {

            return "<>";
        }
        String out = "<";
        for (int looper = 0; looper < values.length - 1; looper++) {
            out += values[looper] + ",";
        }
        out += values[values.length - 1];
        out += ">";
        return out;
    }
}
