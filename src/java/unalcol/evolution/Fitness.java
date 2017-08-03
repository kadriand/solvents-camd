package unalcol.evolution;

/**
 * <p>Title: Fitness</p>
 * <p>Description: An abstract class that represents a fitness function.</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public abstract class Fitness<T> {

    protected Environment environment;

    /**
     * Determines if the fitness function is non-statoinary. It will help the ea
     * to evaluate the fitness function whenever is required
     */
    protected boolean nonStationary = false;

    /**
     * Evaluates the fitness function of the object given
     *
     * @param obj Object used to calculate the fitness
     * @return The fitness value of the given object
     */
    public abstract double evaluate(T obj);

    /**
     * Determines if the fitness function is not stationary or stationary
     *
     * @return true if the fitness function is not stationary, false if it is stationary
     */
    public boolean isNonStationary() {
        return nonStationary;
    }

    /**
     * Sets the attribute enviroment
     *
     * @param environment The enviroment
     */
    public void setEnvironment(Environment<T, ?> environment) {
        this.environment = environment;
    }
}
