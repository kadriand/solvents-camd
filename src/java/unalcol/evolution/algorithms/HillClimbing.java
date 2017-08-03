package unalcol.evolution.algorithms;

import java.util.Enumeration;
import java.util.Vector;

import unalcol.core.Cloner;
import unalcol.evolution.*;
import unalcol.evolution.operators.ArityOne;
import unalcol.evolution.selections.Elitism;

/**
 * <p>Title: HAEA</p>
 * <p>Description: The Hybrid Adaptive Evolutionary Algorithm proposed by Gomez in
 * "Self Adaptation of Operator Rates in Evolutionary Algorithms", Proceedings of Gecco 2004.</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class HillClimbing extends Transformation {
    /**
     * Set of genetic operators that are used by CEA for evolving the solution chromosomes
     */
    protected ArityOne mutation = null;

    /**
     * The selection scheme between parent-children
     */
    protected Selection selection = null;

    /**
     * Constructor: Creates a CEA transformation function that uses proportional
     * selection between parent-children
     *
     * @param _mutation Genetic operators used to evolve the solution
     */
    public HillClimbing(ArityOne _mutation) {
        mutation = _mutation;
        if (mutation != null) {
            selection = new Elitism(mutation.getEnvironment(), 1, false, 1.0, 0.0);
        } else {
            selection = null;
        }
    }

    /**
     * Constructor
     *
     * @param _mutation  Genetic operators used to evolve the solution
     * @param _selection The selection scheme between parent-children
     */
    public HillClimbing(ArityOne _mutation, Selection _selection) {
        mutation = _mutation;
        selection = _selection;
    }


    /**
     * Transforms the given population to another population according to its rules.
     *
     * @param population The population to be transformed
     * @return A new population built from the given population
     */
    public Population apply(Population population) {
        Population newPopulation = null;
        if (mutation != null) {
            Environment env = population.getEnvironment();
            int size = population.size();
            Vector<Individual> newInd = new Vector<Individual>();
            for (int i = 0; i < size; i++) {
                Vector<Individual> v = mutation.apply(population, i);
                int n = v.size();
                for (int k = 0; k < n; k++) {
                    v.get(k).evalFitness(env);
                }
                Individual par = (Individual) Cloner.clone(population.get(i));
                v.add(0, par);
                Population p = new Population(env, v);
                v = selection.choose(p);
                n = v.size();
                for (int k = 0; k < n; k++) {
                    newInd.add(v.get(k));
                }
            }
            newPopulation = new Population(env, newInd);
        }
        return newPopulation;
    }
}
