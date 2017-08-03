package unalcol.evolution.algorithms.haea;

import java.util.Enumeration;
import java.util.Vector;

import unalcol.core.Cloner;
import unalcol.evolution.*;
import unalcol.evolution.selections.Elitism;
import unalcol.evolution.algorithms.haea.statistics.*;
import unalcol.random.*;

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
public class HAEA extends Transformation {

    /**
     * Set of genetic operators that are used by CEA for evolving the solution chromosomes
     */
    protected HaeaOperators operators = null;

    /**
     * The selection scheme between parent-children
     */
    protected Selection selection = null;


    /**
     * Constructor: Creates a CEA transformation function that uses proportional
     * selection between parent-children
     *
     * @param environment Environment of the evolutionary process
     * @param _operators  Genetic operators used to evolve the solution
     */
    public HAEA(Environment environment, HaeaOperators _operators) {
        operators = _operators;
        if (operators != null) {
            selection = new Elitism(environment, 1, false, 1.0, 0.0);
        } else {
            selection = null;
        }
    }

    /**
     * Constructor
     *
     * @param _operators Genetic operators used to evolve the solution
     * @param _selection The selection scheme between parent-children
     */
    public HAEA(HaeaOperators _operators, Selection _selection) {
        operators = _operators;
        selection = _selection;
    }

    /**
     * Init the internal state of transformation
     */
    public void init() {
        operators.init();
    }


    /**
     * Transforms the given population to another population according to its rules.
     *
     * @param population The population to be transformed
     * @return A new population built from the given population
     */
    public Population apply(Population population) {
        Population newPopulation = null;
        if (operators != null) {
            Environment env = population.getEnvironment();
            operators.setPopulationSize(population.size());
            Vector newRates = new Vector();
            Vector newInd = new Vector();
            double avgFitness = 0.0;
            for (int i = 0; i < population.size(); i++) {
                int oper = operators.selectOperatorIndex(i);
                Operator o = operators.getOperator(i, oper);
                Vector<Individual> v = o.apply(population, i);
                Enumeration<Individual> iter = v.elements();
                while (iter.hasMoreElements()) {
                    iter.nextElement().evalFitness(env);
                }
                Individual parent = population.get(i);
                Individual par = (Individual) Cloner.clone(parent);
                par.setThing(parent.getThing());
                v.add(par);
                Population p = new Population(env, v);
                v = selection.choose(p);
                double pf = parent.getFitness();
                iter = v.elements();
                while (iter.hasMoreElements()) {
                    Individual child = iter.nextElement();
                    double f = child.getFitness();
                    avgFitness += f;
                    if (pf < child.getFitness()) {
                        operators.increase(i, oper);
                    } else {
                        operators.decrease(i, oper);
                    }
                    newInd.add(child);
                }
            }
            newPopulation = new Population(env, newInd);
            operators.update();
        }
        return newPopulation;
    }

    /**
     * Return the statistical information of the population given. includes information
     * of the transformation process if any
     *
     * @param population Population used to calculate the statistical information
     * @return Statistical information of the transformation process
     */
    public Object statistics(Population population) {
        operators.setPopulationSize(population.size());
        return new HAEAStatistics(operators.getRates(), population);
    }
}
