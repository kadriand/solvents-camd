package co.unal.camd.properties.environmental;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.parameters.unifac.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class RuralAirEmissionCarcinERAC {

    private static final double A_ERAC = 6.5561;

    public static final double compute(Molecule molecule) {
        double contributionsSum = 0;

        for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getAirERAc() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getAirERAc();

        for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getAirERAc() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getAirERAc();

        return Math.exp(A_ERAC - contributionsSum);
    }

}
