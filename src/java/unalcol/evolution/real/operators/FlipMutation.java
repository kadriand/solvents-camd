package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.operators.ArityOne;
import unalcol.evolution.real.RealVectorLimits;
import unalcol.random.*;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: FlipMutation</p>
 * <p>Description: Changes one component of the encoded double[] with the complement
 * in the interval defined for the component (x = min + max - x)</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class FlipMutation extends ArityOne<double[]> {
    /**
     * Limits of the real vector
     */
    RealVectorLimits limits;

    /**
     * Default constructor
     *
     * @param environment The environment
     */
    public FlipMutation(Environment environment, RealVectorLimits limits) {
        super(environment);
        this.limits = limits;
    }

    /**
     * Modifies one component of the encoded double[] with the complement
     * in the interval defined for the component (x=min+max-x)
     *
     * @param gen Genome to be modified
     * @return Index of the real modified
     */
    public Vector<double[]> apply(double[] gen) {
        double[] genome = (double[]) Cloner.clone(gen);
        int pos = -1;
        try {
            IntegerGenerator s = new IntegerGenerator(genome.length);
            pos = s.next();
            double x = genome[pos];
            double min = limits.min[pos];
            double max = limits.max[pos];
            genome[pos] = min + max - x;
        } catch (Exception e) {
            System.err.println("[Flip Mutation]" + e.getMessage());
        }
        Vector<double[]> v = new Vector<double[]>();
        v.add(genome);
        return v;
    }

}
