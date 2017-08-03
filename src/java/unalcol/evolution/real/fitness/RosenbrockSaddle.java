package unalcol.evolution.real.fitness;

import unalcol.evolution.Fitness;

/**
 * <p>Title:  RosenbrockSaddle</p>
 * <p>Description: The RosenbrockSaddle function</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class RosenbrockSaddle extends Fitness<double[]> {

    /**
     * Constructor: Creates a RosenbrockSaddle function
     * The limits are [-2.048,2.048] per component
     */
    public RosenbrockSaddle() {
    }

    /**
     * Evaluates the RosenbrockSaddle function over two real values
     *
     * @param x1 the first real value argument of the RosenbrockSaddle function
     * @param x2 the second real value argument of the RosenbrockSaddle function
     * @return the RosenbrockSaddle value for the given values
     */
    public double evaluate(double x1, double x2) {
        double y = x1 * x1 - x2;
        return (100.0 * y * y + (1.0 - x1) * (1.0 - x1));
    }

    /**
     * Evaluate the fitness function over the real vector given
     *
     * @param x Real vector to be evaluated
     * @return the fitness function over the real vector
     */
    public double evaluate(double[] x) {
        double f = 0.0;
        int n = x.length - 1;
        for (int i = 0; i < n; i++) {
            f += evaluate(x[i], x[i + 1]);
        }
        // minimization
        return -f;
    }
}
