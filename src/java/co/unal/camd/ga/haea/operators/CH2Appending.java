package co.unal.camd.ga.haea.operators;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.MoleculeDetailsPanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class CH2Appending extends CamdOperator {

    public CH2Appending() {
        this.cardinal = 1;
    }

    @Override
    public List<Molecule> apply(List<Molecule> originalMoleculeList) {
        Molecule newMolecule = originalMoleculeList.get(0).clone();
        if (newMolecule.getSize() + 1 >= ProblemParameters.getMaxGroupsPerMolecule())
            return Arrays.asList(newMolecule);

        List<UnifacGroupNode> pickedGroups = newMolecule.pickAllGroups();
        UnifacGroupNode randomGroup = pickedGroups.stream().filter(group -> group.getParentGroup() != null).findAny().orElse(null);
        UnifacGroupNode randomGroupParent = randomGroup.getParentGroup();
        randomGroupParent.getSubGroups().remove(randomGroup);

        UnifacGroupNode groupCH2 = new UnifacGroupNode(2);
        groupCH2.getSubGroups().add(randomGroup);
        randomGroupParent.getSubGroups().add(groupCH2);
        return Arrays.asList(newMolecule);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        //        originalUnifacMolecule = "21.2.3(1).1";
        originalUnifacMolecule = "1.2.60";
        originalUnifacMolecule = "42.2.2.3(1).1";


        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule originalMolecule = new Molecule(rootFunctionalGroupNode);
        //  Molecule originalMolecule = MoleculeSpace.randomMolecule();

        CH2Appending moleculeMCH2Appending = new CH2Appending();
        List<Molecule> originalMolecules = Arrays.asList(originalMolecule);
        Molecule mutatedMolecule = moleculeMCH2Appending.apply(originalMolecules).get(0);
        Molecule mutatedMolecule1 = moleculeMCH2Appending.apply(originalMolecules).get(0);
        Molecule mutatedMolecule2 = moleculeMCH2Appending.apply(originalMolecules).get(0);

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
