package unalcol.evolution.util;

import unalcol.evolution.Phenotype;
import unalcol.structures.bitarray.*;
import unalcol.object.transformation.Normalization;
import unalcol.math.realvector.RandomVector;

/**
 * <p>Title: BinaryToRealVectorPhenotype</p>
 * <p>Description: Phenotype with binary genome and real thing</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company:Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class BinaryToRealVectorPhenotype extends Phenotype<double[], BitArray> {
    double max;
    /**
     * Determines the space of the fitness functions. Since the individual is storing
     * values in the [0,1)^n space, This attribute allows to scale the individual to
     * the appropiated range of the fitness function
     */
    protected Normalization space = null;

    protected BinaryToIntArrayPhenotype lowLevel = null;

    /**
     * Constructor: Creates an individual with a random genome
     */
    public BinaryToRealVectorPhenotype(int _int_size, boolean _grayCode,
                                       double[] _min, double[] _max) {
        max = (1 << _int_size);
        lowLevel = new BinaryToIntArrayPhenotype(_int_size, _grayCode);
        double[] minOriginal = RandomVector.create(_min.length, 0.0);
        double[] maxOriginal = RandomVector.create(_max.length, max);
        space = new Normalization(minOriginal, maxOriginal, _min, _max);
    }

    /**
     * Generates a thing from the given genome
     *
     * @param genome Genome of the thing to be expressed
     * @return A thing expressed from the genome
     */
    public double[] get(BitArray genome) {
        int[] y = lowLevel.get(genome);
        int n = y.length;
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = y[i];
        }
        if (space != null) {
            space.fastApply(x);
        }
        return x;
    }

    /**
     * Generates a genome from the given thing
     *
     * @param thing A thing expressed from the genome
     * @return Genome of the thing
     */
    public BitArray set(double[] rv) {
        int n = rv.length;
        double[] x = (double[]) rv.clone();
        int[] y = new int[n];
        if (space != null) {
            x = space.inverse(x);
        }
        for (int i = 0; i < n; i++) {
            y[i] = (int) (x[i]);
        }
        return lowLevel.set(y);
    }

    public static void main(String[] args) {
        BinaryToRealVectorPhenotype p = new BinaryToRealVectorPhenotype(
                10, false, new double[]{-512.0},
                new double[]{512.0});
        for (int i = 0; i < 10; i++) {
            BitArray g = new BitArray(10, true);
            System.out.println(g.toString());
            System.out.println(p.get(g)[0]);
        }

        System.out.println("***" + p.set(new double[]{0.0}));
        System.out.println("***" + p.set(new double[]{1.0}));
    }
}
