package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import unalcol.search.variation.Variation_1_1;
import unalcol.services.MicroService;

import static co.unal.camd.ga.haea.GeneticOperator.searchAndReplace;

public class CutAndClose extends MicroService<Molecule> implements Variation_1_1<Molecule> {

    @Override
    public Molecule apply(Molecule genome) {
        //System.out.println("CutAndClose");
        Molecule clone_genome = genome.clone(); // @TODO: clonar objeto
        ContributionGroupNode newGroup = new ContributionGroupNode(1);
        int num = (int) (Math.random() * (clone_genome.getSize()) - 1);
        searchAndReplace(clone_genome.getRootContributionGroup(), num, newGroup, true);
        return clone_genome;
    }
}
