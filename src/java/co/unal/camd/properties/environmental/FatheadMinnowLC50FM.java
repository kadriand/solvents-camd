package co.unal.camd.properties.environmental;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.groups.contributions.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.groups.contributions.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class FatheadMinnowLC50FM {

    private static final double FM_0 = 2.1949;

    public static final double compute(Molecule molecule) {
        double contributionsSum = 0;

        for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getWaterLC50FM() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getWaterLC50FM();

        for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getWaterLC50FM() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getWaterLC50FM();

        return Math.exp(FM_0 - contributionsSum);
    }

}
