package unalcol.evolution.util;

import unalcol.evolution.*;
import unalcol.math.quasimetric.QuasiMetric;

/**
 * <p>Title: IndividualMetric</p>
 * <p>Description: To get the distance between to individuals</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class IndividualMetric<G, P> extends QuasiMetric<Individual<G, P>> {
    /**
     * The environment of IndividualMetrics
     */
    protected Environment<P, G> env;
    /**
     * A QuasiMetric
     */
    protected QuasiMetric<P> metric;

    /**
     * Default constructor
     *
     * @param _env    The environment
     * @param _metric The Quasimetric
     */
    public IndividualMetric(Environment<P, G> _env, QuasiMetric<P> _metric) {
        metric = _metric;
        env = _env;
    }

    /**
     * Distance (Implement this unalcol.util.quasimetric.QuasiMetric method)
     *
     * @param one First Object
     * @param two Second Object
     * @return double
     */
    public double distance(Individual<G, P> one, Individual<G, P> two) {
        return metric.distance(one.getThing(env), two.getThing(env));
    }

    /**
     * Generates a IndividualMetric
     *
     * @param environment The environment
     * @param metric      The QuasiMetric
     * @return A IndividualMetrics
     */
    public static QuasiMetric generate(Environment environment, QuasiMetric metric) {
        if (metric instanceof IndividualMetric) {
            return metric;
        } else {
            return new IndividualMetric(environment, metric);
        }
    }
}
