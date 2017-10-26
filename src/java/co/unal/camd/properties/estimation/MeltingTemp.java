package co.unal.camd.properties.estimation;

import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class MeltingTemp {

    private double sum = 0;
    private MoleculeGroups aMolecule;
    private ArrayList<Integer> secondOrderCodes;

    public MeltingTemp(Molecule molecule, ArrayList<Integer> secOrderCode) {
        secondOrderCodes = secOrderCode;
        aMolecule = molecule.getGroupsArray();
        aMolecule.optimize();
    }

    public double getMethodResult() {
        for (int i = 0; i < aMolecule.size(); i++) {
            sum += aMolecule.getAmount(i) * CamdRunner.CONTRIBUTION_GROUPS.getMeltTemp((aMolecule.getGroupCode(i)));
        }
        sum = sum + calculateSecOrderContribution();
        return 102.425 * Math.log10(sum) * 2.30258509;
    }

    private double calculateSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCodes.size(); i++) {
            a += CamdRunner.CONTRIBUTION_GROUPS.getFusionTempSecondOrderParameter(secondOrderCodes.get(i));
        }
        return a;
    }


}
