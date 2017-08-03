package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.Selection;
import unalcol.evolution.operators.ArityTwo;
import unalcol.random.*;
import unalcol.random.*;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: LinearXOver</p>
 * <p>Description:Applies a linear crossover to a single component</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class OneDimensionLinearXOver extends ArityTwo<double[]> {
    /**
     * Defalut constructor
     *
     * @param _environment The environment
     */
    public OneDimensionLinearXOver(Environment _environment) {
        super(_environment);
    }

    /**
     * Creates a one dimension linear crossover operation with the given selection strategy for
     * choosing the parents used by the crossover
     *
     * @param _selection   Selection mechanism for parent selection
     * @param _environment The environment
     */
    public OneDimensionLinearXOver(Environment _environment, Selection _selection) {
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

        double alpha = Generator.random.next();
        double alpha_1 = 1.0 - alpha;
        double tx, ty;
        tx = x[pos];
        ty = y[pos];
        x[pos] = alpha * tx + alpha_1 * ty;
        y[pos] = alpha_1 * tx + alpha * ty;
        Vector<double[]> v = new Vector<double[]>();
        v.add(x);
        v.add(y);
        return v;
    }
}
