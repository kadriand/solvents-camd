package unalcol.evolution.real.fitness;


/**
 * <p>Title: Shubert_1</p>
 * <p>Description: The Shubert 1-dimensional function</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class Shubert_1 extends RealFitness {
    /**
     * Evaluates the Shubert function 1-dimensional
     *
     * @param x Argument for the Shubert function
     * @return The Shuberrt function evaluated on the given argument
     */
    public double evaluate(double x) {
        double f = 0.0;
        for (double i = 1; i <= 5; i++) {
            f += i * Math.cos((i + 1) * x + i);
        }
        return f;
    }
}
