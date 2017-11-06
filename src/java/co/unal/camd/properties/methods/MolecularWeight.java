package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.MoleculeGroups;

public class MolecularWeight {

    public static double getMethodResult(MoleculeGroups molecule) {
        int sum = 0;
        for (int i = 0; i < molecule.size(); i++)
            sum += molecule.getAmount(i) * molecule.getGroupContributions()[i].getMolecularWeight();
        return sum;
    }

}
