package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.properties.parameters.unifac.ThermodynamicFirstOrderContribution;

import java.util.Map;

public class MolecularWeight {

    public static double compute(MoleculeGroups molecule) {
        int sum = 0;
        for (int i = 0; i < molecule.size(); i++)
            sum += molecule.getAmount()[i] * molecule.getGroupContributions()[i].getMolecularWeight();
        return sum;
    }

    public static final double compute(Molecule molecule) {
        double sum = 0;
        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            sum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getMolecularWeight();
        return sum;
    }

}
