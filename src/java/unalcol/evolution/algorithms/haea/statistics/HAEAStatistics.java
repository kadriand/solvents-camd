package unalcol.evolution.algorithms.haea.statistics;

import java.util.Vector;

import unalcol.evolution.statistics.PopulationStatistics;
import unalcol.evolution.Population;


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
public class HAEAStatistics extends PopulationStatistics implements Cloneable {
    public static boolean printOperatorStat = true;

    /**
     * Operators rates for the best individual
     */
    protected double[] opers = null;
    /**
     * Average operator rates
     */
    protected double[] avg_opers = null;

    /**
     * Constructor: Creates a empty CEA statistics information
     *
     * @param opers_number NUmber of genetic operators rates for each individual in the population
     * @param popStat      The population statistical information
     */
    public HAEAStatistics(int opers_number, Population pop) {
        super(pop);
        opers = new double[opers_number];
        avg_opers = new double[opers_number];
    }

    /**
     * Constructor: Creates a CEA statistics using the information from the population
     * and the operators rate information
     *
     * @param rates   Genetic operators rates for each individual in the population
     * @param popStat The population statistical information
     */
    public HAEAStatistics(Vector rates, Population pop) {
        super(pop);
        if (rates != null && rates.size() > 0) {
            int bestIndex = getBestIndex();
            int opers_number = ((double[]) rates.get(0)).length;
            opers = new double[opers_number];
            avg_opers = new double[opers_number];
            for (int j = 0; j < opers_number; j++) {
                if (bestIndex != -1) {
                    opers[j] = ((double[]) rates.get(bestIndex))[j];
                } else {
                    opers[j] = 0.0;
                }
                avg_opers[j] = 0.0;
                for (int i = 0; i < rates.size(); i++) {
                    avg_opers[j] += ((double[]) rates.get(i))[j];
                }
                avg_opers[j] /= rates.size();
            }
        }
    }

    /**
     * Copy contructor
     *
     * @param source The Statistical information to be cloned
     */
    public HAEAStatistics(PopulationStatistics source) {
        super(source);
        if (source instanceof HAEAStatistics && ((HAEAStatistics) source).opers != null) {
            HAEAStatistics hsource = (HAEAStatistics) source;
            opers = new double[hsource.opers.length];
            avg_opers = new double[hsource.avg_opers.length];
            for (int i = 0; i < opers.length; i++) {
                opers[i] = hsource.opers[i];
                avg_opers[i] = hsource.avg_opers[i];
            }
        }
    }

    /**
     * Clones the statistical information
     *
     * @return A cloned statistical information
     */
    public Object clone() {
        return new HAEAStatistics(this);
    }

    /**
     * Converts the statistical information to a string
     *
     * @return A string with the population and transformation statistical information
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        if (opers != null && printOperatorStat) {
            for (int i = 0; i < opers.length; i++) {
                sb.append(' ');
                sb.append(opers[i]);
            }
            for (int i = 0; i < avg_opers.length; i++) {
                sb.append(' ');
                sb.append(avg_opers[i]);
            }
        }
        return sb.toString();
    }
}
