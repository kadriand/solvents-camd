package unalcol.evolution.real.fitness;

import unalcol.evolution.Fitness;

/**
 * <p>Title:  Rastrigin</p>
 * <p>Description: The Rastrigin function</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class Rastrigin extends Fitness<double[]> {

    /**
     * Constructor: Creates a Rastrigin function
     */
    public Rastrigin() {
    }

    /**
     * Evaluates the Rastrigin function over a real value
     *
     * @param x the real value argument of the Rastrigin function
     * @return the Rastrigin value for the given value
     */
    public double evaluate(double x) {
        return (x * x - 10.0 * Math.cos(2.0 * Math.PI * x));
    }

    /**
     * Evaluate the fitness function over the real vector given
     *
     * @param x Real vector to be evaluated
     * @return the fitness function over the real vector
     */
    public double evaluate(double[] x) {
        int n = x.length;
        double f = 0.0;
        for (int i = 0; i < n; i++) {
            f += evaluate(x[i]);
        }
        return -(10.0 * n + f);
    }
}
