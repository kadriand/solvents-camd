package unalcol.evolution.binary.fitness;

import unalcol.evolution.Fitness;
import unalcol.structures.bitarray.BitArray;

/**
 * <p>Title: RoyalRoad</p>
 * <p>Description: The fitness of a binary array is the royal road function
 * as proposed by Mickalewicks</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class RoyalRoad extends Fitness<BitArray> {
    /**
     * The royal road path length
     */
    protected int pathLength = 8;

    /**
     * Constructor: Create a royal road fitness function with the path given
     *
     * @param _pathLength The royal road path length
     */
    public RoyalRoad(int _pathLength) {
        pathLength = _pathLength;
    }

    /**
     * Evaluate the max ones fitness function over the binary array given
     *
     * @param x Binary Array to be evaluated
     * @return the fitness function over the binary array
     */
    public double evaluate(BitArray x) {
        double f = 0.0;
        int n = x.size() / pathLength;
        for (int i = 0; i < n; i++) {
            int start = pathLength * i;
            int end = start + pathLength;
            while (start < end && x.get(start)) {
                start++;
            }
            if (start == end) {
                f += pathLength;
            }
        }
        return f;
    }
}
