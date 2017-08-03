package unalcol.evolution.statistics;

import java.util.Enumeration;
import java.util.Vector;

import unalcol.evolution.*;


/**
 * Title: PopulationStatistics</p>
 * Description: Statistical information of a population</p>
 * Copyright:    Copyright (c) 2006</p>
 * Company: Universidad Nacional de Colombia
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class PopulationStatistics implements Cloneable {
    /**
     * The population
     */
    protected Population population = null;
    /**
     * Population size
     */
    public double pop_size;
    /**
     * Best individual index
     */
    protected int bestIndex;
    /**
     * Best individual
     */
    protected Individual bestIndividual;
    /**
     * Fitness of the best individual
     */
    public double best;
    /**
     * Fitness of the worst individual
     */
    public double worst;
    /**
     * Average Fitness
     */
    public double avg;
    /**
     * Average genome length
     */
    public double avg_length;
    /**
     * Length of the best individual
     */
    public double best_length;
    /**
     * Number of feasible individuals
     */
    public double feasible_chroms;

    /**
     * Constructor: Default
     */
    public PopulationStatistics() {
    }

    /**
     * Constructor: Creastes a PopulationStatistics
     *
     * @param _population The population
     */
    public PopulationStatistics(Population _population) {
        population = _population;
        Genotype genotype = population.getEnvironment().getGenotype();
        Vector<Individual> individuals = population.individuals;

        Enumeration<Individual> iter = individuals.elements();

        Individual ind = iter.nextElement();

        best = ind.getFitness();
        worst = ind.getFitness();
        avg = ind.getFitness();
        avg_length = (double) genotype.size(ind.getGenome());

        feasible_chroms = 0.0;
        if (ind.isFeasible()) {
            feasible_chroms++;
        }

        int best_index = 0;
        int i = 0;
        while (iter.hasMoreElements()) {
            i++;
            ind = iter.nextElement();
            if (best < ind.getFitness()) {
                best = ind.getFitness();
                best_index = i;
            } else {
                if (worst > ind.getFitness()) {
                    worst = ind.getFitness();
                }
            }
            avg += ind.getFitness();
            avg_length += (double) genotype.size(ind.getGenome());
            if (ind.isFeasible()) {
                feasible_chroms++;
            }
        }

        bestIndividual = individuals.get(best_index);
        bestIndex = best_index;
        pop_size = individuals.size();
        avg /= pop_size;
        avg_length /= pop_size;
        feasible_chroms /= pop_size;
        best_length = (double) genotype.size(bestIndividual.getGenome());
    }

    /**
     * Copy contructor
     *
     * @param source The Statistical information to be cloned
     */
    public PopulationStatistics(PopulationStatistics source) {
        population = source.population;
        bestIndividual = source.bestIndividual;
        bestIndex = source.bestIndex;
        best = source.best;
        avg = source.avg;
        worst = source.worst;
        feasible_chroms = source.feasible_chroms;
        best_length = source.best_length;
        avg_length = source.avg_length;
        pop_size = source.pop_size;
    }

    /**
     * Clones the statistical information
     *
     * @return A cloned statistical information
     */
    public Object clone() {
        return new PopulationStatistics(this);
    }

    /**
     * Converts the population statistical information to a string
     *
     * @return A string with the population statistical information
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(' ');
        sb.append(bestIndividual);
        sb.append(' ');
        sb.append(best);
        sb.append(' ');
        sb.append(avg);
        sb.append(' ');
        sb.append(worst);
        sb.append(' ');
        sb.append(best_length);
        sb.append(' ');
        sb.append(avg_length);
        sb.append(' ');
        sb.append(pop_size);
        sb.append(' ');
        sb.append(feasible_chroms);
        return sb.toString();
    }

    /**
     * Gets the best individual
     *
     * @return The best individual in the population
     */
    public Individual getBest() {
        return bestIndividual;
    }

    /**
     * Gets the best individual index
     *
     * @return Index of the best individual in the population
     */
    public int getBestIndex() {
        return bestIndex;
    }

    /**
     * Gets the population
     *
     * @return The population
     */
    public Population getPopulation() {
        return population;
    }
}
