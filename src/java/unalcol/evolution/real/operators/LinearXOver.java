package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.Selection;
import unalcol.evolution.operators.ArityTwo;
import unalcol.random.*;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: LinearXOver</p>
 * <p>Description:Applies a linear crossover. In this case the alpha is unique
 * for each component, it use two alpha, one for the first vector and one
 * for the second vector</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class LinearXOver extends ArityTwo<double[]> {
    /**
     * default constructor
     *
     * @param _environment The environment
     */
    public LinearXOver(Environment _environment) {
        super(_environment);
    }

    /**
     * Creates a linear crossover operation with the given selection strategy for
     * choosing the parents used by the crossover
     *
     * @param _selection   Selection mechanism for parent selection
     * @param _environment The environment
     */
    public LinearXOver(Environment _environment, Selection _selection) {
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

        double alpha = Generator.random.next();
        double alpha_1 = Generator.random.next();
        double neg_alpha = 1.0 - alpha;
        double neg_alpha_1 = 1.0 - alpha_1;
        double tx;
        double ty;
        for (int i = 0; i < min; i++) {
            tx = x[i];
            ty = y[i];
            x[i] = alpha * tx + neg_alpha * ty;
            y[i] = alpha_1 * tx + neg_alpha_1 * ty;
        }
        Vector<double[]> v = new Vector<double[]>();
        v.add(x);
        v.add(y);
        return v;
    }
}
