package unalcol.evolution.algorithms;


import java.util.Enumeration;
import java.util.Vector;

import unalcol.evolution.*;
import unalcol.evolution.binary.operators.*;
import unalcol.evolution.operators.ArityTwo;
import unalcol.random.*;

/**
 * <p>Title: GenerationalEA</p>
 * <p>Description: The Generational Genetic Algorithm (includes the Simple Genetic Algorithm)</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class GenerationalEA extends Transformation {

    /**
     * Probability that each operator has to be applied
     */
    protected double[] operator_probabilities = null;
    /**
     * Set of operators that can be applied
     */
    protected Operator[] operators = null;

    /**
     * Creates a Simple Genetic Algorithm Transformation function, with the given
     * selection scheme, and simple crossover, and simple gen mutation operators. The
     * operators have the given probability to be applied
     *
     * @param selection Selection scheme to be applied
     * @param mProb     Mutation probability
     * @param xoverProb Crossover probability
     */
    public GenerationalEA(Selection selection,
                          double mProb, double xoverProb) {
        Environment env = selection.getEnvironment();
        operators = new Operator[3];
        operators[0] = selection;
        operators[1] = new XOver(env, new Sequence(env, 2, true));
        operators[2] = new Mutation(env, mProb);

        operator_probabilities = new double[3];
        operator_probabilities[0] = 1.0;
        operator_probabilities[1] = xoverProb;
        operator_probabilities[2] = 1.0;
    }

    /**
     * Creates a Simple Genetic Algorithm Transformation function, with the given
     * selection scheme, and simple crossover, and simple gen mutation operators. The
     * operators have the given probability to be applied
     *
     * @param selection Selection scheme to be applied
     * @param xover     XOver operator to be used
     * @param mutation  Mutation operator to be used
     * @param mProb     Mutation probability
     * @param xoverProb Crossover probability
     */
    public GenerationalEA(Selection selection, Operator mutation, ArityTwo xover,
                          double mProb, double xoverProb) {
        Environment env = selection.getEnvironment();
        operators = new Operator[3];
        operators[0] = selection;
        operators[1] = xover;
        xover.setSelection(new Sequence(env, 2, true));
        operators[2] = mutation;

        operator_probabilities = new double[3];
        operator_probabilities[0] = 1.0;
        operator_probabilities[1] = xoverProb;
        operator_probabilities[2] = mProb;
    }

    /**
     * Creates a Simple Genetic Algorithm Transformation function, with the given
     * operators and operator probabilities
     *
     * @param _operator_probabilities Probability that each operator has to be applied
     * @param _operators              Set of operators that can be applied
     */
    public GenerationalEA(double[] _operator_probabilities, Operator[] _operators) {
        operators = _operators;
        operator_probabilities = _operator_probabilities;
    }

    /**
     * Transforms the given population to another population according to its rules.
     * Also updates the statistical information of the transformation process
     *
     * @param population The population to be transformed
     * @return A new population built from the given population
     */
    public Population apply(Population population) {
        if (operators != null) {
            for (int i = 0; i < operators.length; i++) {
                int n = operators[i].getArity();
                int m = population.size() / n;
                Vector ind = new Vector();
                int k = 0;
                for (int j = 0; j < m; j++) {
                    BooleanGenerator random = new BooleanGenerator(operator_probabilities[i]);
                    if (random.next()) {
                        Vector<Individual> subInd = operators[i].apply(population, k);
                        int size = subInd.size();
                        for (int h = 0; h < size; h++) {
                            ind.add(subInd.get(h));
                        }
                    } else {
                        for (int h = 0; h < n; h++) {
                            ind.add(population.get(k + h));
                        }
                    }
                    k += n;
                }
                population = new Population(population.getEnvironment(), ind);
            }
        }
        return population;
    }
}
