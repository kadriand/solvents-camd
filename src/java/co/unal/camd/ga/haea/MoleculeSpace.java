package co.unal.camd.ga.haea;

import co.unal.camd.properties.ProblemParameters;
import co.unal.camd.properties.groups.unifac.ContributionGroup;
import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.view.CamdRunner;
import co.unal.camd.view.CandidateSolventPanel;
import com.co.evolution.model.FitnessCalculation;
import com.co.evolution.model.Population;
import com.co.evolution.model.PopulationInitialization;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MoleculeSpace implements PopulationInitialization<Molecule> {

    private static final Random RANDOM = new Random();

    @Override
    public Population<Molecule> init(FitnessCalculation<Molecule> fitnessCalculation) {
        Population<Molecule> pop = new Population<>();
        int populationSize = ProblemParameters.getParentsPoolSize();
        for (int i = 0; i < populationSize; i++) {
            Molecule realIndividual = initRandom();
            int functionsSize = fitnessCalculation.getObjectiveFunctions().length;
            realIndividual.setObjectiveValues(new double[functionsSize]);
            for (int j = 0; j < functionsSize; j++)
                realIndividual.getObjectiveValues()[j] = fitnessCalculation.getObjectiveFunctions()[j].compute(realIndividual);
            pop.add(realIndividual);
        }
        fitnessCalculation.newGenerationApply(pop);
        for (Molecule molecule : pop)
            molecule.setFitness(fitnessCalculation.calculate(molecule, pop));
        return pop;
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

        ContributionGroup headContributionGroup = fetchRandomGroup();
        ContributionGroupNode headGroup = new ContributionGroupNode(headContributionGroup);
        openBonds = headContributionGroup.getValence();

        while (openBonds > 0 && moleculeSize - headGroup.countSubgroupsDownStream() - openBonds > 0) {
            ArrayList<ContributionGroupNode> moleculeGroups = headGroup.collectAllTreeGroups();
            int randomIndex = RANDOM.nextInt(moleculeGroups.size());
            ContributionGroupNode randomGroup = moleculeGroups.get(randomIndex);

            // Is it possible to add a new group to the selected one?
            if (randomGroup.getContributionGroupDetails().getValence() > randomGroup.bondedGroups().size()) {
                boolean allowMoreFunctionalElements = randomGroup.countFunctionalElements() < ProblemParameters.DEFAULT_MAX_FUNCTIONAL_ELEMENTS;
                int maxValidValence = moleculeSize - openBonds - headGroup.countSubgroupsDownStream();
                ContributionGroup newGroup = fetchRandomGroup(allowMoreFunctionalElements);
                if (newGroup.getValence() > maxValidValence)
                    newGroup = fetchRandomGroup(maxValidValence, allowMoreFunctionalElements);
                randomGroup.getSubGroups().add(new ContributionGroupNode(newGroup));
                openBonds = computeOpenBonds(headGroup);
            }
        }

        headGroup.collectAllTreeGroups().stream()
                .filter(group -> group.getContributionGroupDetails().getValence() > group.bondedGroups().size())
                .forEach(group -> {
                    for (int g = group.bondedGroups().size(); g < group.getContributionGroupDetails().getValence(); g++) {
                        ContributionGroup randomClosingGroup = fetchRandomGroup(1, ProblemParameters.DEFAULT_MAX_FUNCTIONAL_ELEMENTS > headGroup.countFunctionalElements());
                        ContributionGroupNode closingGroupNode = new ContributionGroupNode(randomClosingGroup);
                        group.getSubGroups().add(closingGroupNode);
                    }
                });

        return new Molecule(headGroup);
    }

    private static int computeOpenBonds(ContributionGroupNode headGroup) {
        int openBonds = 0;
        for (ContributionGroupNode groupNode : headGroup.collectAllTreeGroups())
            openBonds += groupNode.getContributionGroupDetails().getValence() - groupNode.bondedGroups().size();
        return openBonds;
    }

    /**
     * Returns a random contribution group code given a valence and whether it must be functional
     *
     * @param valence
     * @param functional
     * @return
     */
    public static ContributionGroup fetchRandomGroup(int valence, boolean functional) {
        List<ContributionGroup> filteredContributionGroups;
        filteredContributionGroups = CamdRunner.CONTRIBUTION_GROUPS.getValenceContributionGroups().get(valence).stream()
                .filter(contributionGroup -> contributionGroup.getMainGroup().getFamilyGroup().getProbability() > 0 && (functional || contributionGroup.getFunctionalElementsNumber() == 0))
                .collect(Collectors.toList());

        int randomIndex = RANDOM.nextInt(filteredContributionGroups.size());
        return filteredContributionGroups.get(randomIndex);
    }

    /**
     * Returns a random contribution group. Probability 50% functional 50% non-functional
     *
     * @return
     */
    public static ContributionGroup fetchRandomGroup() {
        return fetchRandomGroup(RANDOM.nextBoolean());
    }

    /**
     * Returns a random contribution group code of any valence
     *
     * @param functional
     * @return
     */
    public static ContributionGroup fetchRandomGroup(boolean functional) {
        List<ContributionGroup> filteredContributionGroups;
        filteredContributionGroups = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().values().stream()
                .filter(contributionGroup -> contributionGroup.getMainGroup().getFamilyGroup().getProbability() > 0 && contributionGroup.getValence() > 0 && (functional || contributionGroup.getFunctionalElementsNumber() == 0))
                .collect(Collectors.toList());

        int randomIndex = RANDOM.nextInt(filteredContributionGroups.size());
        return filteredContributionGroups.get(randomIndex);
    }

    public static void main(String... args) {
        Molecule randomMolecule = randomMolecule();
        Molecule randomMolecule1 = randomMolecule();
        Molecule randomMolecule2 = randomMolecule();
        Molecule randomMolecule3 = randomMolecule();
        Molecule randomMolecule4 = randomMolecule();
        Molecule randomMolecule5 = randomMolecule();

        JFrame firstFrame = CandidateSolventPanel.showMoleculeFrame(randomMolecule, "Random 0");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        CandidateSolventPanel.showMoleculeFrame(randomMolecule1, "Random 1")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        CandidateSolventPanel.showMoleculeFrame(randomMolecule2, "Random 2")
                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
        CandidateSolventPanel.showMoleculeFrame(randomMolecule3, "Random 3")
                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        CandidateSolventPanel.showMoleculeFrame(randomMolecule4, "Random 4")
                .setLocation(firstFrame.getX() - firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
        CandidateSolventPanel.showMoleculeFrame(randomMolecule5, "Random 5")
                .setLocation(firstFrame.getX() - firstFrame.getWidth(), firstFrame.getY());
    }
}