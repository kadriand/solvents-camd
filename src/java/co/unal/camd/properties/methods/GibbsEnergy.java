package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class GibbsEnergy {

    private MoleculeGroups molecule;
    private ArrayList<Integer> secondOrderCodes;

    public GibbsEnergy(Molecule molecule, ArrayList<Integer> SOG) {
        secondOrderCodes = SOG;
        this.molecule = molecule.getGroupsArray();
        this.molecule.optimize();
    }

    public double getMethodResult() {
        double sum = 0;
        for (int i = 0; i < molecule.size(); i++)
            sum += molecule.getAmount()[i] * molecule.getGroupContributions()[i].getGibbsFreeEnergy();
        double g0 = -14.828;
        return sum + g0 + calculateSecOrderContribution();
    }

    private double calculateSecOrderContribution() {
        double sum = 0;
        for (int i = 0; i < secondOrderCodes.size(); i++) {
            int code = secondOrderCodes.get(i);
            sum += CamdRunner.CONTRIBUTION_GROUPS.getSecondOrderContributionsCases().get(code).getGibbsEnergy();
        }
        return sum;
    }

}

