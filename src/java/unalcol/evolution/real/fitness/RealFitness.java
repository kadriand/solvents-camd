package unalcol.evolution.real.fitness;

import unalcol.evolution.Fitness;

/**
 * <p>Title: RealFitness</p>
 * <p>Description: Abstract fitness function for real values (one dimension)</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public abstract class RealFitness extends Fitness<Double> {

    /**
     * Evaluates the fitness function in the given real value
     *
     * @param x value used for evaluating the fitness function
     * @return The fitness function value for the given value
     */
    public abstract double evaluate(double x);

    /**
     * Evaluates the fitness funtion in the first component of the RealVector
     *
     * @param x RealVector used for sending the real value to be evaluated
     * @return The fitness function value for the given value (first component of the RealVector)
     */
    public double evaluate(Double x) {
        return this.evaluate(x.doubleValue());
    }
}
