package unalcol.evolution.string;

import unalcol.evolution.*;
import unalcol.random.*;


/**
 * <p>Title: </p>
 * <p>
 * <p>Description: </p>
 * <p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class StringGenotype extends Genotype<String> {
    /**
     * Maximum number of genes
     */
    protected int min;
    /**
     * Generator of new genes
     */
    protected IntegerGenerator extra;

    public StringGenotype(int _min, int max) {
        min = _min;
        extra = new IntegerGenerator(max - min);
    }

    /**
     * Creates a new genome of the given genotype
     *
     * @return Object The new genome
     */
    public String newInstance() {
        int n = min + extra.next();
        return new String(new char[n]);
    }

    /**
     * Returns the number of genes in the individual's genome
     *
     * @return Number of genes in the individual's genome
     */
    public int size(String genome) {
        return genome.length();
    }
}
