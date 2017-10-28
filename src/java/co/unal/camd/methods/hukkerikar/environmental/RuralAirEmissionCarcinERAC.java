package co.unal.camd.methods.hukkerikar.environmental;

import co.unal.camd.methods.hukkerikar.HukkerikarEnvironmentalProperties;
import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;

import java.util.Map;

public class RuralAirEmissionCarcinERAC {

    private static final double A_ERAC = 6.5561;

    public static double compute(HukkerikarEnvironmentalProperties environmentalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : environmentalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getEnvironmental().getAirERAc() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getEnvironmental().getAirERAc();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : environmentalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getEnvironmental().getAirERAc() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getEnvironmental().getAirERAc();

        return Math.pow(10, -A_ERAC - contributionsSum);
    }

}
