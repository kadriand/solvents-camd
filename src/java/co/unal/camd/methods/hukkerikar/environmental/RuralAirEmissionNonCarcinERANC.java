package co.unal.camd.methods.hukkerikar.environmental;

import co.unal.camd.methods.hukkerikar.HukkerikarEnvironmentalProperties;
import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;

import java.util.Map;

public class RuralAirEmissionNonCarcinERANC {

    private static final double A_ERANC = 7.5541;

    public static double compute(HukkerikarEnvironmentalProperties environmentalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : environmentalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getEnvironmental().getAirERAnc() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getEnvironmental().getAirERAnc();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : environmentalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getEnvironmental().getAirERAnc() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getEnvironmental().getAirERAnc();

        return Math.pow(10, -A_ERANC - contributionsSum);
    }

}
