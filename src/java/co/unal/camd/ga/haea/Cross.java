package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import unalcol.search.variation.Variation_2_2;
import unalcol.services.MicroService;

public class Cross extends MicroService<Molecule> implements Variation_2_2<Molecule> {


    public Molecule[] apply(Molecule one, Molecule two) {
        // System.out.println("Cross");
        Molecule clone_genome = one.clone(); // @TODO: clonar objeto
        Molecule clone_genome2 = two.clone(); // @TODO: clonar objeto
        int code;
        int num = (int) (Math.random() * (clone_genome.getTotalGroups()) - 1);
        int num2 = (int) (Math.random() * (clone_genome2.getTotalGroups()) - 1);

        ContributionGroupNode aGroup1 = one.getGroupAt(num);
        ContributionGroupNode aGroup2 = two.getGroupAt(num2);

        GeneticOperator.searchAndReplace(clone_genome.getMoleculeByRootGroup(), num, aGroup2, true);
        GeneticOperator.searchAndReplace(clone_genome2.getMoleculeByRootGroup(), num2, aGroup1, true);

        return new Molecule[]{clone_genome, clone_genome2};
    }

}
