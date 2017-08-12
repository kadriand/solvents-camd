package co.unal.camd.properties.estimation;

import unalcol.evolution.Environment;
import unalcol.evolution.Individual;
import unalcol.evolution.Population;

import java.util.Vector;


public class ChangeByCH2 extends GeneticOperator {

    public ChangeByCH2(Environment _environment) {
        super(_environment);
    }

    public Vector<Molecules> apply(Molecules genome) {
        //System.out.println("changeByCh2");
        Molecules clone_genome = genome.clone(); // @TODO: clonar objeto
        // TODO: Mutacion
        int num = (int) (Math.random() * (clone_genome.getTotalGroups()) - 1);

        FunctionalGroupNode newCH2 = new FunctionalGroupNode(2);

        newCH2.addGroup(clone_genome.getGroupAt(num));
        searchAndReplace(clone_genome.getMoleculeByRootGroup(), num, newCH2, true, ((MoleculesEnvironment) environment).aGC);
        Vector<Molecules> v = new Vector<Molecules>();
        v.add(clone_genome);
        return v;
    }

    /**
     * Apply the operator over the given individuals
     *
     * @param population Source population
     * @param x          Individual used as first parent
     * @return A collection of individuals generated by the operator
     */
    public Vector<Individual> apply(Population population, int x) {
        Vector<Individual> v = null;
        if (population != null) {
            v = new Vector<Individual>();
            Molecules genome = (Molecules) population.get(x).getGenome();
            Vector<Molecules> genomes = apply(genome);
            int n = genomes.size();
            for (int i = 0; i < n; i++) {
                v.add(new Individual(genomes.get(i)));
            }
        }
        return v;
    }

    /**
     * Return the genetic operator arity
     *
     * @return the genetic operator arity
     */
    public int getArity() {
        return 1;
    }

}
