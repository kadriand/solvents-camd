package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.FunctionalGroupNode;
import co.unal.camd.properties.model.Molecule;
import unalcol.search.variation.Variation_1_1;
import unalcol.services.MicroService;

import static co.unal.camd.ga.haea.GeneticOperator.searchAndReplace;

public class CutAndReplace extends MicroService<Molecule> implements Variation_1_1<Molecule> {

    @Override
    public Molecule apply(Molecule genome) {
        //System.out.println("CutAndReplace");
        Molecule clone_genome = genome.clone(); // @TODO: clonar objeto
        // TODO: Mutacion
        int num = (int) (Math.random() * (clone_genome.getTotalGroups()) - 1);

        int valence = (int) (Math.random() * 3) + 2;
        boolean functional = false;

        FunctionalGroupNode aGroupMut = clone_genome.getGroupAt(num);
        if (aGroupMut.getRootNode() > 4) {
            functional = true;
        }
        int refCode = MoleculeOperations.findNewRefCode(valence, functional);
        FunctionalGroupNode newGroup = new FunctionalGroupNode(refCode);

        if (valence == 3) {
            newGroup.addGroup(new FunctionalGroupNode(1));
        } else if (valence == 4) {
            newGroup.addGroup(new FunctionalGroupNode(1));
            newGroup.addGroup(new FunctionalGroupNode(1));
        }
        searchAndReplace(clone_genome.getMoleculeByRootGroup(), num, newGroup, false);
        return clone_genome;
    }


}
