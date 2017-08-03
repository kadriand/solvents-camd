package unalcol.evolution.real.fitness;

/**
 * <p>Title: CentralTwoPeakTrap</p>
 * <p>Description: A central two peak trap function for real values</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad@author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez) @author Jonatan Gomez
 *
 * @version 1.0
 */

public class CentralTwoPeakTrap extends TwoPeakTrap {
    /**
     * Evaluates the Central two peak trap function 1-dimensional
     *
     * @param x Argument for the Shubert function
     * @return The Shuberrt function evaluated on the given argument
     */
    public double evaluate(double x) {
        if (x < 10.0) {
            return (160.0 * x / 15.0);
        } else {
            return (super.evaluate(x));
        }
    }
}
