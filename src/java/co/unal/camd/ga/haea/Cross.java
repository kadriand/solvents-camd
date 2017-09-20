package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionGroupsManager;
import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.GeneticOperator;
import co.unal.camd.properties.estimation.Molecule;
import unalcol.search.variation.Variation_2_2;

public class Cross extends Variation_2_2<Molecule> {

    private ContributionGroupsManager parametersManager;

    public Cross(ContributionGroupsManager parametersManager) {
        this.parametersManager = parametersManager;
    }

    public Molecule[] apply(Molecule one, Molecule two) {
        // System.out.println("Cross");
        Molecule clone_genome = one.clone(); // @TODO: clonar objeto
        Molecule clone_genome2 = two.clone(); // @TODO: clonar objeto
        int code;
        int num = (int) (Math.random() * (clone_genome.getTotalGroups()) - 1);
        int num2 = (int) (Math.random() * (clone_genome2.getTotalGroups()) - 1);

        FunctionalGroupNode aGroup1 = one.getGroupAt(num);
        FunctionalGroupNode aGroup2 = two.getGroupAt(num2);

        GeneticOperator.searchAndReplace(clone_genome.getMoleculeByRootGroup(), num, aGroup2, true, parametersManager);
        GeneticOperator.searchAndReplace(clone_genome2.getMoleculeByRootGroup(), num2, aGroup1, true, parametersManager);

        return new Molecule[]{clone_genome, clone_genome2};
    }

}
