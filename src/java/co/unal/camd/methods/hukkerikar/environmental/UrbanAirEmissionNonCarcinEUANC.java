package co.unal.camd.methods.hukkerikar.environmental;

import co.unal.camd.methods.hukkerikar.HukkerikarEnvironmentalProperties;
import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;

import java.util.Map;

public class UrbanAirEmissionNonCarcinEUANC {

    private static final double A_EUANC = 6.8181;

    public static double compute(HukkerikarEnvironmentalProperties environmentalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : environmentalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getEnvironmental().getAirEUAnc() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getEnvironmental().getAirEUAnc();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : environmentalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getEnvironmental().getAirEUAnc() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getEnvironmental().getAirEUAnc();

        return Math.pow(10, -A_EUANC - contributionsSum);
    }

}
