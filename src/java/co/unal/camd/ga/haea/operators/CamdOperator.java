package co.unal.camd.ga.haea.operators;

import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.model.molecule.UnifacGroupNode;
import com.co.evolution.model.GeneticOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

abstract class CamdOperator extends GeneticOperator<Molecule> {

    static final Random RANDOM = new Random();

    UnifacGroupNode joinGroupTreesHeads(UnifacGroupNode group1, UnifacGroupNode group2) {
        /*TODO CHECK IF THE TRICK IS VALID*/
        if (hasAliphatics(group1) || hasAliphatics(group1)) {
            group1.getSubGroups().add(group2);
            return group1;
        }
        UnifacGroupNode groupCH2 = new UnifacGroupNode(2);
        groupCH2.getSubGroups().add(group1, group2);
        return groupCH2;
    }

    private boolean hasAliphatics(UnifacGroupNode unifacGroupNode) {
        ArrayList<UnifacGroupNode> allGroups = unifacGroupNode.collectAllTreeGroups();
        boolean hasAliphatics = allGroups.stream()
                .anyMatch(group -> group.getUnifacSubGroup().isAliphaticContent());
        return hasAliphatics;
    }

    /**
     * Breaks a UnifacGroupNode into two new independent ones, according to a randomly breakpoint
     * The head UnifacGroupNode of the resulting UnifacGroupNode objects will have an open bound for the attachment of new groups
     *
     * @param unifacGroupNode
     * @return an Array of UnifacGroupNode SORTED in descending order by the number of functional groups
     */
    UnifacGroupNode[] breakContributionGroupsTree(UnifacGroupNode unifacGroupNode) {
        List<UnifacGroupNode> treeGroups = unifacGroupNode.collectAllTreeGroups();
        treeGroups.remove(unifacGroupNode);

        int randomIndex = RANDOM.nextInt(treeGroups.size());
        UnifacGroupNode randomGroup = treeGroups.get(randomIndex);
        UnifacGroupNode parentGroup = randomGroup.getParentGroup();
        parentGroup.getSubGroups().remove(randomGroup);
        parentGroup.reorderGroupTree();

        if (parentGroup.countStrongGroups() > randomGroup.countStrongGroups())
            return new UnifacGroupNode[]{parentGroup, randomGroup};
        else
            return new UnifacGroupNode[]{randomGroup, parentGroup};
    }

}
