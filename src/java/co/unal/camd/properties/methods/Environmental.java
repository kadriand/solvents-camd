package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.parameters.unifac.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class Environmental {
    private static final double FM_0 = 2.1949;
    private static final double DM_0 = 2.9717;
    private static final double A_LD50 = 1.9372;
    private static final double B_LD50 = 0.0016;
    private static final double A_EUAC = 5.2801;
    private static final double A_EUANC = 6.8181;
    private static final double A_ERAC = 6.5561;
    private static final double A_ERANC = 7.5541;
    private static final double A_EFWC = 5.6726;
    private static final double A_EFWNC = 6.4429;
    private static final double A_ESWC = 8.3962;
    private static final double A_ESWNC = 8.6360;
    private static final double A_ENSC = 6.4837;
    private static final double A_ENSNC = 7.0265;
    private static final double A_EASC = 6.2913;
    private static final double A_EASNC = 6.9723;

    public static class BioConcentrationFactor {

        private static final double A_LogWs = 4.5484;
        private static final double B_LogWs = 0.3411;

        public static final double compute(Molecule molecule) {
            double contributionsSum = 0;

            for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getWaterLogWS();

            for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getWaterLogWS();

            return A_LogWs + B_LogWs * molecule.getThermoPhysicalProperties().getMolecularWeight() + contributionsSum;
        }

    }

}
