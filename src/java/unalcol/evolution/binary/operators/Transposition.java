package unalcol.evolution.binary.operators;

import unalcol.evolution.*;
import unalcol.evolution.operators.ArityOne;
import unalcol.random.*;
import unalcol.structures.bitarray.BitArray;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: Transposition</p>
 * <p>Description: The simple transposition operator (without flanking)</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class Transposition extends ArityOne<BitArray> {

    /**
     * Constructor: Default constructor
     */
    public Transposition(Environment _environment) {
        super(_environment);
    }


    /**
     * Interchange the bits between two positions randomly chosen
     * Example:      genome = 100011001110
     * Transposition 2-10:    101100110010
     *
     * @param _genome Genome to be modified
     */
    public Vector<BitArray> apply(BitArray _genome) {
        BitArray genome = (BitArray) Cloner.clone(_genome);

        IntegerGenerator gen = new IntegerGenerator(genome.size());
        int start = gen.next();
        int end = gen.next();

        if (start > end) {
            int t = start;
            start = end;
            end = t;
        }
        boolean tr;

        while (start < end) {
            tr = genome.get(start);
            genome.set(start, genome.get(end));
            genome.set(end, tr);
            start++;
            end--;
        }
        Vector<BitArray> v = new Vector<BitArray>();
        v.add(genome);
        return v;
    }
}
