package co.unal.camd.methods.gani.thermophysical;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.gani.GaniThermoPhysicalFirstOrderContribution;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;

public class Density {

    public static double compute(Molecule molecule) {
        double temperature = ProblemParameters.getTemperature();
        double sum = 0;
        double a, b, c;

        for (UnifacGroupNode contributionGroup : molecule.pickAllGroups()) {
            /**
             * this are 6 exceptions to create density groups between unifac groups
             */
            if (isBond(contributionGroup, 10, 4)) {
                a = 39.37;
                b = -0.2721;
                c = 0.0002492;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(contributionGroup, 2, 14)) {
                a = 36.73;
                b = -0.07125;
                c = 0.0001406;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(contributionGroup, 3, 81)) {
                a = 14.26;
                b = -0.008187;
                c = 0;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(contributionGroup, 4, 82)) {
                a = -95.68;
                b = 0.5935;
                c = -0.0009479;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(contributionGroup, 3, 77)) {
                a = 38.23;
                b = -0.1121;
                c = 0.0001665;
                sum += (a + b * temperature + c * temperature * temperature);
            } else if (isBond(contributionGroup, 10, 77)) {
                a = 27.61;
                b = -0.02077;
                c = 0;
                sum += (a + b * temperature + c * temperature * temperature);
            } else {
                GaniThermoPhysicalFirstOrderContribution firstOrderContribution = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(contributionGroup.getGroupCode());
                for (int j = 0; j < 4; j++) {
                    a = firstOrderContribution.getDensityA()[j];
                    b = firstOrderContribution.getDensityB()[j];
                    c = firstOrderContribution.getDensityC()[j];
                    sum += (a + b * temperature + c * temperature * temperature);
                }
            }
        }
        return molecule.getMolecularWeight() / sum;
    }

    private static boolean isBond(UnifacGroupNode group, int branchGroup, int leafGroup) {
        return group.getGroupCode() == branchGroup && group.getSubGroups().stream().anyMatch(subGroup -> subGroup.getGroupCode() == leafGroup);
    }

}
