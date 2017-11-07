package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class MeltingPoint {

    private MoleculeGroups molecule;
    private ArrayList<Integer> secondOrderCodes;

    public MeltingPoint(Molecule molecule, ArrayList<Integer> secOrderCode) {
        secondOrderCodes = secOrderCode;
        this.molecule = molecule.getGroupsArray();
        this.molecule.optimize();
    }

    public double getMethodResult() {
        double sum = 0;
        for (int i = 0; i < molecule.size(); i++)
            sum += molecule.getAmount()[i] * molecule.getGroupContributions()[i].getMeltingPoint();

        sum += calculateSecOrderContribution();
        return 102.425 * Math.log10(sum) * 2.30258509;
    }

    private double calculateSecOrderContribution() {
        double a = 0;
        for (int i = 0; i < secondOrderCodes.size(); i++) {
            int code = secondOrderCodes.get(i);
            a += CamdRunner.CONTRIBUTION_GROUPS.getSecondOrderGroupsContributions().get(code).getMeltingPoint();
        }
        return a;
    }


}
