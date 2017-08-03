package unalcol.evolution.statistics;

import unalcol.abs.Square;
import unalcol.core.Cloner;

/**
 * <p>Title: Square</p>
 * <p>Description: Abstract class, multiplies and divide one
 * object for one scalar.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class PopStatSquare extends Square<PopulationStatistics> {
    /**
     * Multiplies object one and the scalar x
     *
     * @param one The object
     * @param x   The scalar
     */
    public PopulationStatistics fastSqr(PopulationStatistics one) {
        one.best *= one.best;
        one.avg *= one.avg;
        one.worst *= one.worst;
        one.avg_length *= one.avg_length;
        one.best_length *= one.best_length;
        one.feasible_chroms *= one.feasible_chroms;
        one.pop_size *= one.pop_size;
        return one;
    }

    /**
     * Divide object one by the scalar x
     *
     * @param one The object
     * @param x   The scalar
     */
    public PopulationStatistics fastSqrt(PopulationStatistics one) {
        one.best = Math.sqrt(one.best);
        one.avg = Math.sqrt(one.avg);
        one.worst = Math.sqrt(one.worst);
        one.avg_length = Math.sqrt(one.avg_length);
        one.best_length = Math.sqrt(one.best_length);
        one.feasible_chroms = Math.sqrt(one.feasible_chroms);
        one.pop_size = Math.sqrt(one.pop_size);
        return one;
    }
}
