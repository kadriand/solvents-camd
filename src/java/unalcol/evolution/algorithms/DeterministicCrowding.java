package unalcol.evolution.algorithms;


import java.util.Vector;

import unalcol.core.Cloner;
import unalcol.evolution.*;
import unalcol.evolution.binary.operators.*;
import unalcol.evolution.operators.*;
import unalcol.evolution.util.IndividualMetric;
import unalcol.math.quasimetric.QuasiMetric;
import unalcol.random.*;

/**
 * <p>Title: DeterministicCrowding</p>
 * <p>Description: The Deterministic Crowding Approach proposed by Mahfoud for niching</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class DeterministicCrowding extends Transformation {
    /**
     * Distance between individuals
     */
    QuasiMetric metric = null;

    /**
     * Crossover Probability
     */
    protected double xoverProbability = 1.0;
    /**
     * Mutation Probability
     */
    protected double mutationProbability = 0.1;

    /**
     * Mutation operator
     */
    protected ArityOne mutation;

    /**
     * Crossover operator
     */
    protected ArityTwo xover;

    /**
     * Creates a Simple Genetic Algorithm Transformation function, with the given
     * selection scheme, and simple crossover, and simple gen mutation operators. The
     * operators have the given probability to be applied
     *
     * @param _metric   Distance between individuals
     * @param mProb     Probability of mutating one bit of the individual
     * @param xoverProb Crossover probability
     */
    public DeterministicCrowding(Environment environment, QuasiMetric _metric,
                                 double mProb, double xoverProb) {
        metric = IndividualMetric.generate(environment, _metric);
        xover = new XOver(environment, new Sequence(environment, 2, true));
        mutation = new Mutation(environment, mProb);
        mutationProbability = 1.0;
        xoverProbability = xoverProb;
    }

    /**
     * Creates a Simple Genetic Algorithm Transformation function, with the given
     * selection scheme, and simple crossover, and simple gen mutation operators. The
     * operators have the given probability to be applied
     *
     * @param _metric   Distance between individuals
     * @param _xover    XOver operator to be used
     * @param _mutation Mutation operator to be used
     * @param mProb     Probability of applying the mutation operator
     * @param xoverProb Crossover probability
     */
    public DeterministicCrowding(QuasiMetric _metric, ArityOne _mutation, ArityTwo _xover,
                                 double mProb, double xoverProb) {
        metric = IndividualMetric.generate(xover.getEnvironment(), _metric);
        xover = _xover;
        mutation = _mutation;
        mutationProbability = mProb;
        xoverProbability = xoverProb;
    }

    /**
     * Transforms the given population to another population according to its rules.
     * Also updates the statistical information of the transformation process
     *
     * @param population The population to be transformed
     * @return A new population built from the given population
     */
    public Population apply(Population population) {
        Environment env = population.getEnvironment();
        int n = population.size();
        Shuffle<Individual> shuffle = new Shuffle<Individual>();
        shuffle.apply(population.individuals);
        Vector ind = new Vector();
        Individual P1, P2, C1, C2;
        for (int i = 0; i < n; i += 2) {
            P1 = population.get(i);
            P2 = population.get(i + 1);
            C1 = (Individual) Cloner.clone(P1);
            C2 = (Individual) Cloner.clone(P2);

            BooleanGenerator random = new BooleanGenerator(xoverProbability);

            if (random.next()) {
                xover.apply(C1.getGenome(), C2.getGenome());
            }

            random = new BooleanGenerator(mutationProbability);
            if (random.next()) {
                mutation.apply(C1.getGenome());
                mutation.apply(C2.getGenome());
            }

            C1.evalFitness(env);
            C2.evalFitness(env);

            if (metric.distance(P1, C1) + metric.distance(P2, C2) <=
                    metric.distance(P1, C2) + metric.distance(P2, C1)) {
                Individual temp = C1;
                C1 = C2;
                C2 = temp;
            }
            if (C1.getFitness() < P1.getFitness()) {
                C1 = P1;
            }
            if (C2.getFitness() < P2.getFitness()) {
                C2 = P2;
            }
            ind.add(C1);
            ind.add(C2);
        }
        return new Population(env, ind);
    }
}
