package co.unal.camd.properties.environmental;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.groups.contributions.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.groups.contributions.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class OralRatLD50 {

    private static final double A_LD50 = 1.9372;
    private static final double B_LD50 = 0.0016;

    public static final double compute(Molecule molecule) {
        double contributionsSum = 0;

        for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getOralLD50() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getOralLD50();

        for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getOralLD50() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getOralLD50();

        return Math.exp(A_LD50 - B_LD50 * molecule.getThermoPhysicalProperties().getMolecularWeight() - contributionsSum);
    }

}
