package unalcol.evolution.algorithms.haea.statistics;

import unalcol.evolution.statistics.*;
import unalcol.evolution.*;
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

public class HaeaStatAddition extends PopStatAddition {
    /**
     * Adds to the second PopStat the first PopStat. The addition process is component by component
     * x[i,j] = x[i,j] + y[i,j] for all i=1..n and j=1..m
     * The result of the operation is stored in the first PopStat.
     *
     * @param x The first PopStat
     * @param y The PopStat to be added
     */
    public PopulationStatistics fastSum(PopulationStatistics x, PopulationStatistics y) {
        super.fastSum(x, y);
        HAEAStatistics hx = (HAEAStatistics) x;
        HAEAStatistics hy = (HAEAStatistics) y;
        try {
            for (int i = 0; i < hx.opers.length; i++) {
                hx.opers[i] += hy.opers[i];
            }
            for (int i = 0; i < hx.avg_opers.length; i++) {
                hx.avg_opers[i] += hy.avg_opers[i];
            }
        } catch (Exception e) {
        }
        return hx;

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
        super.fastSubstract(x, y);
        HAEAStatistics hx = (HAEAStatistics) x;
        HAEAStatistics hy = (HAEAStatistics) y;
        try {
            for (int i = 0; i < hx.opers.length; i++) {
                hx.opers[i] -= hy.opers[i];
            }
            for (int i = 0; i < hx.avg_opers.length; i++) {
                hx.avg_opers[i] -= hy.avg_opers[i];
            }
        } catch (Exception e) {
        }
        return hx;
    }
}
