package co.unal.camd.methods.hukkerikar.thermophysical;

import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarThermoPhysicalProperties;

import java.util.Map;

/**
 * In cc/kmol
 */
public class LiquidMolarVolume {

    private static final double Vm_0 = 0.0160;

    public static double compute(HukkerikarThermoPhysicalProperties thermoPhysicalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : thermoPhysicalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getThermoPhysical().getLiquidMolarVolume() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getThermoPhysical().getLiquidMolarVolume();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : thermoPhysicalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getThermoPhysical().getLiquidMolarVolume() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getThermoPhysical().getLiquidMolarVolume();

        return contributionsSum + Vm_0;
    }

}

