package unalcol.evolution.binary.fitness;

import unalcol.structures.bitarray.BitArray;

/**
 * <p>Title: M8_Deceptive</p>
 * <p>Description: Extended deceptive binary functions</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class M8_Deceptive extends M7_Deceptive {

    /**
     * Evaluate the max ones fitness function over the binary array given
     *
     * @param x Binary Array to be evaluated
     * @return the fitness function over the binary array
     */
    public double evaluate(BitArray x) {
        double f = super.evaluate(x);
        f = 5.0 * Math.pow(f / 5.0, 15.0);
        return f;
    }
}
