package unalcol.evolution.algorithms.haea;

import java.util.Vector;

import unalcol.evolution.*;
import unalcol.math.quasimetric.QuasiMetric;

/**
 * <p>Title: DCHaea </p>
 * <p>Description: Deterministic Crowding for Haea as proposed by Gomez in
 * "Self Adaptation of Operator Rates for Multimodal Optimization", Proceedings of CEC-2004</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class DCHaea extends Selection {
    /**
     * Quasimetric used to determine the closest offspring to the parent
     */
    protected QuasiMetric metric = null;

    /**
     * Constructor: Create a DC selection operator that chooses one individuals from a population
     */
    public DCHaea(Environment _environment, QuasiMetric _metric) {
        super(_environment, 1);
        metric = _metric;
//    metric = IndividualMetric.generate(_environment, _metric);
    }

    /**
     * Choose an individual from the population including the individual x
     *
     * @param population Population source of the selection process
     * @param x          Individual to be included in the selection
     */
    public Vector<Individual> choose(Population population, int x) {
        Vector<Individual> v = null;
        if (x >= 0 && x < population.size()) {
            v = new Vector<Individual>();
            v.add(population.get(x));
        } else {
            int m = population.size();
            // The parent is the first individual in the population
            Individual p = population.get(0);
            // determining the closest offspring to the parent
            Individual c = population.get(1);
            double d = metric.distance(p, c);
            for (int i = 2; i < m; i++) {
                double d2 = metric.distance(p, population.get(i));
                if (d2 < d) {
                    d = d2;
                    c = population.get(i);
                }
            }
            // determining if the parent can be replaced with the child
            if (can_replace(c, p)) {
                v = new Vector<Individual>();
                v.add(c);
            } else {
                v = new Vector<Individual>();
                v.add(p);
            }
        }
        return v;
    }

    /**
     * This method determines if the parent can be replaced by the child.
     * It is possible if the child is better than the parent
     *
     * @param child  The child individual
     * @param parent The parent individual
     * @return true if the parent can be replaced, false otherwise
     */
    public boolean can_replace(Individual child, Individual parent) {
//    return (child.getFitness() > parent.getFitness());
        return (child.evalFitness(environment) > parent.evalFitness(environment));
    }
}
