package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionParametersManager;

import java.util.ArrayList;

public class BoilingTemp {

    private double sum = 0;
    private ContributionParametersManager aGC;
    private GroupArray aMolecule;
    private ArrayList<Integer> secondOrderCode;

    public BoilingTemp(Molecules solvent, ArrayList<Integer> secOrderCode, ContributionParametersManager aGC) {
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
