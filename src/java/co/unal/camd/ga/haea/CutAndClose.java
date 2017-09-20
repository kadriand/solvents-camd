package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionGroupsManager;
import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.Molecule;
import unalcol.search.variation.Variation_1_1;

import static co.unal.camd.properties.estimation.GeneticOperator.searchAndReplace;

public class CutAndClose extends Variation_1_1<Molecule> {

    private ContributionGroupsManager parametersManager;

    public CutAndClose(ContributionGroupsManager parametersManager) {
        this.parametersManager = parametersManager;
    }

    @Override
    public Molecule apply(Molecule genome) {
        //System.out.println("CutAndClose");
        Molecule clone_genome = genome.clone(); // @TODO: clonar objeto

        FunctionalGroupNode newGroup = new FunctionalGroupNode(1);

        int num = (int) (Math.random() * (clone_genome.getTotalGroups()) - 1);
        searchAndReplace(clone_genome.getMoleculeByRootGroup(), num, newGroup, true, parametersManager);
        return clone_genome;
    }
}
