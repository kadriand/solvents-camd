package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.operators.ArityOne;
import unalcol.evolution.real.RealVectorLimits;
import unalcol.random.*;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: GaussianMutation</p>
 * <p>Description: Changes one component of the encoded double[] with a number
 * randomly generated following a Gaussian distribution with mean the old value of
 * the component and the given standard deviation</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class GaussianMutation extends ArityOne<double[]> {
    /**
     * Limits of the real vector
     */
    protected RealVectorLimits limits;
    /**
     * Gauss number generator
     */
    protected StandardGaussianGenerator g = null;
    /**
     * sigma: standard deviation
     */
    protected double[] sigma = null;

    /**
     * Creates a Gaussian Mutation with the given standard deviation for each component
     *
     * @param _limits      Limits of the real vector
     * @param _sigma       Standard deviation per component
     * @param _environment The environment
     */
    public GaussianMutation(Environment _environment, RealVectorLimits _limits,
                            double _sigma) {
        super(_environment);
        limits = _limits;
        int n = limits.min.length;
        sigma = new double[n];
        setSigma(_sigma);
        g = new StandardGaussianGenerator();
    }

    /**
     * Creates a Gaussian Mutation with the given standard deviation per component
     *
     * @param _sigma       Standard deviation per component
     * @param _limits      Limits of the real vector
     * @param _environment The environment
     */
    public GaussianMutation(Environment _environment, RealVectorLimits _limits,
                            double[] _sigma) {
        super(_environment);
        limits = _limits;
        sigma = _sigma;
        g = new StandardGaussianGenerator();
    }

    /**
     * Sets the standard deviation
     *
     * @param _sigma Standard deviation for all the components
     */
    public void setSigma(double _sigma) {
        int n = sigma.length;
        for (int i = 0; i < n; i++) {
            sigma[i] = _sigma;
        }
    }

    /**
     * Sets the standard deviation
     *
     * @param _sigma Standard deviation per component
     */
    public void setSigma(double[] _sigma) {
        sigma = _sigma;
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
            double x = genome[pos];
            double y = g.next() * sigma[pos];
            x += y;
            if (x < min[pos]) {
                x = min[pos];
            } else {
                if (x > max[pos]) {
                    x = max[pos];
                }
            }
            genome[pos] = x;
        } catch (Exception e) {
            System.err.println("[Guassian Mutation]" + e.getMessage());
        }
        Vector<double[]> v = new Vector<double[]>();
        v.add(genome);
        return v;
    }

}
