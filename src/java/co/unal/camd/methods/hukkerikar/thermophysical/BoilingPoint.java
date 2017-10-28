package co.unal.camd.methods.hukkerikar.thermophysical;

import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarThermoPhysicalProperties;

import java.util.Map;

/**
 * In K
 */
public class BoilingPoint {

    private static final double Tb_0 = 244.5165;

    public static double compute(HukkerikarThermoPhysicalProperties thermoPhysicalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : thermoPhysicalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getThermoPhysical().getBoilingPoint() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getThermoPhysical().getBoilingPoint();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : thermoPhysicalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getThermoPhysical().getBoilingPoint() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getThermoPhysical().getBoilingPoint();

        return Tb_0 * Math.log(contributionsSum);
    }

}

