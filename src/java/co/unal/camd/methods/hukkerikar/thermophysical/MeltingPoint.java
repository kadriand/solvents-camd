package co.unal.camd.methods.hukkerikar.thermophysical;

import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarThermoPhysicalProperties;

import java.util.Map;

/**
 * In K
 */
public class MeltingPoint {

    private static final double Tm_0 = 143.5706;

    public static double compute(HukkerikarThermoPhysicalProperties thermoPhysicalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : thermoPhysicalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getThermoPhysical().getMeltingPoint() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getThermoPhysical().getMeltingPoint();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : thermoPhysicalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getThermoPhysical().getMeltingPoint() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getThermoPhysical().getMeltingPoint();

        return Tm_0 * Math.log(contributionsSum);
    }

}

