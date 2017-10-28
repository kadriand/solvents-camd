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

        List<UnifacGroupNode> pickedGroups = newMolecule.pickAllGroups();
        boolean allowReplacementWithFunctionalGroup = ProblemParameters.DEFAULT_MAX_FUNCTIONAL_ELEMENTS > originalMoleculeList.get(0).getStrongGroupsCount();
        if (!allowReplacementWithFunctionalGroup)
            pickedGroups = pickedGroups.stream()
                    .filter(pickedGroup -> pickedGroup.getUnifacSubGroup().getMainGroup().getCode() != 1)
                    .collect(Collectors.toList());

        int randomIndex = RANDOM.nextInt(pickedGroups.size());
        UnifacGroupNode randomGroup = pickedGroups.get(randomIndex);
        UnifacSubGroup newGroup = MoleculeSpace.fetchRandomGroup(randomGroup.getUnifacSubGroup().getValence(), allowReplacementWithFunctionalGroup);
        randomGroup.setGroupCode(newGroup.getCode());
        return Arrays.asList(newMolecule);
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        originalUnifacMolecule = "42.2.2.3(1).1";
        //        originalUnifacMolecule = "1.2.60";

        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule originalMolecule = new Molecule(rootFunctionalGroupNode);
//         Molecule originalMolecule = MoleculeSpace.randomMolecule();

        MoleculeMutation moleculeMutation = new MoleculeMutation();
        List<Molecule> originalMolecules = Arrays.asList(originalMolecule);
        Molecule mutatedMolecule = moleculeMutation.apply(originalMolecules).get(0);
        Molecule mutatedMolecule1 = moleculeMutation.apply(originalMolecules).get(0);
        Molecule mutatedMolecule2 = moleculeMutation.apply(originalMolecules).get(0);

        JFrame firstFrame = MoleculeDetailsPanel.showMoleculeFrame(originalMolecule, "Original");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        MoleculeDetailsPanel.showMoleculeFrame(mutatedMolecule, "Mutated 0")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        MoleculeDetailsPanel.showMoleculeFrame(mutatedMolecule1, "Mutated 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        MoleculeDetailsPanel.showMoleculeFrame(mutatedMolecule2, "Mutated 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }
}
