package co.unal.camd.methods.gani.thermophysical;

import co.unal.camd.methods.gani.GaniThermoPhysicalProperties;
import co.unal.camd.methods.gani.GaniThermoPhysicalFirstOrderContribution;
import co.unal.camd.methods.gani.GaniThermoPhysicalSecondOrderContribution;

import java.util.Map;

/**
 * Gani, 1994
 *
 */
public class BoilingPoint {

    public static double compute(GaniThermoPhysicalProperties thermoPhysicalProperties) {
        double sum = 0;

        for (Map.Entry<GaniThermoPhysicalFirstOrderContribution, Integer> firstOrderContributionEntry : thermoPhysicalProperties.getFirstOrderContributions().entrySet())
            sum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getBoilingPoint();

        for (Map.Entry<GaniThermoPhysicalSecondOrderContribution, Integer> secondOrderContributionEntry : thermoPhysicalProperties.getSecondOrderContributions().entrySet())
            sum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getBoilingPoint();

        return 204.359 * Math.log(sum);
    }

}
