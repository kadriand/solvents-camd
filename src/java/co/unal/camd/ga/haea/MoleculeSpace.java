package co.unal.camd.ga.haea;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.unifac.UnifacSubGroup;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.MoleculeDetailsPanel;
import com.co.evolution.model.FitnessCalculation;
import com.co.evolution.model.Population;
import com.co.evolution.model.PopulationInitialization;
import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MoleculeSpace implements PopulationInitialization<Molecule> {

    private static final Random RANDOM = new Random();

    @Getter
    private int size;

    @Override
    public Population<Molecule> init(FitnessCalculation<Molecule> fitnessCalculation) {
        Population<Molecule> population = new Population<>();
        this.size = ProblemParameters.getParentsPoolSize();
        for (int i = 0; i < size; i++) {
            Molecule moleculeIndividual = initRandom();
            moleculeIndividual.setObjectiveValues(fitnessCalculation.computeObjectives(moleculeIndividual));
            population.add(moleculeIndividual);
        }
        fitnessCalculation.computePopulationRanksFitness(population);
        for (Molecule molecule : population)
            molecule.setFitness(fitnessCalculation.computeIndividualFitness(molecule, population));
        return population;
    }

    @Override
    public Molecule initRandom() {
        return randomMolecule();
    }

    /**
     * At least two groups
     *
     * @return
     */
    public static Molecule randomMolecule() {
        int moleculeSize = RANDOM.nextInt(9) + 2;
        int openBonds;

        UnifacSubGroup headUnifacSubGroup = fetchRandomGroup();
        UnifacGroupNode headGroup = new UnifacGroupNode(headUnifacSubGroup);
        openBonds = headUnifacSubGroup.getValence();

        while (openBonds > 0 && moleculeSize - headGroup.countSubgroupsDownStream() - openBonds > 0) {
            ArrayList<UnifacGroupNode> moleculeGroups = headGroup.collectAllTreeGroups();
            int randomIndex = RANDOM.nextInt(moleculeGroups.size());
            UnifacGroupNode randomGroup = moleculeGroups.get(randomIndex);

            // Is it possible to add a new group to the selected one?
            if (randomGroup.getUnifacSubGroup().getValence() > randomGroup.bondedGroups().size()) {
                boolean allowMoreFunctionalElements = randomGroup.countStrongGroups() < ProblemParameters.DEFAULT_MAX_FUNCTIONAL_ELEMENTS;
                int maxValidValence = moleculeSize - openBonds - headGroup.countSubgroupsDownStream();
                UnifacSubGroup newGroup = fetchRandomGroup(allowMoreFunctionalElements);
                if (newGroup.getValence() > maxValidValence)
                    newGroup = fetchRandomGroup(maxValidValence, allowMoreFunctionalElements);
                randomGroup.getSubGroups().add(new UnifacGroupNode(newGroup));
                openBonds = computeOpenBonds(headGroup);
            }
        }

        headGroup.collectAllTreeGroups().stream()
                .filter(group -> group.getUnifacSubGroup().getValence() > group.bondedGroups().size())
                .forEach(group -> {
                    for (int g = group.bondedGroups().size(); g < group.getUnifacSubGroup().getValence(); g++) {
                        UnifacSubGroup randomClosingGroup = fetchRandomGroup(1, ProblemParameters.DEFAULT_MAX_FUNCTIONAL_ELEMENTS > headGroup.countStrongGroups());
                        UnifacGroupNode closingGroupNode = new UnifacGroupNode(randomClosingGroup);
                        group.getSubGroups().add(closingGroupNode);
                    }
                });

        return new Molecule(headGroup);
    }

    private static int computeOpenBonds(UnifacGroupNode headGroup) {
        int openBonds = 0;
        for (UnifacGroupNode groupNode : headGroup.collectAllTreeGroups())
            openBonds += groupNode.getUnifacSubGroup().getValence() - groupNode.bondedGroups().size();
        return openBonds;
    }

    /**
     * Returns a random contribution group code given a valence and whether it must be functional
     *
     * @param valence
     * @param strongGroup
     * @return
     */
    public static UnifacSubGroup fetchRandomGroup(int valence, boolean strongGroup) {
        List<UnifacSubGroup> filteredUnifacSubGroups;
        filteredUnifacSubGroups = CamdRunner.CONTRIBUTION_GROUPS.getValenceContributionGroups().get(valence).stream()
                .filter(contributionGroup -> contributionGroup.getMainGroup().getFamilyGroup().getProbability() > 0 && (strongGroup || contributionGroup.getStrongGroupsNumber() == 0))
                .collect(Collectors.toList());

        int randomIndex = RANDOM.nextInt(filteredUnifacSubGroups.size());
        return filteredUnifacSubGroups.get(randomIndex);
    }

    /**
     * Returns a random contribution group. Probability 50% functional 50% non-functional
     *
     * @return
     */
    public static UnifacSubGroup fetchRandomGroup() {
        return fetchRandomGroup(RANDOM.nextBoolean());
    }

    /**
     * Returns a random contribution group code of any valence
     *
     * @param functional
     * @return
     */
    public static UnifacSubGroup fetchRandomGroup(boolean functional) {
        List<UnifacSubGroup> filteredUnifacSubGroups;
        filteredUnifacSubGroups = CamdRunner.CONTRIBUTION_GROUPS.getUnifacContributions().values().stream()
                .filter(contributionGroup -> contributionGroup.getMainGroup().getFamilyGroup().getProbability() > 0 && contributionGroup.getValence() > 0 && (functional || contributionGroup.getStrongGroupsNumber() == 0))
                .collect(Collectors.toList());

        int randomIndex = RANDOM.nextInt(filteredUnifacSubGroups.size());
        return filteredUnifacSubGroups.get(randomIndex);
    }

    public static void main(String... args) {
        Molecule randomMolecule = randomMolecule();
        Molecule randomMolecule1 = randomMolecule();
        Molecule randomMolecule2 = randomMolecule();
        Molecule randomMolecule3 = randomMolecule();
        Molecule randomMolecule4 = randomMolecule();
        Molecule randomMolecule5 = randomMolecule();

        JFrame firstFrame = MoleculeDetailsPanel.showMoleculeFrame(randomMolecule, "Random 0");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        MoleculeDetailsPanel.showMoleculeFrame(randomMolecule1, "Random 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        MoleculeDetailsPanel.showMoleculeFrame(randomMolecule2, "Random 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
        MoleculeDetailsPanel.showMoleculeFrame(randomMolecule3, "Random 3")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        MoleculeDetailsPanel.showMoleculeFrame(randomMolecule4, "Random 4")
                .setLocation(firstFrame.getX() - firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
        MoleculeDetailsPanel.showMoleculeFrame(randomMolecule5, "Random 5")
                .setLocation(firstFrame.getX() - firstFrame.getWidth(), firstFrame.getY());
    }
}