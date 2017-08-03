package unalcol.evolution.binary.operators;

import unalcol.evolution.*;
import unalcol.evolution.binary.BinaryGenotype;
import unalcol.evolution.operators.ArityOne;
import unalcol.random.*;
import unalcol.structures.bitarray.BitArray;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: SingleBitMutation</p>
 * <p>Description: Flips one bit in the chromosome. The flipped bit is randomly selected
 * with uniform probability distribution</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class SingleBitMutation extends ArityOne<BitArray> {

    /**
     * Constructor: Default constructor
     */
    public SingleBitMutation(Environment _environment) {
        super(_environment);
    }

    /**
     * Flips a bit in the given genome
     *
     * @param gen Genome to be modified
     * @return Index of the flipped bit
     */
    public Vector<BitArray> apply(BitArray gen) {
        BitArray genome = (BitArray) Cloner.clone(gen);
        int pos = -1;
        try {
            IntegerGenerator g = new IntegerGenerator(genome.size());
            pos = g.next();
            genome.not(pos);
        } catch (Exception e) {
            System.err.println("[Mutation]" + e.getMessage());
        }
        Vector<BitArray> v = new Vector<BitArray>();
        v.add(genome);
        return v;
    }

    /**
     * Testing function
     */
    public static void main(String[] argv) {
        System.out.println("*** Generating a genome of 21 genes randomly ***");
        BitArray genome = new BitArray(21, true);
        System.out.println(genome.toString());


        SingleBitMutation mutation = new SingleBitMutation(new Environment(new BinaryGenotype(21), null));

        System.out.println("*** Applying the mutation ***");
        BitArray mutated = mutation.apply(genome).get(0);
        System.out.println("Mutated array " + mutated);
    }

}
