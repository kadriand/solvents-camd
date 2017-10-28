package co.unal.camd.model.molecule;

import co.unal.camd.methods.unifac.UnifacSubGroup;
import co.unal.camd.view.CamdRunner;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
public class UnifacGroupNode {

    /**
     * Contribution group code according to the UNIFAC naming
     * {@link <a href="http://unifac.ddbst.de/PublishedParametersUNIFACDO.html#ListOfSubGroupsAndTheirGroupSurfacesAndVolumes">'UNIFAC Published parameters'</a>}
     */
    @Setter(AccessLevel.NONE)
    private int groupCode;

    private UnifacSubGroup unifacSubGroup;

    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    private UnifacGroupNodesList<UnifacGroupNode> subGroups = new UnifacGroupNodesList<>(this);

    @ToString.Exclude
    private UnifacGroupNode parentGroup;

    public UnifacGroupNode(int groupCode) {
        this.groupCode = groupCode;
        this.unifacSubGroup = CamdRunner.CONTRIBUTION_GROUPS.getUnifacContributions().get(this.groupCode);
    }

    public UnifacGroupNode(UnifacGroupNode originGroupNode) {
        this.groupCode = originGroupNode.getGroupCode();
        this.unifacSubGroup = CamdRunner.CONTRIBUTION_GROUPS.getUnifacContributions().get(this.groupCode);

        originGroupNode.subGroups.forEach(groupNode -> {
            UnifacGroupNode newGroupNode = groupNode.clone();
            this.subGroups.add(newGroupNode);
        });
    }

    public UnifacGroupNode(UnifacSubGroup unifacSubGroup) {
        this.groupCode = unifacSubGroup.getCode();
        this.unifacSubGroup = unifacSubGroup;
    }

    public UnifacGroupNode setGroupCode(int groupCode) {
        this.groupCode = groupCode;
        this.unifacSubGroup = CamdRunner.CONTRIBUTION_GROUPS.getUnifacContributions().get(this.groupCode);
        return this;
    }

    @Override
    public UnifacGroupNode clone() {
        return new UnifacGroupNode(this);
    }

    public int countSubgroupsDownStream() {
        int size = 1;
        for (UnifacGroupNode subGroup : subGroups)
            size += subGroup.countSubgroupsDownStream();
        return size;
    }

    /**
     * Finds how many times a UnifacGroupNode contains another
     *
     * @param other node that may be contained
     * @return a list with the occurrences found
     */
    public List<List<UnifacGroupNode>> contains(UnifacGroupNode other) {
        List<List<UnifacGroupNode>> matchingGroupsLists = this.containsAnywhere(other, null);
        List<List<UnifacGroupNode>> cleanedMatchingGroupsLists = new ArrayList<>();

        for (List<UnifacGroupNode> matchingGroups : matchingGroupsLists) {
            boolean cleanedInMatching = cleanedMatchingGroupsLists.stream()
                    .noneMatch(matchingGroups::containsAll);
            if (cleanedInMatching)
                cleanedMatchingGroupsLists.add(matchingGroups);
        }

        return cleanedMatchingGroupsLists;
    }

    /**
     * If a UnifacGroupNode is contained in another, return the occurrences. It may content repeated nodes.
     * The head of the @other can be in any node of the @origin UnifacGroupNode
     *
     * @param other  node that may be contained
     * @param origin to know the origin node, so it won't be taken into account during the iterations and the code won't stuck in an endless loop
     * @return the lists of groups involved in every match
     */
    private List<List<UnifacGroupNode>> containsAnywhere(UnifacGroupNode other, UnifacGroupNode origin) {
        List<List<UnifacGroupNode>> occurrences = new ArrayList<>();

        if (this.groupCode == other.groupCode) {
            List<UnifacGroupNode> matchGroups = this.containsFromHere(other, null);
            if (CollectionUtils.isNotEmpty(matchGroups))
                occurrences.add(matchGroups);
        }

        if (this.parentGroup != null && this.parentGroup != origin)
            occurrences.addAll(this.parentGroup.containsAnywhere(other, this));
        for (UnifacGroupNode subGroup : this.subGroups)
            if (subGroup != origin)
                occurrences.addAll(subGroup.containsAnywhere(other, this));
        return occurrences;
    }

    /**
     * Check if the subgroups of a UnifacGroupNode object containsAnywhere the subgroups of another
     * To get
     *
     * @param other    UnifacGroupNode to compare
     * @param previous to avoid recheck already checked group nodes
     * @return an empty array if the other GroupNode is not contained, else returns the contained UnifacGroupNode objects matching the @other subgroups
     */
    private List<UnifacGroupNode> containsFromHere(UnifacGroupNode other, UnifacGroupNode previous) {
        List<UnifacGroupNode> matchGroups = new ArrayList<>(Arrays.asList(this));
        boolean containsResult = containsFromHere(other, previous, matchGroups);
        return containsResult ? matchGroups : null;
    }

    private boolean containsFromHere(UnifacGroupNode other, UnifacGroupNode previous, List<UnifacGroupNode> matchGroups) {
        List<UnifacGroupNode> subsequentBondedGroups = this.subsequentBondedGroups(previous);
        for (UnifacGroupNode otherSubGroup : other.subGroups) {
            Optional<UnifacGroupNode> subGroupMatch = subsequentBondedGroups.stream().filter(subGroup ->
                    subGroup.groupCode == otherSubGroup.groupCode
                            && matchGroups.stream().noneMatch(alreadyMatchedGroup -> alreadyMatchedGroup == subGroup)
                            && subGroup.containsFromHere(otherSubGroup, this, matchGroups)
            ).findFirst();
            if (subGroupMatch.isPresent())
                matchGroups.add(subGroupMatch.get());
            else
                return false;
        }
        return true;
    }

    /**
     * Collect all the UnifacGroupNode elements downwards, the parent is not taken into account
     *
     * @return List of UnifacGroupNode
     */
    public ArrayList<UnifacGroupNode> collectAllTreeGroups() {
        ArrayList<UnifacGroupNode> groupNodes = new ArrayList<>();
        this.collectBranchGroups(null, groupNodes);
        return groupNodes;
    }

    /**
     * Collect all the groups of the tree, no matter if the {@code originGroup}
     *
     * @return List of UnifacGroupNode
     */
    private void collectBranchGroups(UnifacGroupNode originGroup, ArrayList<UnifacGroupNode> groupNodes) {
        groupNodes.add(this);
        for (UnifacGroupNode group : this.subsequentBondedGroups(originGroup))
            group.collectBranchGroups(this, groupNodes);
    }

    public int countStrongGroups() {
        ArrayList<UnifacGroupNode> allGroups = collectAllTreeGroups();
        int functionalElements = 0;
        for (UnifacGroupNode group : allGroups)
            functionalElements += group.getUnifacSubGroup().getStrongGroupsNumber();
        return functionalElements;
    }

    public int countTotalGroups() {
        ArrayList<UnifacGroupNode> allGroups = collectAllTreeGroups();
        return allGroups.size();
    }

    public List<UnifacGroupNode> bondedGroups() {
        return this.subsequentBondedGroups(null);
    }

    /**
     * Lists the groups attached to a UnifacGroupNode object, including parent and subgroups, the source @originGroupNode is excluded
     *
     * @param previousGroupNode group to exclude
     * @return bonded groups
     */
    private List<UnifacGroupNode> subsequentBondedGroups(UnifacGroupNode previousGroupNode) {
        List<UnifacGroupNode> bondedGroups = new ArrayList<>();
        if (this.parentGroup != null && this.parentGroup != previousGroupNode)
            bondedGroups.add(this.parentGroup);
        this.subGroups.stream()
                .filter(contributionNode -> contributionNode != null && contributionNode != previousGroupNode)
                .forEach(bondedGroups::add);
        return bondedGroups;
    }

    public void reorderGroupTree() {
        if (this.getParentGroup() == null)
            return;
        UnifacGroupNode parentGroup = this.getParentGroup();
        parentGroup.reorderGroupTree();
        this.getSubGroups().add(parentGroup);
        parentGroup.getSubGroups().remove(this);
    }

    String buildSmiles() {
        return buildSmiles(null);
    }

    /**
     * The '.' elements go first
     *
     * @param originNode
     * @return
     */
    private String buildSmiles(UnifacGroupNode originNode) {
        List<UnifacGroupNode> branchSubGroups = this.subsequentBondedGroups(originNode);
        String smiles = CamdRunner.CONTRIBUTION_GROUPS.getUnifacContributions().get(this.groupCode).getSmilesPattern();

        if (branchSubGroups.size() == 0)
            return smiles;

        for (UnifacGroupNode branchSubGroup : branchSubGroups)
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
