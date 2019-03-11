package co.unal.camd.properties.methods;

import co.unal.camd.properties.groups.unifac.ContributionGroup;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.groups.contributions.ThermoPhysicalSecondOrderContribution;

import java.util.Map;

public class BoilingPoint {

    public static final double compute(Molecule molecule) {
        double sum = 0;

        for (Map.Entry<ContributionGroup, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            sum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getThermoPhysicalFirstContribution().getBoilingPoint();

        for (Map.Entry<ThermoPhysicalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getSecondOrderContributions().entrySet())
            sum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getBoilingPoint();

        return 204.359 * Math.log10(sum) * 2.30258509;
    }

}