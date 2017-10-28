package co.unal.camd.properties.model;

import co.unal.camd.properties.groups.unifac.ContributionGroup;
import co.unal.camd.view.CamdRunner;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(exclude = {"parentGroup", "contributionGroupDetails"})
@ToString(exclude = {"parentGroup", "contributionGroupDetails"})
public class ContributionGroupNode {

    /**
     * Contribution group code according to the UNIFAC naming
     * {@link <a href="http://unifac.ddbst.de/PublishedParametersUNIFACDO.html#ListOfSubGroupsAndTheirGroupSurfacesAndVolumes">'UNIFAC Published parameters'</a>}
     */
    @Setter(AccessLevel.NONE)
    private int groupCode;

    private ContributionGroup contributionGroupDetails;
    @Setter(AccessLevel.NONE)
    private ContributionsList<ContributionGroupNode> subGroups = new ContributionsList<>(this);

    private ContributionGroupNode parentGroup;

    public ContributionGroupNode(int groupCode) {
        this.groupCode = groupCode;
        this.contributionGroupDetails = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(this.groupCode);
    }

    public ContributionGroupNode(ContributionGroupNode originGroupNode) {
        this.groupCode = originGroupNode.getGroupCode();
        this.contributionGroupDetails = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(this.groupCode);

        originGroupNode.subGroups.forEach(groupNode -> {
            ContributionGroupNode newGroupNode = groupNode.clone();
            this.subGroups.add(newGroupNode);
        });
    }

    public ContributionGroupNode(ContributionGroup contributionGroup) {
        this.groupCode = contributionGroup.getCode();
        this.contributionGroupDetails = contributionGroup;
    }

    public ContributionGroupNode setGroupCode(int groupCode) {
        this.groupCode = groupCode;
        this.contributionGroupDetails = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(this.groupCode);
        return this;
    }

    @Override
    public ContributionGroupNode clone() {
        return new ContributionGroupNode(this);
    }

    public int countSubgroupsDownStream() {
        int size = 1;
        for (ContributionGroupNode subGroup : subGroups)
            size += subGroup.countSubgroupsDownStream();
        return size;
    }

    public ContributionGroupNode getGroupAt(int i) {
        if (subGroups.size() < i + 1)
            return null;
        else
            return subGroups.get(i);
    }

    /**
     * Finds how many tiles a ContributionGroupNode contains other
     *
     * @param other node that may be contained
     * @return the number of occurrences found
     */
    public int contains(ContributionGroupNode other) {
        List<List<ContributionGroupNode>> containedGroupsList = contains(other, null);
        List<List<ContributionGroupNode>> cleanedContainedGroupsList = new ArrayList<>();

        for (List<ContributionGroupNode> containedGroups : containedGroupsList) {
            boolean isContainedInCleaned = false;
            for (List<ContributionGroupNode> cleanedContainedGroups : cleanedContainedGroupsList) {
                if (cleanedContainedGroups.size() != containedGroups.size())
                    continue;
                boolean cleanedInContained = !cleanedContainedGroups.stream().anyMatch(cleanedContainedGroup -> !containedGroups.stream().anyMatch(containedGroup -> containedGroup == cleanedContainedGroup));
                boolean containedInCleaned = !containedGroups.stream().anyMatch(containedGroup -> !cleanedContainedGroups.stream().anyMatch(cleanedContainedGroup -> containedGroup == cleanedContainedGroup));
                isContainedInCleaned = cleanedInContained && containedInCleaned;
            }
            if (!isContainedInCleaned)
                cleanedContainedGroupsList.add(containedGroups);
        }
        return cleanedContainedGroupsList.size();
    }

    /**
     * If s ContributionGroupNode is contained in another, return the occurrences. It may content repeated nodes
     *
     * @param other      node that may be contained
     * @param originNode to know the origin node, so it won't be taken into account during the iterations and the code won't stuck in an endless loop
     * @return
     */
    private List<List<ContributionGroupNode>> contains(ContributionGroupNode other, ContributionGroupNode originNode) {
        List<List<ContributionGroupNode>> occurrences = new ArrayList<>();

        if (this.groupCode == other.groupCode) {
            List<ContributionGroupNode> containedGroups = this.containsGroups(other, null);
            if (containedGroups.size() > 0)
                occurrences.add(containedGroups);
        }

        if (this.parentGroup != null && this.parentGroup != originNode)
            occurrences.addAll(this.parentGroup.contains(other, this));
        for (ContributionGroupNode subGroup : this.subGroups)
            if (subGroup != originNode)
                occurrences.addAll(subGroup.contains(other, this));
        return occurrences;
    }

    /**
     * Check if the subgroups of a ContributionGroupNode contains the subgroups of another
     *
     * @param other      ContributionGroupNode to compare
     * @param originNode to avoid recheck already checked group nodes
     * @return empty array the other GroupNode is not contained, else return the contained groups
     */
    private List<ContributionGroupNode> containsGroups(ContributionGroupNode other, ContributionGroupNode originNode) {
        List<ContributionGroupNode> branchSubGroups = this.bondedGroups(originNode);
        List<ContributionGroupNode> alreadyMatchedGroups = new ArrayList<>(Arrays.asList(this));

        for (ContributionGroupNode otherSubGroup : other.subGroups) {
            Optional<ContributionGroupNode> subGroupMatch = branchSubGroups.stream().filter(subGroup ->
                    subGroup.groupCode == otherSubGroup.groupCode
                            && !alreadyMatchedGroups.stream().anyMatch(alreadyMatchedGroup -> alreadyMatchedGroup == subGroup)
                            && subGroup.containsGroups(otherSubGroup, this).size() > 0
            ).findFirst();
            if (subGroupMatch.isPresent())
                alreadyMatchedGroups.add(subGroupMatch.get());
            else
                return new ArrayList<>();
        }
        return alreadyMatchedGroups;
    }

    /**
     * Collect all the leafs of a ContributionGroupNode group
     *
     * @return
     */
    public ArrayList<ContributionGroupNode> collectAllTreeGroups() {
        ArrayList<ContributionGroupNode> groupNodes = new ArrayList<>();
        this.collectBranchGroups(null, groupNodes);
        return groupNodes;
    }

    private void collectBranchGroups(ContributionGroupNode originGroup, ArrayList<ContributionGroupNode> groupNodes) {
        groupNodes.add(this);
        for (ContributionGroupNode group : this.bondedGroups(originGroup))
            group.collectBranchGroups(this, groupNodes);
    }

    public int countFunctionalElements() {
        ArrayList<ContributionGroupNode> allGroups = collectAllTreeGroups();
        int functionalElements = 0;
        for (ContributionGroupNode group : allGroups)
            functionalElements += group.getContributionGroupDetails().getFunctionalElementsNumber();
        return functionalElements;
    }

    public int countTotalGroups() {
        ArrayList<ContributionGroupNode> allGroups = collectAllTreeGroups();
        return allGroups.size();
    }

    public List<ContributionGroupNode> bondedGroups() {
        return this.bondedGroups(null);
    }

    private List<ContributionGroupNode> bondedGroups(ContributionGroupNode originGroupNode) {
        List<ContributionGroupNode> bondedGroups = new ArrayList<>();
        if (this.parentGroup != null && this.parentGroup != originGroupNode)
            bondedGroups.add(this.parentGroup);
        this.subGroups.stream()
                .filter(contributionNode -> contributionNode != null && contributionNode != originGroupNode)
                .forEach(bondedGroups::add);
        return bondedGroups;
    }

    public void reorderGroupTree() {
        if (this.getParentGroup() == null)
            return;
        ContributionGroupNode parentGroup = this.getParentGroup();
        parentGroup.reorderGroupTree();
        this.getSubGroups().add(parentGroup);
        parentGroup.getSubGroups().remove(this);
    }

    public String buildSmiles() {
        return buildSmiles(null);
    }

    private String buildSmiles(ContributionGroupNode originNode) {
        List<ContributionGroupNode> branchSubGroups = this.bondedGroups(originNode);
        String smiles = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(this.groupCode).getSmilesPattern();

        if (branchSubGroups.size() == 0)
            return smiles;

        for (ContributionGroupNode branchSubGroup : branchSubGroups)
            if (smiles.contains("."))
                smiles = smiles.replaceFirst("\\.", branchSubGroup.buildSmiles(this));
            else
                smiles += "(" + branchSubGroup.buildSmiles(this) + ")";
        return smiles;
    }

    @Override
    public boolean equals(Object object) {
        return this == object;
    }
}
