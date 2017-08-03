package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.operators.ArityOne;
import unalcol.evolution.real.RealVectorLimits;
import unalcol.random.*;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: UniformMutation</p>
 * <p>Description: A uniform mutation of a single component. The new value is generated
 * in the interval defined for the component being modified</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class UniformMutation extends ArityOne<double[]> {
    /**
     * Number generator
     */
    protected StandardUniformGenerator g = null;
    /**
     * Limits of the real vector
     */
    protected RealVectorLimits limits;

    /**
     * Default constructor
     *
     * @param _limits      Limits of the real vector
     * @param _environment The environment
     */
    public UniformMutation(Environment _environment, RealVectorLimits _limits) {
        super(_environment);
        limits = _limits;
        g = Generator.random;
    }

    /**
     * Modifies the number in a random position for a guassian value with mean
     * thevalue encoded in the genome and sigma given as attribute
     *
     * @param gen Genome to be modified
     * @return Index of the real modified
     */
    public Vector<double[]> apply(double[] gen) {
        double[] genome = (double[]) Cloner.clone(gen);
        double[] min = limits.min;
        double[] max = limits.max;
        int pos = -1;
        try {
            IntegerGenerator s = new IntegerGenerator(genome.length);
            pos = s.next();
            genome[pos] = min[pos] + g.next() * (max[pos] - min[pos]);
        } catch (Exception e) {
            System.err.println("[Guassian Mutation]" + e.getMessage());
        }
        Vector<double[]> v = new Vector<double[]>();
        v.add(genome);
        return v;
    }

}
