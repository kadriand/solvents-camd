package co.unal.camd.methods.hukkerikar.thermophysical;

import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarThermoPhysicalProperties;

import java.util.Map;

/**
 * In kJ/mol
 */
public class GibbsEnergy {

    private static final double Gf_0 = -1.3385;

    public static double compute(HukkerikarThermoPhysicalProperties thermoPhysicalProperties) {
        double contributionsSum = 0;

        for (Map.Entry<HukkerikarFirstOrderContribution, Integer> firstOrderContributionEntry : thermoPhysicalProperties.getFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getThermoPhysical().getGibbsFreeEnergy() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getThermoPhysical().getGibbsFreeEnergy();

        for (Map.Entry<HukkerikarSecondOrderContribution, Integer> secondOrderContributionEntry : thermoPhysicalProperties.getSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getThermoPhysical().getGibbsFreeEnergy() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getThermoPhysical().getGibbsFreeEnergy();

        return contributionsSum + Gf_0;
    }

}

