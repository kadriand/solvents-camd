package co.unal.camd.ga.haea.operators;

import co.unal.camd.ga.haea.MoleculeSpace;
import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.CandidateSolventPanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Cross extends CamdOperator {

    public Cross() {
        this.cardinal = 2;
    }

    @Override
    public List<Molecule> apply(List<Molecule> originalMoleculeList) {

        ContributionGroupNode moleculeOneRootGroup = originalMoleculeList.get(0).clone().getRootContributionGroup();
        ContributionGroupNode[] subTreesOne = breakContributionGroupsTree(moleculeOneRootGroup);

        ContributionGroupNode moleculeTwoRootGroup = originalMoleculeList.get(1).clone().getRootContributionGroup();
        ContributionGroupNode[] subTreesTwo = breakContributionGroupsTree(moleculeTwoRootGroup);

        ContributionGroupNode newMoleculeOneRoot = joinGroupTreesHeads(subTreesOne[0], subTreesTwo[1]);
        ContributionGroupNode newMoleculeTwoRoot = joinGroupTreesHeads(subTreesTwo[0], subTreesOne[1]);

        Molecule newMoleculeOne = new Molecule(newMoleculeOneRoot);
        Molecule newMoleculeTwo = new Molecule(newMoleculeTwoRoot);

        return Arrays.asList(newMoleculeOne, newMoleculeTwo);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMoleculeOne = "42.65";
        //        originalUnifacMoleculeOne = "1.2.60";
        ContributionGroupNode rootFunctionalGroupNodeOne = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMoleculeOne);
        Molecule originalMoleculeOne = new Molecule(rootFunctionalGroupNodeOne);

        String originalUnifacMoleculeTwo = "22.20.1";
        ContributionGroupNode rootFunctionalGroupNodeTwo = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMoleculeTwo);
        Molecule originalMoleculeTwo = new Molecule(rootFunctionalGroupNodeTwo);

        originalMoleculeOne = MoleculeSpace.randomMolecule();
        originalMoleculeTwo = MoleculeSpace.randomMolecule();

        Cross moleculeCross = new Cross();
        List<Molecule> crossedMolecules = moleculeCross.apply(Arrays.asList(originalMoleculeOne, originalMoleculeTwo));

        JFrame firstFrame = CandidateSolventPanel.showMoleculeFrame(originalMoleculeOne, "Original One");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        CandidateSolventPanel.showMoleculeFrame(originalMoleculeTwo, "Original Two")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        CandidateSolventPanel.showMoleculeFrame(crossedMolecules.get(0), "Crossed 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        CandidateSolventPanel.showMoleculeFrame(crossedMolecules.get(1), "Crossed 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }
}
