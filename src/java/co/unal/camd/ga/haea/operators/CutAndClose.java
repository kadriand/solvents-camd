package co.unal.camd.ga.haea.operators;

import co.unal.camd.ga.haea.MoleculeSpace;
import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.unifac.UnifacSubGroup;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.MoleculeDetailsPanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class CutAndClose extends CamdOperator {

    public CutAndClose() {
        this.cardinal = 1;
    }

    @Override
    public List<Molecule> apply(List<Molecule> originalMoleculeList) {
        UnifacGroupNode moleculeRootGroupCopy = originalMoleculeList.get(0).clone().getRootContributionGroup();
        UnifacGroupNode[] subTrees = breakContributionGroupsTree(moleculeRootGroupCopy);

        boolean allowAttachmentOfFunctionalGroup = ProblemParameters.DEFAULT_MAX_FUNCTIONAL_ELEMENTS > originalMoleculeList.get(0).getStrongGroupsCount();
        UnifacSubGroup newGroup = MoleculeSpace.fetchRandomGroup(1, allowAttachmentOfFunctionalGroup);

        UnifacGroupNode newMoleculeRoot = joinGroupTreesHeads(subTrees[RANDOM.nextInt(2)], new UnifacGroupNode(newGroup));

        Molecule newMolecule = new Molecule(newMoleculeRoot);
        return Arrays.asList(newMolecule);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        originalUnifacMolecule = "42.2.2.3(1).1";
        originalUnifacMolecule = "42.2.2.2.3(1).1";
        //        originalUnifacMolecule = "1.2.60";

        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule originalMolecule = new Molecule(rootFunctionalGroupNode);
        // Molecule originalMolecule = MoleculeSpace.randomMolecule();

        CutAndClose cutAndClose = new CutAndClose();
        List<Molecule> originalMolecules = Arrays.asList(originalMolecule);
        Molecule cutMolecule = cutAndClose.apply(originalMolecules).get(0);
        Molecule cutMolecule1 = cutAndClose.apply(originalMolecules).get(0);
        Molecule cutMolecule2 = cutAndClose.apply(originalMolecules).get(0);

        JFrame firstFrame = MoleculeDetailsPanel.showMoleculeFrame(originalMolecule, "Original");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        MoleculeDetailsPanel.showMoleculeFrame(cutMolecule, "CutClose 0")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        MoleculeDetailsPanel.showMoleculeFrame(cutMolecule1, "CutClose 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        MoleculeDetailsPanel.showMoleculeFrame(cutMolecule2, "CutClose 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }
}
