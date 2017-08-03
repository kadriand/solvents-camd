package unalcol.evolution.statistics;

import unalcol.*;
import unalcol.abs.Addition;


/**
 * <p>Title: PopStatAddition</p>
 * <p>Description: Adds and Subtracts matrices.</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class PopStatAddition extends Addition<PopulationStatistics> {
    /**
     * Adds to the second PopStat the first PopStat. The addition process is component by component
     * x[i,j] = x[i,j] + y[i,j] for all i=1..n and j=1..m
     * The result of the operation is stored in the first PopStat.
     *
     * @param x The first PopStat
     * @param y The PopStat to be added
     */
    public PopulationStatistics fastSum(PopulationStatistics x, PopulationStatistics y) {
        x.best += y.best;
        x.avg += y.avg;
        x.worst += y.worst;
        x.avg_length += y.avg_length;
        x.best_length += y.best_length;
        x.feasible_chroms += y.feasible_chroms;
        x.pop_size += y.pop_size;
        return x;
    }

    /**
     * Substracts a PopStat from the first PopStat.
     * The substraction process is component by component. The substraction process is component by component
     * x[i,j] = x[i,j] - y[i,j] for all i=1..n and j=1..m
     * The result of the operation is stored in the first PopStat.
     *
     * @param x The first PopStat
     * @param y The PopStat to be substracted
     */
    public PopulationStatistics fastSubstract(PopulationStatistics x, PopulationStatistics y) {
        x.best -= y.best;
        x.avg -= y.avg;
        x.worst -= y.worst;
        x.avg_length -= y.avg_length;
        x.best_length -= y.best_length;
        x.feasible_chroms -= y.feasible_chroms;
        x.pop_size -= y.pop_size;
        return x;
    }
}
