package unalcol.evolution.binary.operators;

import unalcol.evolution.*;
import unalcol.evolution.binary.BinaryGenotype;
import unalcol.evolution.operators.ArityTwo;
import unalcol.random.*;
import unalcol.structures.bitarray.BitArray;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: XOver</p>
 * <p>Description: The simple point crossover operator (variable length)</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class XOver extends ArityTwo {

    /**
     * The crossover point of the last xover execution
     */
    protected int cross_over_point;

    /**
     * Default constructor.
     *
     * @param _environment The environment
     */
    public XOver(Environment _environment) {
        super(_environment);
    }

    /**
     * Constructor: Create a crossover operator with the given selection parent strategy
     *
     * @param _environment The environment
     * @param _selection   Selection mechanism for choosing the parents
     */
    public XOver(Environment _environment, Selection _selection) {
        super(_environment, _selection);
    }


    /**
     * Apply the simple point crossover operation over the given genomes at the given
     * cross point
     *
     * @param child1     The first parent
     * @param child2     The second parent
     * @param xoverPoint crossover point
     * @return The crossover point
     */
    public Vector apply(BitArray child1, BitArray child2, int xoverPoint) {
        BitArray child1_1 = (BitArray) Cloner.clone(child1);
        BitArray child2_1 = (BitArray) Cloner.clone(child2);
        BitArray child1_2 = (BitArray) Cloner.clone(child1);
        BitArray child2_2 = (BitArray) Cloner.clone(child2);

        cross_over_point = xoverPoint;

        child1_2.leftSetToZero(cross_over_point);
        child2_2.leftSetToZero(cross_over_point);
        child1_1.rightSetToZero(cross_over_point);
        child2_1.rightSetToZero(cross_over_point);
        child1_2.or(child2_1);
        child2_2.or(child1_1);
        Vector v = new Vector();
        v.add(child1_2);
        v.add(child2_2);
        return v;
    }

    /**
     * Apply the simple point crossover operation over the given genomes
     *
     * @param c1 The first parent
     * @param c2 The second parent
     * @return The crossover point
     */
    public Vector apply(Object c1, Object c2) {
        BitArray child1 = (BitArray) c1;
        BitArray child2 = (BitArray) c2;

        IntegerGenerator g = new IntegerGenerator(Math.min(child1.size(), child2.size()));
        return apply(child1, child2, g.next());
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

        XOver xover = new XOver(new Environment(new BinaryGenotype(10), null));

        System.out.println("*** Applying the croosover ***");
        Object pos = xover.apply(parent1, parent2);
        System.out.println("Position " + pos);

        System.out.println("*** Child 1 ***");
        System.out.println(parent1.toString());
        System.out.println("*** Child 2 ***");
        System.out.println(parent2.toString());

        System.out.println("*** Applying the croosover with parent2, parent1 ***");
        pos = xover.apply(parent2, parent1);
        System.out.println("Position " + pos);

        System.out.println("*** Child 1 ***");
        System.out.println(parent1.toString());
        System.out.println("*** Child 2 ***");
        System.out.println(parent2.toString());
    }
}
