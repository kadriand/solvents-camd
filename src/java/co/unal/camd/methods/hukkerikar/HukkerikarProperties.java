package co.unal.camd.methods.hukkerikar;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.view.CamdRunner;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Accessors(chain = true)
public class HukkerikarProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(HukkerikarProperties.class);

    @Getter
    private Map<HukkerikarFirstOrderContribution, Integer> environmentalFirstOrderContributions;

    @Getter
    private Map<HukkerikarSecondOrderContribution, Integer> environmentalSecondOrderContributions;

    static Map<HukkerikarFirstOrderContribution, Integer> findFirstOrderGroups(UnifacGroupNode rootContributionGroup) {
        Map<HukkerikarFirstOrderContribution, Integer> firstOrderContribution = new HashMap<>();
        findFirstOrderGroups(rootContributionGroup, firstOrderContribution);
        return firstOrderContribution;
    }

    static void findFirstOrderGroups(UnifacGroupNode contributionGroup, Map<HukkerikarFirstOrderContribution, Integer> firstOrderContributions) {
        Integer hukkerikarCode = CamdRunner.CONTRIBUTION_GROUPS.getUnifacHukkerikarGroupsEquivalences().get(contributionGroup.getGroupCode());
        HukkerikarFirstOrderContribution firstOrderContribution = CamdRunner.CONTRIBUTION_GROUPS.getHukkerikarFirstOrderContributions().get(hukkerikarCode);
        if (firstOrderContributions.containsKey(firstOrderContribution))
            firstOrderContributions.replace(firstOrderContribution, firstOrderContributions.get(firstOrderContribution) + 1);
        else
            firstOrderContributions.put(firstOrderContribution, 1);

        for (UnifacGroupNode subGroup : contributionGroup.getSubGroups())
            findFirstOrderGroups(subGroup, firstOrderContributions);
    }

    static Map<HukkerikarSecondOrderContribution, Integer> findSecondOrderGroups(UnifacGroupNode contributionGroup) {
        Map<HukkerikarSecondOrderContribution, List<List<UnifacGroupNode>>> secondOrderContributionsLists = new HashMap<>();
        findSecondOrderGroups(contributionGroup, secondOrderContributionsLists);

        if (ProblemParameters.EXCLUDE_NESTED_GROUPS)
            return removeNestedAndCountSecondOrderGroups(secondOrderContributionsLists);
        Map<HukkerikarSecondOrderContribution, Integer> secondOrderContributions = new HashMap<>();
        secondOrderContributionsLists.forEach((environmentalSecondOrderContribution, lists) -> secondOrderContributions.put(environmentalSecondOrderContribution, lists.size()));
        return secondOrderContributions;
    }

    static void findSecondOrderGroups(UnifacGroupNode contributionGroup, Map<HukkerikarSecondOrderContribution, List<List<UnifacGroupNode>>> secondOrderContributionsLists) {
        try {
            Integer hukkerikarCode = CamdRunner.CONTRIBUTION_GROUPS.getUnifacHukkerikarGroupsEquivalences().get(contributionGroup.getGroupCode());
            Map<UnifacGroupNode, HukkerikarSecondOrderContribution> branchSecondOrderContributions = CamdRunner.CONTRIBUTION_GROUPS.getHukkerikarFirstOrderContributions().get(hukkerikarCode).getSecondOrderContributions();

            branchSecondOrderContributions.forEach((contributionGroupNode, secondOrderContribution) -> {
                if (secondOrderContributionsLists.containsKey(secondOrderContribution))
                    return;
                List<List<UnifacGroupNode>> occurrences = contributionGroup.contains(contributionGroupNode);
                if (!occurrences.isEmpty())
                    secondOrderContributionsLists.put(secondOrderContribution, occurrences);
            });

            for (UnifacGroupNode subGroup : contributionGroup.getSubGroups())
                findSecondOrderGroups(subGroup, secondOrderContributionsLists);
        } catch (Exception e) {
            LOGGER.error("Errors with node {}", contributionGroup);
            throw e;
        }
    }

    static Map<HukkerikarSecondOrderContribution, Integer> removeNestedAndCountSecondOrderGroups(Map<HukkerikarSecondOrderContribution, List<List<UnifacGroupNode>>> secondOrderContributionsLists) {
        Map<HukkerikarSecondOrderContribution, Integer> secondOrderContributions = new HashMap<>();
        secondOrderContributionsLists.forEach((contributionsGroup, contributionsLists) -> {
            Long occurrences = contributionsLists.stream()
                    .filter(contributionGroups -> !groupsInEnvironmentalSecondOrderContributionsGroupsLists(contributionGroups, contributionsGroup, secondOrderContributionsLists))
                    .count();
            if (occurrences > 0)
                secondOrderContributions.put(contributionsGroup, occurrences.intValue());
        });

        return secondOrderContributions;
    }

    static boolean groupsInEnvironmentalSecondOrderContributionsGroupsLists(List<UnifacGroupNode> contributionGroups, HukkerikarSecondOrderContribution contributionsGroup, Map<HukkerikarSecondOrderContribution, List<List<UnifacGroupNode>>> secondOrderContributionsLists) {
        return secondOrderContributionsLists.entrySet().stream()
                .anyMatch(contributionsEntry -> contributionsEntry.getKey() != contributionsGroup
                        && contributionsEntry.getValue().stream()
                        .anyMatch(evaluationGroups -> evaluationGroups.containsAll(contributionGroups)
                        ));
    }


}
