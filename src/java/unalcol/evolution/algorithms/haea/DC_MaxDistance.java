package unalcol.evolution.algorithms.haea;

import unalcol.evolution.*;
import unalcol.math.quasimetric.QuasiMetric;


/**
 * Title:
 * Description: Deterministic Crowding for Haea including a maximum distance for
 * allowing the replacement.
 * Copyright:    Copyright (c) 2006
 * Company: Universidad Nacional de Colombia
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class DC_MaxDistance extends DCHaea {
    /**
     * Maximum distance for allowing the replacement
     */
    protected double max_distance;

    /**
     * Constructor
     *
     * @param _metric       QuasiMetric for calculating the distance between parent and child
     * @param _max_distance Maximum distance for allowing the replacement
     */
    public DC_MaxDistance(Environment _environment, QuasiMetric _metric, double _max_distance) {
        super(_environment, _metric);
        max_distance = _max_distance;
    }

    /**
     * This method determines if the parent can be replaced by the child.
     * It is possible if the child is better than the parent and the distance
     * between the parent and the child is less or equal to max_distance
     *
     * @param child  The child individual
     * @param parent The parent individual
     * @return true if the parent can be replaced, false otherwise
     */
    public boolean can_replace(Individual child, Individual parent) {
        return (super.can_replace(child, parent) && metric.distance(parent, child) <= max_distance);
    }
}
