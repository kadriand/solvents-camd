package co.unal.camd.properties.environmental;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.parameters.unifac.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class RuralAirEmissionNonCarcinERANC {

    private static final double A_ERANC = 7.5541;

    public static final double compute(Molecule molecule) {
        double contributionsSum = 0;

        for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getAirERAnc() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getAirERAnc();

        for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getAirERAnc() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getAirERAnc();

        return Math.exp(A_ERANC - contributionsSum);
    }

}
