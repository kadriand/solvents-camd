package unalcol.evolution;

import unalcol.util.iterative.*;
import unalcol.util.Predicate;

/**
 * <p>Title: EvolutionaryAlgorithm</p>
 * <p>Description: A class representing an evolutionary algorithm</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class EvolutionaryAlgorithm extends IterativeAlgorithm {
    /**
     * If the population has to be initialized or not when the init method is called
     */
    protected boolean initPopulation = true;
    /**
     * The population that is been evolved
     */
    protected Population population = null;

    /**
     * The transformation function used to evolve the population
     */
    protected Transformation transformation = null;


    /**
     * Constructor: Creates an evolutionary algorithm with the given population,
     * continuation condition and transformation function
     *
     * @param population     The population to evolved
     * @param transformation The transformation operation
     * @param condition      The evolution condition (the evolutionary process is executed
     *                       until the condition is false)
     */
    public EvolutionaryAlgorithm(Population population,
                                 Transformation transformation,
                                 Predicate condition) {
        super(condition);
        this.population = population;
        this.transformation = transformation;
    }

    /**
     * Constructor: Creates an evolutionary algorithm with the given population
     * and transformation function
     *
     * @param population     The population to evolved
     * @param transformation The transformation operation
     */
    public EvolutionaryAlgorithm(Population population, Transformation transformation) {
        super();
        this.population = population;
        this.transformation = transformation;
        population.evalFitness();
    }

    /**
     * Sets if the population has to be initialized or not when the method is called
     *
     * @param initPopulation
     */
    public void initializePopulation(boolean initPopulation) {
        this.initPopulation = initPopulation;
    }

    /**
     * Initializes the algorithm.
     */
    public void init(Object obj) {
        super.init();
        if (population != null && initPopulation) {
            population.init();
        }
        if (transformation != null) {
            transformation.init();
        }
    }

    /**
     * An evolutionary algorithm iteration
     */
    public void iteration(int k) {
        addToTrace(transformation.statistics(population));
        Population newPopulation = transformation.apply(population);
        population.setIndividuals(newPopulation.individuals);
    }

    /**
     * Returns the statistical information of the population
     *
     * @return The statistical information of the population
     */
    public Object output() {
        return transformation.statistics(population);
    }

    /**
     * Executes the evolutionary algorithm
     */
    public void run() {
        if (population != null && transformation != null) {
            super.run();
        }
    }

    /**
     * Return the individual at the position given
     *
     * @return The individual at the position given if the position is valid, null in other case
     */
    public Individual get(int i) {
        Individual c = null;
        if (population != null) {
            c = population.get(i);
        }
        return c;
    }

    /**
     * Return the current population
     *
     * @return The current population
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * Sets the Population
     *
     * @param population The population
     */
    public void setPopulation(Population population) {
        this.population = population;
    }
}
