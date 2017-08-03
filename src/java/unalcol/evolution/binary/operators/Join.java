package unalcol.evolution.binary.operators;

import unalcol.evolution.*;
import unalcol.evolution.binary.BinaryGenotype;
import unalcol.evolution.operators.ArityTwo;
import unalcol.structures.bitarray.BitArray;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: Join</p>
 * <p>Description: Joins two chromosomes</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class Join extends ArityTwo<BitArray> {

    /**
     * Default constructor
     *
     * @param _environment The environment
     */
    public Join(Environment _environment) {
        super(_environment);
    }

    /**
     * Constructor: Create a crossover operator with the given selection parent strategy
     *
     * @param _selection Selection mechanism for choosing the parents
     */
    public Join(Environment _environment, Selection _selection) {
        super(_environment, _selection);
    }


    /**
     * Apply the simple point crossover operation over the given genomes
     *
     * @param c1 The first parent
     * @param c2 The second parent
     */
    public Vector<BitArray> apply(BitArray c1, BitArray c2) {
        BitArray genome = (BitArray) Cloner.clone(c1);
        genome.add(c2);
        Vector<BitArray> v = new Vector<BitArray>();
        v.add(genome);
        return v;
    }


    /**
     * Testing function
     */
    public static void main(String[] argv) {
        System.out.println("*** Generating a genome of 20 genes randomly ***");
        BitArray parent1 = new BitArray(20, true);
        System.out.println(parent1.toString());

        System.out.println("*** Generating a genome of 10 genes randomly ***");
        BitArray parent2 = new BitArray(10, true);
        System.out.println(parent2.toString());

        Join xover = new Join(new Environment(new BinaryGenotype(10), null));

        System.out.println("*** Applying the croosover ***");
        BitArray child = xover.apply(parent1, parent2).get(0);
        System.out.println("New Individual " + child);

    }
}
