package co.unal.camd.properties.estimation;

import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class MeltingTemp {

    private double sum = 0;
    private MoleculeGroups aMolecule;
    private ArrayList<Integer> secondOrderCode;

    public MeltingTemp(Molecule solvent, ArrayList<Integer> secOrderCode) {
        secondOrderCode = secOrderCode;
        aMolecule = solvent.getGroupArray();
        aMolecule.optimize();
    }

    public double getMethodResult() {
        for (int i = 0; i < aMolecule.size(); i++) {
            sum += aMolecule.getAmount(i) * CamdRunner.CONTRIBUTION_GROUPS.getMeltTemp((aMolecule.getGroupCode(i)));
        }
        sum = sum + calculeSecOrderContribution();
        return 102.425 * Math.log10(sum) * 2.30258509;
    }

    private double calculeSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCode.size(); i++) {
            a += CamdRunner.CONTRIBUTION_GROUPS.getTfusSecondOrderParameter(secondOrderCode.get(i));
        }
        return a;
    }


}
