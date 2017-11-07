package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.properties.parameters.unifac.ContributionGroupData;

public class Density {
    private Molecule molecule;
    private double temperature;

    public Density(Molecule solvent, double temp) {
        temperature = temp;
        molecule = solvent;
    }

    public double getMethodResult() {
        double sum = 0;
        double a, b, c;

        for (int i = 0; i < molecule.getGroupsArray().size(); i++) {
            /**
             * this are 6 exceptions to create density groups between unifac groups
             */
            if (isBond(molecule.getGroupAt(i), 10, 4)) {
                a = 39.37;
                b = -0.2721;
                c = 0.0002492;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(molecule.getGroupAt(i), 2, 14)) {
                a = 36.73;
                b = -0.07125;
                c = 0.0001406;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(molecule.getGroupAt(i), 3, 81)) {
                a = 14.26;
                b = -0.008187;
                c = 0;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(molecule.getGroupAt(i), 4, 82)) {
                a = -95.68;
                b = 0.5935;
                c = -0.0009479;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(molecule.getGroupAt(i), 3, 77)) {
                a = 38.23;
                b = -0.1121;
                c = 0.0001665;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(molecule.getGroupAt(i), 10, 77)) {
                a = 27.61;
                b = -0.02077;
                c = 0;
                sum += (a + b * temperature + c * temperature * temperature);
            } else {
                ContributionGroupData groupContribution = molecule.getGroupsArray().getGroupContributions()[i];
                for (int j = 0; j < 4; j++) {
                    a = groupContribution.getDensityA()[j];
                    b = groupContribution.getDensityB()[j];
                    c = groupContribution.getDensityC()[j];
                    sum += (a + b * temperature + c * temperature * temperature);
                }
            }
        }
        MoleculeGroups gr = molecule.getGroupsArray();
        gr.optimize();
        return MolecularWeight.compute(gr) / sum;
    }

    private static boolean isBond(ContributionGroupNode aGroup, int rootGroup, int leafGroup) {
        if (aGroup.getGroupCode() == rootGroup && aGroup.getGroupAt(0) != null)
            for (int i = 0; i < aGroup.countSubgroups(); i++)
                if (aGroup.getGroupAt(i).getGroupCode() == leafGroup)
                    return true;
        return false;
    }

}
