package co.unal.camd.ga.haea.operators;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import com.co.evolution.model.GeneticOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

abstract class CamdOperator extends GeneticOperator<Molecule> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamdOperator.class);

    static final Random RANDOM = new Random();

    public static final int[] OH_GROUPS = new int[]{3, 13, 16, 17};

    ContributionGroupNode joinGroupTreesHeads(ContributionGroupNode group1, ContributionGroupNode group2) {
        /*TODO CHECK IF THE TRICK IS VALID*/
        if (hasAliphatics(group1) || hasAliphatics(group1)) {
            group1.getSubGroups().add(group2);
            return group1;
        }
        ContributionGroupNode groupCH2 = new ContributionGroupNode(2);
        groupCH2.getSubGroups().add(group1, group2);
        return groupCH2;
    }

    private boolean hasAliphatics(ContributionGroupNode contributionGroupNode) {
        ArrayList<ContributionGroupNode> allGroups = contributionGroupNode.collectAllTreeGroups();
        boolean hasAliphatics = allGroups.stream()
                .anyMatch(group -> group.getContributionGroupDetails().isAliphaticContent());
        return hasAliphatics;
    }

    /**
     * Breaks a ContributionGroupNode into two new independent ones, according to a randomly breakpoint
     * The head ContributionGroupNode of the resulting ContributionGroupNode objects will have an open bound for the attachment of new groups
     *
     * @param contributionGroupNode
     * @return an Array of ContributionGroupNode SORTED in descending order by the number of functional groups
     */
    ContributionGroupNode[] breakContributionGroupsTree(ContributionGroupNode contributionGroupNode) {
        List<ContributionGroupNode> treeGroups = contributionGroupNode.collectAllTreeGroups();
        treeGroups.remove(contributionGroupNode);

        int randomIndex = RANDOM.nextInt(treeGroups.size());
        ContributionGroupNode randomGroup = treeGroups.get(randomIndex);
        ContributionGroupNode parentGroup = randomGroup.getParentGroup();
        parentGroup.getSubGroups().remove(randomGroup);
        parentGroup.reorderGroupTree();

        if (parentGroup.countFunctionalElements() > randomGroup.countFunctionalElements())
            return new ContributionGroupNode[]{parentGroup, randomGroup};
        else
            return new ContributionGroupNode[]{randomGroup, parentGroup};
    }

    void validate(Molecule... molecules) {
        IntStream ohGroupCodes = IntStream.of(OH_GROUPS);
        for (Molecule molecule : molecules) {
            ArrayList<ContributionGroupNode> moleculeGroups = molecule.getRootContributionGroup().collectAllTreeGroups();
            boolean hasOHGroups = moleculeGroups.stream()
                    .anyMatch(group -> ohGroupCodes.anyMatch(value -> value == group.getGroupCode()));
            if(!hasOHGroups)
                continue;
            LOGGER.warn("OH group in molecule, please check the right");

        }
    }

}
