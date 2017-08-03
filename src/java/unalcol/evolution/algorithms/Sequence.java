package unalcol.evolution.algorithms;

import java.util.Vector;

import unalcol.evolution.*;
import unalcol.random.*;


/**
 * <p>Title:Sequence</p>
 * <p>Description:A selection operator that select the indivduals in sequential order</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class Sequence extends Selection {

    /**
     * Constructor: Create a Sequental selection strategy.
     *
     * @param _n        Number of individuals to be choosen
     * @param _includeX If the individual given in the apply method is going to be selected always or not
     *                  If it is false the sequential selection will choose n consecutive individuals
     *                  starting at a random position
     */
    public Sequence(Environment _environment, int _n, boolean _includeX) {
        super(_environment, _n, _includeX);
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
            if (x < 0 || x >= population.size()) {
                IntegerGenerator g = new IntegerGenerator(population.size());
                x = g.next();
            }
            for (int i = 0; i < n; i++) {
                sel.add(population.get(x));
                x++;
                if (x == population.size()) {
                    x = 0;
                }
            }
        }
        return sel;
    }
}
