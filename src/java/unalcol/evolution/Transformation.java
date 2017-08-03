package unalcol.evolution;

import unalcol.evolution.statistics.*;

/**
 * <p>Title: Transformation</p>
 * <p>Description: An abstract class for representing the transformation of a population</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public abstract class Transformation {

    /**
     * Init the internal state of transformation
     */
    public void init() {
    }

    /**
     * Transforms the given population to another population according to its rules.
     * Also updates the statistical information of the transformation process
     *
     * @param population The population to be transformed
     * @return A new population built from the given population
     */
    public abstract Population apply(Population population);

    /**
     * Return the statistical information of the population given. includes information
     * of the transformation process if any
     *
     * @param population Population used to calculate the statistical information
     * @return Statistical Information of the transformation process
     */
    public Object statistics(Population population) {
        return population.statistics();
    }
}
