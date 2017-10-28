package co.unal.camd.methods.gani.thermophysical;

import co.unal.camd.methods.gani.GaniThermoPhysicalFirstOrderContribution;
import co.unal.camd.methods.gani.GaniThermoPhysicalProperties;
import co.unal.camd.methods.gani.GaniThermoPhysicalSecondOrderContribution;

import java.util.Map;

/**
 * Gani, 1994
 */
public class MeltingPoint {

    public static double compute(GaniThermoPhysicalProperties molecule) {
        double sum = 0;

        for (Map.Entry<GaniThermoPhysicalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            sum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getMeltingPoint();

        for (Map.Entry<GaniThermoPhysicalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getSecondOrderContributions().entrySet())
            sum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getMeltingPoint();

        return 102.425 * Math.log(sum);
    }

}
