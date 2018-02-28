package co.unal.camd.properties.environmental;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.parameters.unifac.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class UrbanAirEmissionNonCarcinEUANC {

    private static final double A_EUANC = 6.8181;

    public static final double compute(Molecule molecule) {
        double contributionsSum = 0;

        for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getAirEUAnc() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getAirEUAnc();

        for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getAirEUAnc() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getAirEUAnc();

        return Math.exp(A_EUANC - contributionsSum);
    }

}
