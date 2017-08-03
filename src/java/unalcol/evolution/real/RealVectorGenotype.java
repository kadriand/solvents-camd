package unalcol.evolution.real;

import unalcol.evolution.Genotype;
import unalcol.math.realvector.RandomVector;

/**
 * <p>Title:  BinaryGenome</p>
 * <p>Description: Interface for getting the real vector from an Individual</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class RealVectorGenotype extends Genotype<double[]> {
    /**
     * Generates a random vector of doubles
     */
    protected RandomVector rand = new RandomVector();
    /**
     * Limits of the real vector
     */
    protected RealVectorLimits limits;

    /**
     * Gets the limits of the real vector
     *
     * @return The limits of the real vector
     */
    public RealVectorLimits getLimits() {
        return limits;
    }

    /**
     * Creates a RealVectorGenotype With the given RealVectorLimits
     *
     * @param limits Limits of the real vector
     */
    public RealVectorGenotype(RealVectorLimits limits) {
        this.limits = limits;
    }

    /**
     * Creates a new genome of the given genotype
     *
     * @return Object The new genome
     */
    public double[] newInstance() {
        return rand.generate(limits.min, limits.max);
    }

    /**
     * Returns the number of genes in the individual's genome
     *
     * @return Number of genes in the individual's genome
     */
    public int size(double[] genome) {
        return genome.length;
    }

}
