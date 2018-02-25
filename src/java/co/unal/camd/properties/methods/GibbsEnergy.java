package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.parameters.unifac.ThermoPhysicalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.ThermoPhysicalSecondOrderContribution;

import java.util.Map;

public class GibbsEnergy {

    public static final double compute(Molecule molecule) {
        double sum = -14.828;

        for (Map.Entry<ThermoPhysicalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            sum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getGibbsFreeEnergy();

        for (Map.Entry<ThermoPhysicalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getSecondOrderContributions().entrySet())
            sum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getGibbsEnergy();
        return sum;
    }

}

