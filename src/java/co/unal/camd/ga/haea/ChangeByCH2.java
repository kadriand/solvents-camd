package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import unalcol.search.variation.Variation_1_1;
import unalcol.services.MicroService;

import static co.unal.camd.ga.haea.GeneticOperator.searchAndReplace;


public class ChangeByCH2 extends MicroService<Molecule> implements Variation_1_1<Molecule> {

    @Override
    public Molecule apply(Molecule genome) {
        //System.out.println("changeByCh2");
        Molecule clone_genome = genome.clone(); // @TODO: clonar objeto
        // TODO: Mutacion
        int num = (int) (Math.random() * (clone_genome.getSize()) - 1);

        ContributionGroupNode newCH2 = new ContributionGroupNode(2);

        newCH2.getSubGroups().add(clone_genome.getGroupAt(num));
        searchAndReplace(clone_genome.getRootContributionGroup(), num, newCH2, true);
        return clone_genome;
    }

}
