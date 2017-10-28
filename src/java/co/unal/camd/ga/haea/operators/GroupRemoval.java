package co.unal.camd.ga.haea.operators;

import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.MoleculeDetailsPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Choose a random group bonded to an aliphatic group, then the chosen group is removed and the aliphatic group is replaced by the aliphatic with valence minus one
 *
 * The resulting molecule should have at least two contribution groups
 *
 */
public class GroupRemoval extends CamdOperator {

    public GroupRemoval() {
        this.cardinal = 1;
    }

    @Override
    public List<Molecule> apply(List<Molecule> originalMoleculeList) {
        Molecule newMolecule = originalMoleculeList.get(0).clone();
        if (newMolecule.getSize() < 3)
            return Arrays.asList(newMolecule);

        // Filtering aliphatic groups bonded to removable groups, i.e. groups with valence 1
        ArrayList<UnifacGroupNode> pickAllGroups = newMolecule.pickAllGroups();
        List<UnifacGroupNode> pickedGroups = pickAllGroups.stream()
                .filter(group -> group.getUnifacSubGroup().getMainGroup().getCode() == 1
                        && group.getUnifacSubGroup().getValence() > 1
                        && group.bondedGroups().stream().anyMatch(surroundingGroup -> surroundingGroup.getUnifacSubGroup().getValence() == 1)
                ).collect(Collectors.toList());

        if (pickedGroups.size() < 1)
            return Arrays.asList(newMolecule);

        int randomIndex = RANDOM.nextInt(pickedGroups.size());
        UnifacGroupNode randomGroup = pickedGroups.get(randomIndex);
        UnifacGroupNode groupToRemove = randomGroup.bondedGroups().stream().filter(group -> group.getUnifacSubGroup().getValence() == 1).findAny().get();

        UnifacGroupNode groupToUpdate;
        // Is the group to remove the root of the molecule?
        if (groupToRemove.getParentGroup() == null) {
            groupToUpdate = groupToRemove.getSubGroups().get(0);
            groupToRemove.getSubGroups().remove(groupToUpdate);
            newMolecule = new Molecule(groupToUpdate);
        } else {
            groupToUpdate = groupToRemove.getParentGroup();
            groupToUpdate.getSubGroups().remove(groupToRemove);
        }
        // As it is aliphatic, reducing in one the UNIFAC group code, the resulting group will be an aliphatic with lower valence, so the just-removed group will be balanced
        groupToUpdate.setGroupCode(groupToUpdate.getGroupCode() - 1);

        return Arrays.asList(newMolecule);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.2.75.3(74).75.75.2.74";
        originalUnifacMolecule = "21.2.3(1).1";
        originalUnifacMolecule = "42.2.2.2.3(1).1";

        //        originalUnifacMolecule = "1.2.60";

        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
                Molecule originalMolecule = new Molecule(rootFunctionalGroupNode);
//        Molecule originalMolecule = MoleculeSpace.randomMolecule();

        GroupRemoval groupRemoval = new GroupRemoval();
        List<Molecule> originalMolecules = Arrays.asList(originalMolecule);
        Molecule mutatedMolecule = groupRemoval.apply(originalMolecules).get(0);
        Molecule mutatedMolecule1 = groupRemoval.apply(originalMolecules).get(0);
        Molecule mutatedMolecule2 = groupRemoval.apply(originalMolecules).get(0);

        JFrame firstFrame = MoleculeDetailsPanel.showMoleculeFrame(originalMolecule, "Original");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        MoleculeDetailsPanel.showMoleculeFrame(mutatedMolecule, "CH2Appended 0")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        MoleculeDetailsPanel.showMoleculeFrame(mutatedMolecule1, "CH2Appended 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        MoleculeDetailsPanel.showMoleculeFrame(mutatedMolecule2, "CH2Appended 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }
}
