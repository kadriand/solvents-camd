package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.Selection;
import unalcol.evolution.operators.ArityTwo;
import unalcol.random.*;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: OneDimensionSimpleXOver</p>
 * <p>Description:Exchanges one component of the first individual with
 * the same component of the second individual</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class OneDimensionSimpleXOver extends ArityTwo<double[]> {
    /**
     * Default constructor
     *
     * @param _environment The environment
     */
    public OneDimensionSimpleXOver(Environment _environment) {
        super(_environment);
    }


    /**
     * Creates a crossover  operation with the given selection strategy for
     * choosing the parents used by the crossover
     *
     * @param _selection   Selection mechanism for parent selection
     * @param _environment The environment
     */
    public OneDimensionSimpleXOver(Environment _environment, Selection _selection) {
        super(_environment, _selection);
    }


    /**
     * Apply the 2-ary genetic operator over the individual genomes
     *
     * @param c1 First Individuals genome to be modified by the genetic operator
     * @param c2 Second Individuals genome to be modified by the genetic operator
     * @return extra information of the genetic operator
     */
    public Vector<double[]> apply(double[] c1, double[] c2) {
        double[] x = (double[]) Cloner.clone(c1);
        double[] y = (double[]) Cloner.clone(c2);
        int min = Math.min(x.length, y.length);
        IntegerGenerator g = new IntegerGenerator(min);
        int pos = g.next();
        double t = x[pos];
        x[pos] = y[pos];
        y[pos] = t;
        Vector<double[]> v = new Vector<double[]>();
        v.add(x);
        v.add(y);
        return v;
    }
}
