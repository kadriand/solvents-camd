package co.unal.camd.ga.haea;

import co.unal.camd.properties.estimation.GeneticOperator;
import co.unal.camd.properties.estimation.Molecule;
import unalcol.search.variation.Variation_1_1;

/**
 */
public class MoleculeMutation extends Variation_1_1<Molecule> {

    public Molecule apply(Molecule genome) {
        //System.out.println("Mutation");
        Molecule clone_genome = genome.clone(); // @TODO: clonar objeto
        // TODO: Mutacion

        int num = (int) (Math.random() * (clone_genome.getTotalGroups() - 1));
        GeneticOperator.searchAndReplace(clone_genome.getMoleculeByRootGroup(), num, false);
        return clone_genome;
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
