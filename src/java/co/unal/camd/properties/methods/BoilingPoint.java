package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class BoilingPoint {

    private MoleculeGroups molecule;
    private ArrayList<Integer> secondOrderCodes;

    public BoilingPoint(Molecule solvent, ArrayList<Integer> secOrderCode) {
        secondOrderCodes = secOrderCode;
        molecule = solvent.getGroupsArray();
        molecule.optimize();
    }

    public double getMethodResult() {
        double sum = 0;
        for (int i = 0; i < molecule.size(); i++)
            sum += molecule.getAmount()[i] * molecule.getGroupContributions()[i].getBoilingPoint();
        sum += calculateSecOrderContribution();
        return 204.359 * Math.log10(sum) * 2.30258509;
    }

    private double calculateSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCodes.size(); i++) {
            int so = secondOrderCodes.get(i);
            a += CamdRunner.CONTRIBUTION_GROUPS.getSecondOrderGroupsContributions().get(so).getBoilingPoint();
        }
        return a;

    }


}
