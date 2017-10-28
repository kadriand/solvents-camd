package co.unal.camd.properties.environmental;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.groups.contributions.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.groups.contributions.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class DaphniaMagnaLC50DM {

    private static final double DM_0 = 2.9717;

    public static final double compute(Molecule molecule) {
        double contributionsSum = 0;

        for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getWaterLC50DM() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getWaterLC50DM();

        for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getWaterLC50DM() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getWaterLC50DM();

        return Math.exp(DM_0 - contributionsSum);
    }

}
