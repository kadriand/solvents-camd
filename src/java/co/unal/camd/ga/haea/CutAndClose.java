package co.unal.camd.ga.haea;

import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.Molecule;
import unalcol.search.variation.Variation_1_1;
import unalcol.services.MicroService;

import static co.unal.camd.properties.estimation.GeneticOperator.searchAndReplace;

public class CutAndClose extends MicroService<Molecule> implements Variation_1_1<Molecule> {

    @Override
    public Molecule apply(Molecule genome) {
        //System.out.println("CutAndClose");
        Molecule clone_genome = genome.clone(); // @TODO: clonar objeto

        FunctionalGroupNode newGroup = new FunctionalGroupNode(1);

        int num = (int) (Math.random() * (clone_genome.getTotalGroups()) - 1);
        searchAndReplace(clone_genome.getMoleculeByRootGroup(), num, newGroup, true);
        return clone_genome;
    }
}
