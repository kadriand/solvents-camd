package unalcol.evolution.real.operators;

import unalcol.evolution.*;
import unalcol.evolution.operators.ArityOne;
import unalcol.evolution.real.RealVectorGenotype;
import unalcol.evolution.real.RealVectorLimits;
import unalcol.random.*;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: AddComponent</p>
 * <p>Description: Add a new component to the encoded double[]</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class AddComponent extends ArityOne<double[]> {
    /**
     * Default Constructor
     *
     * @param environment The environment
     */
    public AddComponent(Environment environment) {
        super(environment);
    }

    /**
     * Adds a new component to the vector encoded in the genome
     *
     * @param gen Genome, this cant be modifiend (double[])
     * @return Genome modified
     */
    public Vector<double[]> apply(double[] gen) {
        double[] genome = null;
        RealVectorLimits limits = ((RealVectorGenotype) environment.getGenotype()).getLimits();
        if (genome.length < limits.getMaxGenes()) {
            double[] components = (double[]) gen;
            int n = components.length;
            genome = new double[n + 1];
            for (int i = 0; i < n; i++) {
                genome[i] = components[i];
            }
            double min = limits.min[n];
            double max = limits.max[n];
            UniformGenerator g = new UniformGenerator(min, max);
            genome[n] = g.next();
        } else {
            genome = (double[]) Cloner.clone(gen);
        }
        Vector<double[]> v = new Vector<double[]>();
        v.add(genome);
        return v;
    }

    /**
     * Testing function
     *
     * @param argv main
     */
    public static void main(String[] argv) {
        System.out.println("*** Generating a genome of genes randomly ***");
        double[] min = new double[]{3, -10.0, 5};
        double[] max = new double[]{3, 10.0, -5};
        RealVectorLimits limits = new RealVectorLimits(min, max, 3, 1);
        double[] genome = new double[]{1};
        RealVectorGenotype a = new RealVectorGenotype(limits);
        System.out.println(genome[0]);
        AddComponent add = new AddComponent(new Environment(a, null));
        System.out.println("*** Applying the mutation ***");
        genome = add.apply(genome).get(0);
        System.out.println(genome.toString());
    }


}
