package co.unal.camd.ga.haea.operators;

import co.unal.camd.ga.haea.MoleculeSpace;
import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.MoleculeDetailsPanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class CutAndReplace extends CamdOperator {

    public CutAndReplace() {
        this.cardinal = 1;
    }

    @Override
    public List<Molecule> apply(List<Molecule> originalMoleculeList) {
        UnifacGroupNode moleculeRootGroupCopy = originalMoleculeList.get(0).clone().getRootContributionGroup();
        UnifacGroupNode[] headMoleculeTrees = breakContributionGroupsTree(moleculeRootGroupCopy);

        Molecule newSubBranchSource;
        do {
            newSubBranchSource = MoleculeSpace.randomMolecule();
        } while (newSubBranchSource.getSize() < 3 || 6 < newSubBranchSource.getSize());
        UnifacGroupNode[] moleculeTreesSource = breakContributionGroupsTree(newSubBranchSource.getRootContributionGroup());

        UnifacGroupNode attachedGroupsTree = moleculeTreesSource[1].countTotalGroups() > 1 ? moleculeTreesSource[1] : moleculeTreesSource[0];
        UnifacGroupNode newHeadGroupsTree = headMoleculeTrees[0];
        if (newHeadGroupsTree.countTotalGroups() + attachedGroupsTree.countTotalGroups() > ProblemParameters.getMaxGroupsPerMolecule())
            newHeadGroupsTree = headMoleculeTrees[1];

        newHeadGroupsTree.getSubGroups().add(attachedGroupsTree);
        Molecule newMolecule = new Molecule(newHeadGroupsTree);
        return Arrays.asList(newMolecule);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        originalUnifacMolecule = "21.2.3(1).1";
        originalUnifacMolecule = "42.2.2.2.3(1).1";
        //        originalUnifacMolecule = "1.2.60";

        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule originalMolecule = new Molecule(rootFunctionalGroupNode);
        // Molecule originalMolecule = MoleculeSpace.randomMolecule();

        CutAndReplace cutAndReplace = new CutAndReplace();
        List<Molecule> originalMolecules = Arrays.asList(originalMolecule);
        Molecule cutMolecule = cutAndReplace.apply(originalMolecules).get(0);
        Molecule cutMolecule1 = cutAndReplace.apply(originalMolecules).get(0);
        Molecule cutMolecule2 = cutAndReplace.apply(originalMolecules).get(0);

        JFrame firstFrame = MoleculeDetailsPanel.showMoleculeFrame(originalMolecule, "Original");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        MoleculeDetailsPanel.showMoleculeFrame(cutMolecule, "CutReplace 0")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        MoleculeDetailsPanel.showMoleculeFrame(cutMolecule1, "CutReplace 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        MoleculeDetailsPanel.showMoleculeFrame(cutMolecule2, "CutReplace 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }

}
