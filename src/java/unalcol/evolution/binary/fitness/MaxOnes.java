package unalcol.evolution.binary.fitness;

import unalcol.evolution.Fitness;
import unalcol.structures.bitarray.BitArray;

/**
 * <p>Title: MaxOnes</p>
 * <p>Description: The fitness of a binary array is the number of ones in the array</p>
 * <p>Copyright:    Copyright (c) 2006/p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class MaxOnes extends Fitness<BitArray> {

    /**
     * Evaluate the max ones fitness function over the binary array given
     *
     * @param x Binary Array to be evaluated
     * @return the fitness function over the binary array
     */
    public double evaluate(BitArray x) {
        double f = 0.0;
        for (int i = 0; i < x.size(); i++) {
            if (x.get(i)) {
                f++;
            }
        }
        return f;
    }

}
