package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.Selection;
import unalcol.evolution.operators.ArityTwo;
import unalcol.random.*;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: LinearXOverPerDimension</p>
 * <p>Description:Applies a linear crossover per dimension. in this case each alpha
 * is different for each component</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class LinearXOverPerDimension extends ArityTwo<double[]> {

    /**
     * Defalut constructor
     *
     * @param _environment The environment
     */
    public LinearXOverPerDimension(Environment _environment) {
        super(_environment);
    }

    /**
     * Creates a linear crossover per dimension operation with the given selection strategy for
     * choosing the parents used by the crossover
     *
     * @param _selection   Selection mechanism for parent selection
     * @param _environment The environment
     */
    public LinearXOverPerDimension(Environment _environment, Selection _selection) {
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
        double a;
        double a_1;
        double tx, ty;
        int min = Math.min(x.length, y.length);
        for (int i = 0; i < min; i++) {
            a = Generator.random.next();
            a_1 = 1.0 - a;
            tx = x[i];
            ty = y[i];
            x[i] = a * tx + a_1 * ty;
            y[i] = a_1 * tx + a * ty;
        }
        Vector<double[]> v = new Vector<double[]>();
        v.add(x);
        v.add(y);
        return v;
    }
}
