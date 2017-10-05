package co.unal.camd.properties.estimation;

import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class GibbsEnergy {
    /////////////////////////////G/////////////////////////////////////////////

    private double sum;
    private MoleculeGroups aMolecule;
    private ArrayList<Integer> secondOrderCodes;

    public GibbsEnergy(Molecule molecule, ArrayList<Integer> SOG) {
        secondOrderCodes = SOG;
        aMolecule = molecule.getGroupsArray();
        aMolecule.optimize();
    }

    public double getMethodResult() {
        sum = 0;
        for (int i = 0; i < aMolecule.size(); i++) {
            double g = CamdRunner.CONTRIBUTION_GROUPS.getG(aMolecule.getGroupCode(i));
            sum += aMolecule.getAmount(i) * g;
        }
        double g0 = -14.828;
        return sum + (g0) + calculateSecOrderContribution();
    }


    private double calculateSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCodes.size(); i++) {
            a += CamdRunner.CONTRIBUTION_GROUPS.getGibbsESecondOrderParameter(secondOrderCodes.get(i));
        }
        return a;
    }

}
