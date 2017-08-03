package unalcol.evolution.real.fitness;

import unalcol.evolution.Fitness;

/**
 * <p>Title:  Griewangk</p>
 * <p>Description: The Griewangk function</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class Griewangk extends Fitness<double[]> {

    /**
     * Constructor: Creates a Griewangk function
     */
    public Griewangk() {
    }

    /**
     * Evaluate the fitness function over the real vector given
     *
     * @param x Real vector to be evaluated
     * @return the fitness function over the real vector
     */
    public double evaluate(double[] x) {
        int n = x.length;
        double sum = 0.0;
        double prod = 1.0;
        for (int i = 0; i < n; i++) {
            sum += x[i] * x[i] / 4000.0;
            prod *= Math.cos(x[i] / Math.sqrt(i + 1.0));
        }
        return -(1.0 + sum - prod);
    }
}
