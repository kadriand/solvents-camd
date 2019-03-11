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
import java.util.stream.Collectors;

/**
 */
public class MoleculeMutation extends CamdOperator {

    public MoleculeMutation() {
        this.cardinal = 1;
    }

    @Override
    public List<Molecule> apply(List<Molecule> originalMoleculeList) {
        Molecule newMolecule = originalMoleculeList.get(0).clone();

        List<ContributionGroupNode> pickedGroups = newMolecule.pickAllGroups();
        boolean allowReplacementWithFunctionalGroup = ProblemParameters.DEFAULT_MAX_FUNCTIONAL_ELEMENTS > originalMoleculeList.get(0).getFunctionalElementsCount();
        if (!allowReplacementWithFunctionalGroup)
            pickedGroups = pickedGroups.stream()
                    .filter(pickedGroup -> pickedGroup.getContributionGroupDetails().getMainGroup().getCode() != 1)
                    .collect(Collectors.toList());

        int randomIndex = RANDOM.nextInt(pickedGroups.size());
        ContributionGroupNode randomGroup = pickedGroups.get(randomIndex);
        ContributionGroup newGroup = MoleculeSpace.fetchRandomGroup(randomGroup.getContributionGroupDetails().getValence(), allowReplacementWithFunctionalGroup);
        randomGroup.setGroupCode(newGroup.getCode());
        return Arrays.asList(newMolecule);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        originalUnifacMolecule = "42.2.2.3(1).1";
        //        originalUnifacMolecule = "1.2.60";

        ContributionGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule originalMolecule = new Molecule(rootFunctionalGroupNode);
//         Molecule originalMolecule = MoleculeSpace.randomMolecule();

        MoleculeMutation moleculeMutation = new MoleculeMutation();
        List<Molecule> originalMolecules = Arrays.asList(originalMolecule);
        Molecule mutatedMolecule = moleculeMutation.apply(originalMolecules).get(0);
        Molecule mutatedMolecule1 = moleculeMutation.apply(originalMolecules).get(0);
        Molecule mutatedMolecule2 = moleculeMutation.apply(originalMolecules).get(0);

        JFrame firstFrame = CandidateSolventPanel.showMoleculeFrame(originalMolecule, "Original");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        CandidateSolventPanel.showMoleculeFrame(mutatedMolecule, "Mutated 0")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        CandidateSolventPanel.showMoleculeFrame(mutatedMolecule1, "Mutated 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        CandidateSolventPanel.showMoleculeFrame(mutatedMolecule2, "Mutated 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }
}
