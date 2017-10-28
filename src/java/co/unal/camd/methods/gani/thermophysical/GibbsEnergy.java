package co.unal.camd.methods.gani.thermophysical;

import co.unal.camd.methods.gani.GaniThermoPhysicalFirstOrderContribution;
import co.unal.camd.methods.gani.GaniThermoPhysicalSecondOrderContribution;
import co.unal.camd.methods.gani.GaniThermoPhysicalProperties;

import java.util.Map;

public class GibbsEnergy {

    public static double compute(GaniThermoPhysicalProperties thermoPhysicalProperties) {
        double sum = -14.828;

        for (Map.Entry<GaniThermoPhysicalFirstOrderContribution, Integer> firstOrderContributionEntry : thermoPhysicalProperties.getFirstOrderContributions().entrySet())
            sum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getGibbsFreeEnergy();

        for (Map.Entry<GaniThermoPhysicalSecondOrderContribution, Integer> secondOrderContributionEntry : thermoPhysicalProperties.getSecondOrderContributions().entrySet())
            sum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getGibbsEnergy();
        return sum;
    }

}

