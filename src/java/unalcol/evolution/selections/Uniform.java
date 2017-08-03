package unalcol.evolution.selections;


import unalcol.evolution.*;
import unalcol.random.*;

import java.util.Vector;

/**
 * <p>Title: Uniform</p>
 * <p>Description: The uniform selection operator. In this selection strategy all individuals
 * have the same probability to be choosen</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class Uniform extends Selection {

    /**
     * Constructor: Creates a Uniform selection strategy
     *
     * @param _environment Environment of the Population
     * @param _n           Number of individuals to be choosen
     * @param _includeX    If the individual given in the apply method is going to be selected always or not
     */
    public Uniform(Environment _environment, int _n, boolean _includeX) {
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
            IntegerGenerator generator = new IntegerGenerator(population.size());
            int arity = getArity();
            if (x >= 0 && x < population.size()) {
                arity--;
                sel.add(population.get(x));
            }
            int[] index = generator.raw(arity);
            for (int i = 0; i < arity; i++) {
                sel.add(population.get(index[i]));
            }
        }
        return sel;
    }
}
