package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionGroupsManager;

import java.util.ArrayList;

public class MeltingTemp {

    private double sum = 0;
    private ContributionGroupsManager aGC;
    private GroupArray aMolecule;
    private ArrayList<Integer> secondOrderCode;

    public MeltingTemp(Molecule solvent, ArrayList<Integer> secOrderCode, ContributionGroupsManager aGC) {
        this.aGC = aGC;
        secondOrderCode = secOrderCode;
        aMolecule = solvent.getGroupArray();
        aMolecule.optimize();
    }

    public double getMethodResult() {
        for (int i = 0; i < aMolecule.size(); i++) {
            sum += aMolecule.getAmount(i) * aGC.getMeltTemp((aMolecule.getGroupCode(i)));
        }
        sum = sum + calculeSecOrderContribution();
        return 102.425 * Math.log10(sum) * 2.30258509;
    }

    private double calculeSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCode.size(); i++) {
            a += aGC.getTfusSecondOrderParameter(secondOrderCode.get(i));
        }
        return a;
    }


}
