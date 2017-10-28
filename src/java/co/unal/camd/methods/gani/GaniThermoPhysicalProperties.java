package co.unal.camd.methods.gani;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.gani.thermophysical.BoilingPoint;
import co.unal.camd.methods.gani.thermophysical.Density;
import co.unal.camd.methods.gani.thermophysical.GibbsEnergy;
import co.unal.camd.methods.gani.thermophysical.MeltingPoint;
import co.unal.camd.model.ThermoPhysicalProperties;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Accessors(chain = true)
public class GaniThermoPhysicalProperties extends ThermoPhysicalProperties {

    private Molecule molecule;

    @Getter
    private Map<GaniThermoPhysicalFirstOrderContribution, Integer> firstOrderContributions;

    @Getter
    private Map<GaniThermoPhysicalSecondOrderContribution, Integer> secondOrderContributions;

    public GaniThermoPhysicalProperties(Molecule molecule) {
        UnifacGroupNode rootContributionGroup = molecule.readRootContributionGroup();
        this.firstOrderContributions = this.findFirstOrderGroups(rootContributionGroup);
        this.secondOrderContributions = this.findSecondOrderGroups(rootContributionGroup);
        this.molecule = molecule;
        this.groupsSummary = resumeGroups();
    }

    public void compute() {
        this.gibbsEnergy = GibbsEnergy.compute(this);
        this.boilingPoint = BoilingPoint.compute(this);
        this.meltingPoint = MeltingPoint.compute(this);
        this.density = Density.compute(molecule);
    }

    private Map<GaniThermoPhysicalFirstOrderContribution, Integer> findFirstOrderGroups(UnifacGroupNode rootContributionGroup) {
        Map<GaniThermoPhysicalFirstOrderContribution, Integer> firstOrderContribution = new HashMap<>();
        findFirstOrderGroups(rootContributionGroup, firstOrderContribution);
        return firstOrderContribution;
    }

    private void findFirstOrderGroups(UnifacGroupNode contributionGroup, Map<GaniThermoPhysicalFirstOrderContribution, Integer> firstOrderContributions) {
        GaniThermoPhysicalFirstOrderContribution firstOrderContribution = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(contributionGroup.getGroupCode());
        if (firstOrderContributions.containsKey(firstOrderContribution))
            firstOrderContributions.replace(firstOrderContribution, firstOrderContributions.get(firstOrderContribution) + 1);
        else
            firstOrderContributions.put(firstOrderContribution, 1);

        for (UnifacGroupNode subGroup : contributionGroup.getSubGroups())
            findFirstOrderGroups(subGroup, firstOrderContributions);
    }

    private Map<GaniThermoPhysicalSecondOrderContribution, Integer> findSecondOrderGroups(UnifacGroupNode rootContributionGroup) {
        Map<GaniThermoPhysicalSecondOrderContribution, List<List<UnifacGroupNode>>> secondOrderContributionsLists = new HashMap<>();
        findSecondOrderGroups(rootContributionGroup, secondOrderContributionsLists);
        if (ProblemParameters.EXCLUDE_NESTED_GROUPS)
            return removeNestedAndCountSecondOrderGroups(secondOrderContributionsLists);
        Map<GaniThermoPhysicalSecondOrderContribution, Integer> secondOrderContributions = new HashMap<>();
        secondOrderContributionsLists.forEach((thermoPhysicalSecondOrderContribution, lists) -> secondOrderContributions.put(thermoPhysicalSecondOrderContribution, lists.size()));
        return secondOrderContributions;
    }


    private Map<GaniThermoPhysicalSecondOrderContribution, Integer> removeNestedAndCountSecondOrderGroups(Map<GaniThermoPhysicalSecondOrderContribution, List<List<UnifacGroupNode>>> secondOrderContributionsLists) {
        Map<GaniThermoPhysicalSecondOrderContribution, Integer> secondOrderContributions = new HashMap<>();
        secondOrderContributionsLists.forEach((contributionsGroup, contributionsLists) -> {
            Long occurrences = contributionsLists.stream()
                    .filter(contributionGroups -> !groupsInSecondOrderContributionsGroupsLists(contributionGroups, contributionsGroup, secondOrderContributionsLists))
                    .count();
            if (occurrences > 0)
                secondOrderContributions.put(contributionsGroup, occurrences.intValue());
        });

        return secondOrderContributions;
    }

    private boolean groupsInSecondOrderContributionsGroupsLists(List<UnifacGroupNode> contributionGroups, GaniThermoPhysicalSecondOrderContribution contributionsGroup, Map<GaniThermoPhysicalSecondOrderContribution, List<List<UnifacGroupNode>>> secondOrderContributionsLists) {
        return secondOrderContributionsLists.entrySet().stream()
                .anyMatch(contributionsEntry -> contributionsEntry.getKey() != contributionsGroup
                        && contributionsEntry.getValue().stream()
                        .anyMatch(evaluationGroups -> evaluationGroups.containsAll(contributionGroups)
                        ));
    }

    private void findSecondOrderGroups(UnifacGroupNode contributionGroup, Map<GaniThermoPhysicalSecondOrderContribution, List<List<UnifacGroupNode>>> secondOrderContributionsLists) {
        Map<UnifacGroupNode, GaniThermoPhysicalSecondOrderContribution> branchSecondOrderContributions =
                CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(contributionGroup.getGroupCode()).getSecondOrderContributions();

        branchSecondOrderContributions.forEach((contributionGroupNode, secondOrderContribution) -> {
            if (secondOrderContributionsLists.containsKey(secondOrderContribution))
                return;
            List<List<UnifacGroupNode>> occurrences = contributionGroup.contains(contributionGroupNode);
            if (!occurrences.isEmpty())
                secondOrderContributionsLists.put(secondOrderContribution, occurrences);
        });

        for (UnifacGroupNode subGroup : contributionGroup.getSubGroups())
            findSecondOrderGroups(subGroup, secondOrderContributionsLists);
    }

    private String resumeGroups() {
        StringBuilder result = new StringBuilder();
        this.firstOrderContributions.forEach((contributionGroup, occurrences) -> result.append(String.format("%sx%s ", contributionGroup.getCode(), occurrences)));
        if (this.secondOrderContributions.size() > 0) {
            result.append("[");
            this.secondOrderContributions.forEach((contributionGroup, occurrences) -> result.append(String.format("%sx%s ", contributionGroup.getCode(), occurrences)));
            result.append("]");
        }
        return result.toString().replaceAll("x1|\\s$", "").replaceFirst("\\s]", "]");
    }

}
