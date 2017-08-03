package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.operators.ArityOne;
import unalcol.evolution.real.RealVectorGenotype;
import unalcol.evolution.real.RealVectorLimits;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: DelComponent</p>
 * <p>Description: Deletes the last conponent of the encoded RealVector</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universida Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class DelComponent extends ArityOne<double[]> {
    /**
     * Default constructor
     *
     * @param environment The environment
     */
    public DelComponent(Environment environment) {
        super(environment);
    }

    /**
     * Dels the last component of the vector encoded in the genome
     *
     * @param gen Genome to be modified
     * @return Genome modified (double[])
     */
    public Vector<double[]> apply(double[] gen) {
        double[] genome = null;
        RealVectorLimits limits = ((RealVectorGenotype) environment.getGenotype()).getLimits();
        if (genome.length > limits.getMinGenes()) {
            double[] components = (double[]) gen;
            int n = components.length;
            genome = new double[n - 1];
            for (int i = 0; i < n; i++) {
                genome[i] = components[i];
            }
        } else {
            genome = (double[]) Cloner.clone(gen);
        }
        Vector<double[]> v = new Vector<double[]>();
        v.add(genome);
        return v;
    }

    /**
     * Testing function
     */
    public static void main(String[] argv) {
        System.out.println("*** Generating a genome of genes randomly ***");
        double[] min = new double[]{3, -10.0, 5};
        double[] max = new double[]{3, 10.0, -5};
        RealVectorLimits limits = new RealVectorLimits(min, max, 3, 2);
        double[] genome = new double[]{1, 5, 8};
        RealVectorGenotype a = new RealVectorGenotype(limits);
        System.out.println(genome[0]);
        DelComponent del = new DelComponent(new Environment(a, null));
        System.out.println("*** Applying the mutation ***");
        genome = del.apply(genome).get(0);
        System.out.println(genome.toString());
    }

}
