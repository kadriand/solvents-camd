package unalcol.evolution.binary;

import unalcol.random.*;
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
public class VariableLengthBinaryGenotype extends BinaryGenotype {
    /**
     * Maximum number of genes
     */
    protected int max_length;
    /**
     * Delta lenght
     */
    protected int delta_length;
    /**
     * The final limit of the random number generator range
     */
    protected int max_delta;
    /**
     * Generator of new genes
     */
    protected IntegerGenerator extra_genes;

    public VariableLengthBinaryGenotype(int _min, int _max_length, int _delta_length) {
        super(_min);
        delta_length = _delta_length;
        max_delta = (_max_length - length) / delta_length;
        extra_genes = new IntegerGenerator(max_delta);
        max_length = length + max_delta * delta_length;
    }

    /**
     * Creates a new genome of the given genotype
     *
     * @return Object The new genome
     */
    public BitArray newInstance() {
        int n = extra_genes.next();
        return new BitArray(length + n * delta_length, true);
    }

    /**
     * Returns the number of genes in the individual's genome
     *
     * @return Number of genes in the individual's genome
     */
    public int size(BitArray genome) {
        return ((genome.size() - length) / delta_length);
    }

    /**
     * Returns lenght
     *
     * @return The lenght
     */
    public int getMinLength() {
        return length;
    }

    /**
     * Returns Max_Lenght
     *
     * @return The maximum number of genes
     */
    public int getMaxLength() {
        return max_length;
    }

    /**
     * Returns DeltaLength
     *
     * @return DeltaLenght
     */
    public int getDeltaLength() {
        return delta_length;
    }

    /**
     * Returns MaxDelta
     *
     * @return The final limit of the random number generator range
     */
    public int getMaxDelta() {
        return max_delta;
    }
}
