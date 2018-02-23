package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.parameters.unifac.ThermodynamicFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.ThermodynamicSecondOrderContribution;

import java.util.Map;

public class MeltingPoint {

    public static final double compute(Molecule molecule) {
        double sum = 0;

        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            sum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getMeltingPoint();

        for (Map.Entry<ThermodynamicSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getSecondOrderContributions().entrySet())
            sum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getMeltingPoint();

        return 102.425 * Math.log10(sum) * 2.30258509;
    }

}
