package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.Molecule;
import unalcol.search.variation.Variation_1_1;
import unalcol.services.MicroService;

/**
 */
public class MoleculeMutation extends MicroService<Molecule> implements Variation_1_1<Molecule> {

    public Molecule apply(Molecule genome) {
        //System.out.println("Mutation");
        Molecule clone_genome = genome.clone(); // @TODO: clonar objeto
        // TODO: Mutacion

        int num = (int) (Math.random() * (clone_genome.getSize() - 1));
        GeneticOperator.searchAndReplace(clone_genome.getRootContributionGroup(), num, false);
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
