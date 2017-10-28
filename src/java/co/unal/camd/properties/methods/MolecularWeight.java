package co.unal.camd.properties.methods;

import co.unal.camd.properties.groups.unifac.ContributionGroup;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;

import java.util.Map;

public class MolecularWeight {

    public static double compute(MoleculeGroups molecule) {
        int sum = 0;
        for (int i = 0; i < molecule.size(); i++)
            sum += molecule.getAmount()[i] * molecule.getGroupContributions()[i].getThermoPhysicalFirstContribution().getMolecularWeight();
        return sum;
    }

    public static final double compute(Molecule molecule) {
        double sum = 0;
        for (Map.Entry<ContributionGroup, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            sum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getThermoPhysicalFirstContribution().getMolecularWeight();
        return sum;
    }

}
