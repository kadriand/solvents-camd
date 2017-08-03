package unalcol.evolution;

import unalcol.core.Cloner;

/**
 * <p>Title: Individual</p>
 * <p>Description: An abstract individual class representation. It is abstract because the
 * evaluate fitness method has to be overwritten for every subclass</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class Individual<G, P> implements Cloneable {
    /**
     * Chromosome of the individual
     */
    protected G genome;

    /**
     * Thing represented by the individual
     */
    protected P thing = null;

    /**
     * Individual fitness value
     */
    protected double fitness = -1.0e108;

    /**
     * Constructor: Creates an individual with the given genome
     *
     * @param genome Chromosome of the individual
     */
    public Individual(G genome) {
        this.genome = genome;
    }

    /**
     * Constructor: Creates an individual
     *
     * @param genome  The genome of the individual
     * @param thing   The thing represented by the individual
     * @param fitness The fitness of the individual
     */
    public Individual(G genome, P thing, double fitness) {
        this.genome = genome;
        this.thing = thing;
        this.fitness = fitness;
    }

    /**
     * Constructor: Creates a clone from the given Individual
     *
     * @param source Individual to be cloned
     */
    public Individual(Individual<G, P> source) {
        if (source != null) {
            fitness = source.fitness;
            genome = (G) Cloner.clone(source.genome);
            thing = null;
        }
    }

    /**
     * Creates a clone
     *
     * @return a clone of the individual
     */
    public Object clone() {
        return new Individual(this);
    }

    /**
     * Gets the thing represented by the individual
     *
     * @param env The enviroment
     * @return Thing represented by the individual
     */
    public P getThing(Environment<P, G> env) {
        if (thing == null) {
            thing = env.getPhenotype().get(genome);
        }
        return thing;
    }

    /**
     * Sets the attribute "thing"
     *
     * @param thing The thing represented by the individual
     */
    public void setThing(P thing) {
        this.thing = thing;
    }

    /**
     * Return the attribute "thing"
     *
     * @return The thing represented by the individual
     */
    public P getThing() {
        return thing;
    }

    /**
     * Returns the genome of the individual
     *
     * @return Individual's genome
     */
    public G getGenome() {
        return genome;
    }

    /**
     * Return an individual converted in a string
     *
     * @return A String
     */
    public String toString() {
        if (thing != null) {
            if (thing instanceof double[]) {
                double[] x = (double[]) thing;
                StringBuffer sb = new StringBuffer();
                sb.append("[");
                for (int i = 0; i < x.length - 1; i++) {
                    sb.append(x[i] + ",");
                }
                if (x.length > 0) {
                    sb.append(x[x.length - 1]);
                }
                sb.append("]");
                return sb.toString();
            } else {
                return thing.toString();
            }
        } else {
            return genome.toString();
        }
    }

    /**
     * Returns the fitness of the individual.
     *
     * @return Individual's fitness
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * Sets the fitness of the individual.
     *
     * @param fitness The individual's fitness
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Calculates the fitness of the individual.
     *
     * @return Individual's fitness
     */
    public double evalFitness(Environment<P, G> env) {
        Fitness<P> f = env.getFitness();
        fitness = f.evaluate(getThing(env));
        return fitness;
    }

    /**
     * Determines if the genotype represents a valid individual
     *
     * @return true if the genotype represents a valid individual, false in other case
     */
    public boolean isFeasible() {
        return true;
    }

}
