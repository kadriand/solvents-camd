package co.unal.camd.methods.hukkerikar.environmental;

import co.unal.camd.methods.hukkerikar.HukkerikarEnvironmentalProperties;
import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;

import java.util.Map;

/**
 * In Log(mol/lit)
 */
public class DaphniaMagnaLC50DM {

    private static final double DM_0 = 2.9717;

    public static double compute(HukkerikarEnvironmentalProperties environmentalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : environmentalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getEnvironmental().getWaterLC50DM() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getEnvironmental().getWaterLC50DM();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : environmentalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getEnvironmental().getWaterLC50DM() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getEnvironmental().getWaterLC50DM();

        return -contributionsSum - DM_0;
    }

}
