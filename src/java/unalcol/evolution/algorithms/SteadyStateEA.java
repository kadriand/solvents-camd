package unalcol.evolution.algorithms;

import java.util.Vector;

import unalcol.core.Cloner;
import unalcol.evolution.*;
import unalcol.evolution.binary.operators.XOver;
import unalcol.evolution.operators.ArityTwo;
import unalcol.evolution.selections.Tournament;
import unalcol.random.*;
import unalcol.abs.*;
import unalcol.sort.*;
import unalcol.random.BooleanGenerator;


/**
 * <p>Title: SteadyStateEA</p>
 * <p>Description: An Steady State Genetic Algorithm</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class SteadyStateEA extends GenerationalEA {
    /**
     * Indicates if the population requires to be sorted or not. This is useful for
     * sorting the population only once.
     */
    boolean need_sort = true;

    /**
     * Creates a Steady State Genetic Algorithm Transformation function, with the given
     * selection scheme, simple crossover, and simple gen mutation operators. The
     * operators have the given probability to be applied
     *
     * @param selection Selection scheme to be applied between the offsprings
     * @param mProb     Mutation probability
     * @param xoverProb Crossover probability
     */
    public SteadyStateEA(Selection selection, double mProb, double xoverProb) {
        super(selection, mProb, xoverProb);
        Environment env = selection.getEnvironment();
        operators[1] = new XOver(env, new Tournament(env, 2, true, 2));
    }

    /**
     * Creates a Steady State Genetic Algorithm Transformation function, with the given
     * selection scheme, and simple crossover, and simple gen mutation operators. The
     * operators have the given probability to be applied
     *
     * @param selection Selection scheme to be applied
     * @param xover     XOver operator to be used
     * @param mutation  Mutation operator to be used
     * @param mProb     Mutation probability
     * @param xoverProb Crossover probability
     */
    public SteadyStateEA(Selection selection, Operator mutation, ArityTwo xover,
                         double mProb, double xoverProb) {
        super(selection, mutation, xover, mProb, xoverProb);
    }

    /**
     * Creates a Steady State Genetic Algorithm Transformation function, with the given
     * operators and operator probabilities
     *
     * @param _operator_probabilities Probability that each operator has to be applied
     * @param _operators              Set of operators that can be applied
     */
    public SteadyStateEA(double[] _operator_probabilities, Operator[] _operators) {
        super(_operator_probabilities, _operators);
    }

    /**
     * Init the internal state of transformation
     */
    public void init() {
        need_sort = true;
    }

    /**
     * Transforms the given population to another population according to its rules.
     * Also updates the statistical information of the transformation process
     *
     * @param population The population to be transformed
     * @return A new population built from the given population
     */
    public Population apply(Population population) {
        Vector v = new Vector();
        for (int i = 0; i < population.size(); i++) {
            v.add(Cloner.clone(population.get(i)));
        }
        Environment env = population.getEnvironment();
        population = new Population(env, v);
        if (need_sort) {
            population.sort();
            need_sort = false;
        }
        for (int i = 0; i < population.size(); i++) {
            Vector<Individual> children = population.individuals;
            for (int j = 0; j < operators.length; j++) {
                BooleanGenerator random = new BooleanGenerator(operator_probabilities[i]);
                if (random.next()) {
                    children = operators[j].apply(new Population(env, children), 0);
                }
            }
            population.individuals.remove(population.individuals.size() - 1);
            SortedInsert<Individual> insert = new SortedInsert<Individual>();
            insert.apply(population.individuals, children.get(0),
                    new ReversedOrder(new IndividualOrder()), true);
        }
        return population;
    }
}
