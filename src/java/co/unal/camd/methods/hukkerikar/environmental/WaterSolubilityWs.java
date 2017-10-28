package co.unal.camd.methods.hukkerikar.environmental;

import co.unal.camd.methods.hukkerikar.HukkerikarEnvironmentalProperties;
import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;

import java.util.Map;

/**
 * In Log(mg/lit)
 */
public class WaterSolubilityWs {

    private static final double A_Ws = 4.5484;
    private static final double B_Ws = 0.3411;

    public static double compute(HukkerikarEnvironmentalProperties environmentalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : environmentalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getEnvironmental().getWaterLogWS() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getEnvironmental().getWaterLogWS();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : environmentalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getEnvironmental().getWaterLogWS() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getEnvironmental().getWaterLogWS();

        return contributionsSum + A_Ws + B_Ws * environmentalProperties.getMolecularWeight();
    }

}
