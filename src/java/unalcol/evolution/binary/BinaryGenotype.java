package unalcol.evolution.binary;

import unalcol.evolution.Genotype;
import unalcol.structures.bitarray.BitArray;

/**
 * <p>Title:  BinaryGenome</p>
 * <p>Description: Interface for getting the bitarray from an Individual</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class BinaryGenotype extends Genotype<BitArray> {
    /**
     * Lenght of the new bitarray
     */
    protected int length;

    /**
     * Creates a BinaryGenotype with the given lenght
     *
     * @param length The lengh of the new bitarray
     */
    public BinaryGenotype(int length) {
        this.length = length;
    }

    /**
     * Creates a new genome of the given genotype
     *
     * @return Object The new genome
     */
    public BitArray newInstance() {
        return new BitArray(length, true);
    }

    /**
     * Returns the number of genes in the individual's genome
     *
     * @return Number of genes in the individual's genome
     */
    public int size(BitArray genome) {
        return genome.size();
    }
}
