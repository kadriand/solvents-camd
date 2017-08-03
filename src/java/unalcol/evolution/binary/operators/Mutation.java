package unalcol.evolution.binary.operators;

import unalcol.evolution.*;
import unalcol.evolution.binary.BinaryGenotype;
import unalcol.evolution.operators.ArityOne;
import unalcol.random.*;
import unalcol.structures.bitarray.BitArray;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: Mutation</p>
 * <p>Description: The simple bit mutation operator</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class Mutation extends ArityOne<BitArray> {
    /**
     * Probability of mutating one single bit
     */
    protected double bit_mutation_rate = 0.0;

    /**
     * Default constructor. Creates a bit mutation with bit mutation rate equal to
     * 1 divided by the length of the chromosome
     */
    public Mutation(Environment _environment) {
        super(_environment);
    }

    /**
     * Constructor: Creates a mutation with the given mutation rate
     *
     * @param _bit_mutation_rate Probability of mutating each single bit
     */
    public Mutation(Environment _environment, double _bit_mutation_rate) {
        super(_environment);
        bit_mutation_rate = _bit_mutation_rate;
    }

    /**
     * Flips a bit in the given genome
     *
     * @param gen Genome to be modified
     * @return Number of mutated bits
     */
    public Vector<BitArray> apply(BitArray gen) {
        BitArray genome = (BitArray) Cloner.clone(gen);
        int count = 0;
        try {
            double rate = bit_mutation_rate;
            if (bit_mutation_rate == 0.0) {
                rate = 1.0 / genome.size();
            }
            BooleanGenerator g = new BooleanGenerator(rate);
            for (int i = 0; i < genome.size(); i++) {
                if (g.next()) {
                    genome.not(i);
                    count++;
                }
            }
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

        Mutation mutation = new Mutation(new Environment(new BinaryGenotype(21), null), 0.01);

        System.out.println("*** Applying the mutation ***");
        BitArray mutated = mutation.apply(genome).get(0);
        System.out.println("Mutated array " + mutated);

    }

}
