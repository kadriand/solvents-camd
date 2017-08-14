package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionParametersManager;

import java.util.ArrayList;

public class GibbsEnergy {
/////////////////////////////G/////////////////////////////////////////////

    private double sum;
    private ContributionParametersManager aGC;
    private GroupArray aMolecule;
    private ArrayList<Integer> secondOrderCode;

    public GibbsEnergy(Molecule solvent, ArrayList<Integer> SOG, ContributionParametersManager aGC) {
        this.aGC = aGC;
        secondOrderCode = SOG;
        aMolecule = solvent.getGroupArray();
        aMolecule.optimize();
    }

    public double getMethodResult() {
        sum = 0;
        for (int i = 0; i < aMolecule.size(); i++) {
            double g = aGC.getG(aMolecule.getGroupCode(i));
            sum += aMolecule.getAmount(i) * g;
        }
        double g0 = -14.828;
        return sum + (g0) + calculeSecOrderContribution();
    }


    private double calculeSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCode.size(); i++) {
            a += aGC.getGibbsESecondOrderParameter(secondOrderCode.get(i));
        }
        return a;
    }

}
