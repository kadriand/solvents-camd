package unalcol.evolution.selections;

import java.util.Vector;

import unalcol.evolution.*;
import unalcol.random.*;

/**
 * <p>Title: Tournament</p>
 * <p>Description: A tournament selection strategy. In this strategy each individual that
 * is choosen is selected from a group of individuals. The group of individuals are choosen
 * randomly from the population with a uniform probability. From this group of individuals
 * one is choosen using the fitness as the probability to win the game.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class Tournament extends Selection {
    /**
     * The tournament size
     */
    protected int m = 4;

    /**
     * Constructor: Create a tournament selection strategy with m players that returns
     * n individuals.
     *
     * @param _environment Environment of the Population
     * @param _n           Number of individuals to be choosen
     * @param _includeX    If the individual given in the apply method is going to be selected always or not
     * @param _m           The number of players in the tournament
     */
    public Tournament(Environment _environment, int _n, boolean _includeX, int _m) {
        super(_environment, _n, _includeX);
        m = _m;
    }

    protected void iteration(Population p) {
        Vector v = new Vector();
        int n = p.size();
        Shuffle<Individual> shuffle = new Shuffle<Individual>();
        shuffle.apply(p.individuals);
        if (n % 2 == 1) {
            n--;
            v.add(p.get(n));
        }
        for (int i = 0; i < n; i += 2) {
            if (p.get(i).getFitness() > p.get(i + 1).getFitness()) {
                v.add(p.get(i));
            } else {
                v.add(p.get(i + 1));
            }
        }
        p.setIndividuals(v);
    }

    /**
     * Choose an individuals from the population
     *
     * @param generator  A uniform random generator for selecting the tournament players
     * @param population Population source of the selection process
     * @return Index of the tournament winner individual
     */
    protected Individual choose_one(IntegerGenerator generator, Population population) {
        Vector v = new Vector();
        for (int i = 0; i < m; i++) {
            v.add(population.get(generator.next()));
        }
        Population p = new Population(population.getEnvironment(), v);
        while (p.size() > 1) {
            iteration(p);
        }
        return p.get(0);
//    int k = generator.next();
//    for( int i=1; i<m; i++ ){
//      int k2 = generator.next();
//      if( population.get(k).getFitness() < population.get(k2).getFitness() ){ k = k2; };
//    }
//    return k;
    }

    /**
     * Choose a set of individuals from the population including the individual x
     *
     * @param population Population source of the selection process
     * @param x          Individual to be included in the selection
     */
    public Vector<Individual> choose(Population population, int x) {
        Vector<Individual> sel = null;
        if (population != null) {
            sel = new Vector<Individual>();
            IntegerGenerator g = new IntegerGenerator(population.size());
            int arity = n;
            if (x >= 0 && x < population.size()) {
                arity--;
                sel.add(population.get(x));
            }
            for (int i = 0; i < arity; i++) {
                sel.add(choose_one(g, population));
            }
        }
        return sel;
    }
}
