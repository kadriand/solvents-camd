package unalcol.evolution.algorithms.haea;

import unalcol.evolution.Operator;

import java.util.Vector;

import unalcol.evolution.*;
import unalcol.random.*;

/**
 * <p>Title: </p>
 * <p>
 * <p>Description: </p>
 * <p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class HaeaOperators {
    protected Vector rates = new Vector();

    /**
     * Normalize the given vector of reals
     *
     * @param r the vector to be normalized
     */
    protected void normalize(double[] r) {
        double total = 0.0;
        int n = r.length;
        for (int i = 0; i < n; i++) {
            total += r[i];
        }
        for (int i = 0; i < n; i++) {
            r[i] /= total;
        }
    }

    public abstract int numberOfOperatorsPerIndividual();

    public abstract int numberOfOperators();

    public void init() {
        int n = rates.size();
        rates.clear();
        setPopulationSize(n);
    }

    public void setPopulationSize(int n) {
        int m = numberOfOperatorsPerIndividual();
        int start = rates.size();
        if (n > start) {
            for (int i = start; i < n; i++) {
                double[] tempRates = new double[m];
                for (int j = 0; j < m; j++) {
                    tempRates[j] = Generator.random.next();
                }
                normalize(tempRates);
                rates.add(tempRates);
            }
        } else {
            while (n < start) {
                rates.remove(n);
                start--;
            }
        }
    }

    public Vector getRates() {
        return rates;
    }

    protected double[] getRates(int indIndex) {
        return (double[]) rates.get(indIndex);
    }

    public static boolean weightedSelection = false;

    /**
     * Select an operator to be applied according to the rates encoded in the individual
     *
     * @param x Rates to be taken into account
     * @return Index of the choosen operator
     */
    protected int selectOperator(double[] x) {
        IntegerGenerator g;
        if (weightedSelection) {
            g = new WeightedIntegerGenerator(x);
        } else {
            g = new IntegerGenerator(x.length);
        }
        return g.next();
    }

    public int selectOperatorIndex(int indIndex) {
        return selectOperator(getRates(indIndex));
    }

    public abstract Operator getOperator(int indIndex, int operIndex);

    /**
     * Increase the rate of the given operator
     *
     * @param x    Rates vector index
     * @param oper Operator index
     */
    public void increase(int indIndex, int operIndex) {
        double[] x = getRates(indIndex);
        x[operIndex] *= (1.0 + Generator.random.next());
        normalize(x);
    }

    /**
     * Decrease the rate of the given operator
     *
     * @param x    Rates vector index
     * @param oper Operator index
     */
    public void decrease(int indIndex, int operIndex) {
        double[] x = getRates(indIndex);
        x[operIndex] *= (1.0 - Generator.random.next());
        normalize(x);
    }

    public void update() {
    }
}
