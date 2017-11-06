package co.unal.camd.properties.parameters;

import co.unal.camd.properties.parameters.unifac.ContributionGroup;
import co.unal.camd.properties.parameters.unifac.ContributionGroupData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;


/**
 * Finds the parameters used for the computing of chemical properties
 */
public final class ContributionGroupsManager2017 extends EstimationParameters {

    @Getter
    private String[][][] groupsData;
    @Getter
    private String[][][] ijParameters;

    private String[][][] secondOrderGroups;

    /**
     * Manager of the parameters used by the unifac based methods
     */
    public ContributionGroupsManager2017() {
        super();

        UnifacParameters unifacParameters = new UnifacParameters();
        groupsData = new String[8][50][50];
        ijParameters = unifacParameters.getIjParams();
        groupsData = unifacParameters.getGroupsData();
        secondOrderGroups = unifacParameters.getSecondOrderParameters();
    }

    /**
     * Given the code of contribution group, return the main group of it
     * (column 'Main Group' of sheet 'Main Group' in unifac worksheet)
     *
     * @param groupCode
     * @return
     */
    public final int getPrincipalGroupCode(int groupCode) {
        return contributionGroups.get(groupCode).getMainGroup().getCode();
    }

    /**
     * Find the name of a contribution group
     *
     * @param groupCode
     * @return
     */
    public final String findGroupName(int groupCode) {
        return contributionGroups.get(groupCode).getGroupName();
    }

    /**
     * Find the code of a contribution group given teh name
     *
     * @param name
     * @return
     */
    public final int findGroupCode(String name) {
        ContributionGroupData contributionGroup = contributionGroups.values().stream().filter(oneContributionGroup -> Objects.equals(name, oneContributionGroup.getGroupName())).findFirst().get();
        return contributionGroup.getCode();
    }

    //TODO HANDLE WITH AROMATICS AND STUFF
    public final double getProbability(int contributionGroupCode) {
        ContributionGroup.Main mainGroup = contributionGroups.get(contributionGroupCode).getMainGroup();
        Optional<ContributionGroup.Family> family = familyGroups.stream().filter(oneFamily -> oneFamily.getMainGroups().stream().anyMatch(main -> main.equals(mainGroup))).findFirst();
        return family.map(ContributionGroup.Family::getProbability).orElse(0.0);
    }

    public ArrayList<String[]> getSecondOrderGroupCase(double root) {
        ArrayList<String[]> caseNum = new ArrayList<>();
        double n = 0;
        int i = 0;
        while (n <= root) {
            if (n == root)
                caseNum.add(secondOrderGroups[0][i]);
            i++;
            n = Double.parseDouble(secondOrderGroups[0][i][1]);
        }
        return caseNum;
    }
}
