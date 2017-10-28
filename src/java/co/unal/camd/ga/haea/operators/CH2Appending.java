package co.unal.camd.ga.haea.operators;

import co.unal.camd.properties.ProblemParameters;
import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.CandidateSolventPanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
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

        List<ContributionGroupNode> pickedGroups = newMolecule.pickAllGroups();
        pickedGroups.remove(newMolecule.getRootContributionGroup());
        int randomIndex = RANDOM.nextInt(pickedGroups.size());
        ContributionGroupNode randomGroup = pickedGroups.get(randomIndex);
        ContributionGroupNode randomGroupParent = randomGroup.getParentGroup();
        randomGroupParent.getSubGroups().remove(randomGroup);

        ContributionGroupNode groupCH2 = new ContributionGroupNode(2);
        groupCH2.getSubGroups().add(randomGroup);
        randomGroupParent.getSubGroups().add(groupCH2);
        return Arrays.asList(newMolecule);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        //        originalUnifacMolecule = "21.2.3(1).1";
        originalUnifacMolecule = "1.2.60";

        ContributionGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule originalMolecule = new Molecule(rootFunctionalGroupNode);
        //  Molecule originalMolecule = MoleculeSpace.randomMolecule();

        CH2Appending moleculeMCH2Appending = new CH2Appending();
        List<Molecule> originalMolecules = Arrays.asList(originalMolecule);
        Molecule mutatedMolecule = moleculeMCH2Appending.apply(originalMolecules).get(0);
        Molecule mutatedMolecule1 = moleculeMCH2Appending.apply(originalMolecules).get(0);
        Molecule mutatedMolecule2 = moleculeMCH2Appending.apply(originalMolecules).get(0);

        JFrame firstFrame = CandidateSolventPanel.showMoleculeFrame(originalMolecule, "Original");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        CandidateSolventPanel.showMoleculeFrame(mutatedMolecule, "CH2Appended 0")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        CandidateSolventPanel.showMoleculeFrame(mutatedMolecule1, "CH2Appended 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        CandidateSolventPanel.showMoleculeFrame(mutatedMolecule2, "CH2Appended 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }
}
