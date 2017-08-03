package unalcol.evolution.statistics;

import unalcol.abs.ScalarProduct;
import unalcol.core.Cloner;

/**
 * <p>Title: ScalarProduct</p>
 * <p>Description: Abstract class, multiplies and divide one
 * object for one scalar.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class PopStatScalarProduct extends ScalarProduct<PopulationStatistics> {
    /**
     * Multiplies object one and the scalar x
     *
     * @param one The object
     * @param x   The scalar
     */
    public PopulationStatistics fastMultiply(PopulationStatistics one, double x) {
        one.best *= x;
        one.avg *= x;
        one.worst *= x;
        one.avg_length *= x;
        one.best_length *= x;
        one.feasible_chroms *= x;
        one.pop_size *= x;
        return one;
    }

    /**
     * Divide object one by the scalar x
     *
     * @param one The object
     * @param x   The scalar
     */
    public PopulationStatistics fastDivide(PopulationStatistics one, double x) {
        one.best /= x;
        one.avg /= x;
        one.worst /= x;
        one.avg_length /= x;
        one.best_length /= x;
        one.feasible_chroms /= x;
        one.pop_size /= x;
        return one;
    }
}
