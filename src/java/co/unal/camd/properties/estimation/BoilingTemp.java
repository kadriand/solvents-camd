package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionGroupsManager;

import java.util.ArrayList;

public class BoilingTemp {

    private double sum = 0;
    private ContributionGroupsManager aGC;
    private GroupArray aMolecule;
    private ArrayList<Integer> secondOrderCode;

    public BoilingTemp(Molecule solvent, ArrayList<Integer> secOrderCode, ContributionGroupsManager aGC) {
        this.aGC = aGC;
        this.secondOrderCode = secOrderCode;
        aMolecule = solvent.getGroupArray();
        aMolecule.optimize();
    }

    public double getMethodResult() {
        for (int i = 0; i < aMolecule.size(); i++) {
            double q = aGC.getConstantPTeb(aMolecule.getGroupCode(i));
            sum += aMolecule.getAmount(i) * q;
        }
        sum = sum + calculeSecOrderContribution();
        return 204.359 * Math.log10(sum) * 2.30258509;
    }

    private double calculeSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCode.size(); i++) {
            int so = secondOrderCode.get(i);
            //System.out.println("so :"+so);
            a += aGC.getTemperatureSecondOrderParameter(so);

            //System.out.println("sum :"+sum);
        }
        //System.out.println("a :"+a);
        return a;

    }


}
