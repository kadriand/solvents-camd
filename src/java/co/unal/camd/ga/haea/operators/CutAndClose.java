package co.unal.camd.ga.haea.operators;

import co.unal.camd.ga.haea.MoleculeSpace;
import co.unal.camd.properties.ProblemParameters;
import co.unal.camd.properties.groups.unifac.ContributionGroup;
import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.CandidateSolventPanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class CutAndClose extends CamdOperator {

    public CutAndClose() {
        this.cardinal = 1;
    }

    @Override
    public List<Molecule> apply(List<Molecule> originalMoleculeList) {
        ContributionGroupNode moleculeRootGroupCopy = originalMoleculeList.get(0).clone().getRootContributionGroup();
        ContributionGroupNode[] subTrees = breakContributionGroupsTree(moleculeRootGroupCopy);

        boolean allowAttachmentOfFunctionalGroup = ProblemParameters.DEFAULT_MAX_FUNCTIONAL_ELEMENTS > originalMoleculeList.get(0).getFunctionalElementsCount();
        ContributionGroup newGroup = MoleculeSpace.fetchRandomGroup(1, allowAttachmentOfFunctionalGroup);

        ContributionGroupNode newMoleculeRoot = joinGroupTreesHeads(subTrees[RANDOM.nextInt(2)], new ContributionGroupNode(newGroup));

        Molecule newMolecule = new Molecule(newMoleculeRoot);
        return Arrays.asList(newMolecule);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        originalUnifacMolecule = "21.2.3(1).1";
        //        originalUnifacMolecule = "1.2.60";

        ContributionGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule originalMolecule = new Molecule(rootFunctionalGroupNode);
        // Molecule originalMolecule = MoleculeSpace.randomMolecule();

        CutAndClose cutAndClose = new CutAndClose();
        List<Molecule> originalMolecules = Arrays.asList(originalMolecule);
        Molecule cutMolecule = cutAndClose.apply(originalMolecules).get(0);
        Molecule cutMolecule1 = cutAndClose.apply(originalMolecules).get(0);
        Molecule cutMolecule2 = cutAndClose.apply(originalMolecules).get(0);

        JFrame firstFrame = CandidateSolventPanel.showMoleculeFrame(originalMolecule, "Original");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        CandidateSolventPanel.showMoleculeFrame(cutMolecule, "CutClose 0")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        CandidateSolventPanel.showMoleculeFrame(cutMolecule1, "CutClose 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        CandidateSolventPanel.showMoleculeFrame(cutMolecule2, "CutClose 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }
}
