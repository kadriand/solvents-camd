package unalcol.evolution.real.fitness;


/**
 * <p>Title: M4 </p>
 * <p>Description: M4 Function as defined by De Jong</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class M4 extends M3 {
    /**
     * Evaluates the fitness function in the given real value
     *
     * @param x value used for evaluating the fitness function
     * @return The fitness function value for the given value
     */

    public double evaluate(double x) {
        double v = super.evaluate(x);
        double y = (x - 0.08) / 0.854;
        return Math.exp(-2.0 * 0.69314718 * y * y) * v;
    }

}
