package unalcol.evolution.selections;

import java.util.Vector;

import unalcol.evolution.*;
import unalcol.random.*;

/**
 * <p>Title: Elitism</p>
 * <p>Description: A elitist selection strategy. In this strategy the best individuals
 * (Elite percentage) are always selected and the worst individuals (cull percentange)
 * are never take into account. The remaining part of the individual is choosen
 * randomly, and each individual has a probability to be choosen that is proportional to
 * its fitness.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class Elitism extends Selection {

    /**
     * Elite percentage: Percentage of individuals to be included in the selection
     * according to their fitness
     */
    protected double elite_percentage = 0.1;
    /**
     * Cull percentage: percentage of individuals to be excluded in the selection
     * according to their fitness
     */
    protected double cull_percentage = 0.1;

    /**
     * Constructor: Create a Elitist selection strategy.
     *
     * @param _environment      Environment of the Population
     * @param _n                Number of individuals to be choosen
     * @param _includeX         If the individual given in the apply method is going to be selected always or not
     * @param _elite_percentage Percentage of individuals to be included in the selection
     * @param _cull_percentage  Percentage of individuals to be excluded in the selection
     */
    public Elitism(Environment _environment, int _n, boolean _includeX,
                   double _elite_percentage, double _cull_percentage) {
        super(_environment, _n, _includeX);
        elite_percentage = _elite_percentage;
        cull_percentage = _cull_percentage;
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
            if (n == 1) {
                if (includeX) {
                    sel.add(population.get(x));
                } else {
                    population.sort();
//          sel.add(population.get(population.size()-1));
                    sel.add(population.get(0));
                }
            } else {
                int k = (int) (population.size() * (1.0 - cull_percentage));
                double[] weight = new double[k];
                double total = k * (k + 1) / 2.0;
                for (int i = 0; i < k; i++) {
                    weight[i] = (k - i) / total;
                }
                WeightedIntegerGenerator generator = new WeightedIntegerGenerator(weight);
                int[] sort_index = new int[population.size()];
                for (int i = 0; i < sort_index.length; i++) {
                    sort_index[i] = i;
                }
                for (int i = 0; i < sort_index.length - 1; i++) {
                    for (int j = i + 1; j < sort_index.length; j++) {
                        if (population.get(sort_index[i]).getFitness() <
                                population.get(sort_index[j]).getFitness()) {
                            int temp = sort_index[i];
                            sort_index[i] = sort_index[j];
                            sort_index[j] = temp;
                        }
                    }
                }
                int m = (int) (population.size() * elite_percentage);
                boolean flag = false;
                sel = new Vector<Individual>();
                for (int i = 0; i < n && i < m; i++) {
                    if (sort_index[i] == x) {
                        flag = true;
                    }
                    sel.add(population.get(sort_index[i]));
                }
                for (int i = m; i < n; i++) {
                    int index = sort_index[generator.next()];
                    if (index == x) {
                        flag = true;
                    }
                    sel.add(population.get(index));
                }
                if (!flag && x >= 0 && x < population.size()) {
                    sel.set(getArity() - 1, population.get(x));
                }
            }
        }
        return sel;
    }
}
