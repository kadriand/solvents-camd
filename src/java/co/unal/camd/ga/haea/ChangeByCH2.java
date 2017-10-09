package co.unal.camd.ga.haea;

import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.Molecule;
import unalcol.search.variation.Variation_1_1;
import unalcol.services.MicroService;

import static co.unal.camd.properties.estimation.GeneticOperator.searchAndReplace;


public class ChangeByCH2 extends MicroService<Molecule> implements Variation_1_1<Molecule> {

    @Override
    public Molecule apply(Molecule genome) {
        //System.out.println("changeByCh2");
        Molecule clone_genome = genome.clone(); // @TODO: clonar objeto
        // TODO: Mutacion
        int num = (int) (Math.random() * (clone_genome.getTotalGroups()) - 1);

        FunctionalGroupNode newCH2 = new FunctionalGroupNode(2);

        newCH2.addGroup(clone_genome.getGroupAt(num));
        searchAndReplace(clone_genome.getMoleculeByRootGroup(), num, newCH2, true);
        return clone_genome;
    }

}
