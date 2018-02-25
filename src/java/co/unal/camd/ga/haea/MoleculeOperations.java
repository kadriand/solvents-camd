package co.unal.camd.ga.haea;

import co.unal.camd.properties.parameters.unifac.ThermoPhysicalFirstOrderContribution;
import co.unal.camd.view.CamdRunner;

import java.util.List;

public final class MoleculeOperations {

    /**
     * Retuns a random contribution group code
     *
     * @param valence
     * @param functional
     * @return
     */
    public static int getNewGroupCode(int valence, boolean functional) {
        List<ThermoPhysicalFirstOrderContribution> valenceContributionGroups = CamdRunner.CONTRIBUTION_GROUPS.getValenceContributionGroups().get(valence);
        int groupIndex = 0;
        if (functional) {
            double requestProbability = Math.random();
            double groupProbaility = 0;
            while (requestProbability <= 1 - groupProbaility) {
                groupIndex = (int) (Math.random() * valenceContributionGroups.size());//random row to choose the group
                //TODO update to a new way
                ThermoPhysicalFirstOrderContribution contributionGroup = valenceContributionGroups.get(groupIndex);
                groupProbaility = CamdRunner.CONTRIBUTION_GROUPS.getProbability(contributionGroup.getCode());
            }
        }
        return valenceContributionGroups.get(groupIndex).getCode();
    }
}
