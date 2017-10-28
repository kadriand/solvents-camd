package co.unal.camd.ga.haea.operators;

import co.unal.camd.ga.haea.MoleculeSpace;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.MoleculeDetailsPanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Cross extends CamdOperator {

    public Cross() {
        this.cardinal = 2;
    }

    @Override
    public List<Molecule> apply(List<Molecule> originalMoleculeList) {

        UnifacGroupNode moleculeOneRootGroup = originalMoleculeList.get(0).clone().getRootContributionGroup();
        UnifacGroupNode[] subTreesOne = breakContributionGroupsTree(moleculeOneRootGroup);

        UnifacGroupNode moleculeTwoRootGroup = originalMoleculeList.get(1).clone().getRootContributionGroup();
        UnifacGroupNode[] subTreesTwo = breakContributionGroupsTree(moleculeTwoRootGroup);

        UnifacGroupNode newMoleculeOneRoot = joinGroupTreesHeads(subTreesOne[0], subTreesTwo[1]);
        UnifacGroupNode newMoleculeTwoRoot = joinGroupTreesHeads(subTreesTwo[0], subTreesOne[1]);

        Molecule newMoleculeOne = new Molecule(newMoleculeOneRoot);
        Molecule newMoleculeTwo = new Molecule(newMoleculeTwoRoot);

        return Arrays.asList(newMoleculeOne, newMoleculeTwo);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMoleculeOne = "42.65";
        //        originalUnifacMoleculeOne = "1.2.60";
        UnifacGroupNode rootFunctionalGroupNodeOne = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMoleculeOne);
        Molecule originalMoleculeOne = new Molecule(rootFunctionalGroupNodeOne);

        String originalUnifacMoleculeTwo = "22.20.1";
        UnifacGroupNode rootFunctionalGroupNodeTwo = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMoleculeTwo);
        Molecule originalMoleculeTwo = new Molecule(rootFunctionalGroupNodeTwo);

        originalMoleculeOne = MoleculeSpace.randomMolecule();
        originalMoleculeTwo = MoleculeSpace.randomMolecule();

        Cross moleculeCross = new Cross();
        List<Molecule> crossedMolecules = moleculeCross.apply(Arrays.asList(originalMoleculeOne, originalMoleculeTwo));

        JFrame firstFrame = MoleculeDetailsPanel.showMoleculeFrame(originalMoleculeOne, "Original One");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        MoleculeDetailsPanel.showMoleculeFrame(originalMoleculeTwo, "Original Two")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        MoleculeDetailsPanel.showMoleculeFrame(crossedMolecules.get(0), "Crossed 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        MoleculeDetailsPanel.showMoleculeFrame(crossedMolecules.get(1), "Crossed 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }
}
