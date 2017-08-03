package unalcol.evolution.real.fitness;

/**
 * <p>Title: M1 </p>
 * <p>Description: M1 Function as defined by De Jong</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class M1 extends RealFitness {
    /**
     * Evaluates the fitness function in the given real value
     *
     * @param x value used for evaluating the fitness function
     * @return The fitness function value for the given value
     */
    public double evaluate(double x) {
        return Math.pow(Math.sin(5.0 * Math.PI * x), 6.0);
    }
}
