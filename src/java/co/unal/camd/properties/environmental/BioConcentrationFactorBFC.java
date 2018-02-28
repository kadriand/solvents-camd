package co.unal.camd.properties.environmental;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.parameters.unifac.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class BioConcentrationFactorBFC {

    public static final double compute(Molecule molecule) {
        double contributionsSum = 0;

        for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getWaterBFC() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getWaterBFC();

        for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getWaterBFC() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getWaterBFC();

        return Math.exp(contributionsSum);
    }

}
