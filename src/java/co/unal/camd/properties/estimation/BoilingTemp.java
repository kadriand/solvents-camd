package co.unal.camd.properties.estimation;

import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class BoilingTemp {

    private double sum = 0;
    private MoleculeGroups aMolecule;
    private ArrayList<Integer> secondOrderCodes;

    public BoilingTemp(Molecule solvent, ArrayList<Integer> secOrderCode) {
        secondOrderCodes = secOrderCode;
        aMolecule = solvent.getGroupsArray();
        aMolecule.optimize();
    }

    public double getMethodResult() {
        for (int i = 0; i < aMolecule.size(); i++) {
            double q = CamdRunner.CONTRIBUTION_GROUPS.getConstantPTeb(aMolecule.getGroupCode(i));
            sum += aMolecule.getAmount(i) * q;
        }
        sum = sum + calculateSecOrderContribution();
        return 204.359 * Math.log10(sum) * 2.30258509;
    }

    private double calculateSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCodes.size(); i++) {
            int so = secondOrderCodes.get(i);
            //System.out.println("so :"+so);
            a += CamdRunner.CONTRIBUTION_GROUPS.getBoilingTempSecondOrderParameter(so);

            //System.out.println("sum :"+sum);
        }
        //System.out.println("a :"+a);
        return a;

    }


}
