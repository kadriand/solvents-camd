package unalcol.evolution.util;

import unalcol.evolution.Phenotype;
import unalcol.structures.bitarray.*;
import unalcol.util.primitives.*;

/**
 * <p>Title:BinaryToIntArrayPhenotype</p>
 * <p>Description: Phenotype with binary genome and int thing</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company:Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class BinaryToIntArrayPhenotype extends Phenotype<int[], BitArray> {
    /**
     * If grayCode is used
     */
    protected boolean grayCode;
    /**
     * int size
     */
    protected int int_size;

    /**
     * Constructor: Creates an individual with a random genome
     */
    public BinaryToIntArrayPhenotype(int _int_size, boolean _grayCode) {
        grayCode = _grayCode;
        int_size = _int_size;
    }

    /**
     * Generates a thing from the given genome
     *
     * @param genome Genome of the thing to be expressed
     * @return A thing expressed from the genome
     */
    public int[] get(BitArray genome) {
        return BitArrayConverter.getIntArray(genome, int_size, grayCode);
    }

    /**
     * Generates a genome from the given thing
     *
     * @param thing A thing expressed from the genome
     * @return Genome of the thing
     */
    public BitArray set(int[] thing) {
        BitArray A = new BitArray(int_size * thing.length, false);
        BitArrayConverter.setIntArray(A, thing, int_size, grayCode);
        return A;
    }
}
