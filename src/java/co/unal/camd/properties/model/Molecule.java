package co.unal.camd.properties.model;

import co.unal.camd.properties.methods.BoilingPoint;
import co.unal.camd.properties.methods.Density;
import co.unal.camd.properties.methods.DielectricConstant;
import co.unal.camd.properties.methods.GibbsEnergy;
import co.unal.camd.properties.methods.MeltingPoint;
import co.unal.camd.properties.methods.MolecularWeight;
import co.unal.camd.properties.parameters.unifac.ThermodynamicFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.ThermodynamicSecondOrderContribution;
import co.unal.camd.view.CamdRunner;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * this create ramdom molecules
 *
 * @author Kevin Adrián Rodríguez Ruiz
 */
public class Molecule {

    private ContributionGroupNode rootContributionGroup;

    private int size = -1;

    @Getter
    @Setter
    private double fitness;

    @Getter
    @Setter
    private double temperature;

    @Getter
    private double x; //composición

    private ThermodynamicProperties thermodynamicProperties;

    private Map<ThermodynamicFirstOrderContribution, Integer> firstOrderContributions;

    private Map<ThermodynamicSecondOrderContribution, Integer> secondOrderContributions;

    public Molecule(Molecule molecule) {
        rootContributionGroup = molecule.rootContributionGroup.clone();
        x = molecule.x;
        fitness = molecule.fitness;
    }

    public Molecule(ContributionGroupNode root) {
        rootContributionGroup = root;
    }

    /**
     * Every time the rootContributionGroup is accessed, the second order groups get emptied
     *
     * @return
     */
    public ContributionGroupNode getRootContributionGroup() {
        this.secondOrderContributions = null;
        this.firstOrderContributions = null;
        this.thermodynamicProperties = null;
        this.size = -1;
        return this.rootContributionGroup;
    }

    public ThermodynamicProperties getThermodynamicProperties() {
        if (thermodynamicProperties == null) {
            thermodynamicProperties = new ThermodynamicProperties();
            double gibbsEnergy = GibbsEnergy.compute(this);
            double boilingPoint = BoilingPoint.compute(this);
            double meltingPoint = MeltingPoint.compute(this);
            double molecularWeight = MolecularWeight.compute(this);
            double density = Density.compute(this, temperature);
            DielectricConstant dielectricConstant = new DielectricConstant(this, temperature);
            thermodynamicProperties.
                    setGibbsEnergy(gibbsEnergy)
                    .setBoilingPoint(boilingPoint)
                    .setDensity(density)
                    .setMeltingPoint(meltingPoint)
                    .setDielectricConstant(dielectricConstant.compute())
                    .setMolecularWeight(molecularWeight);
        }
        return thermodynamicProperties;
    }

    public int getSize() {
        if (this.size == -1)
            this.size = rootContributionGroup.countTotalGroups();
        return size;
    }

    /**
     * Return a map with the present ThermodynamicFirstOrderContribution's and the occurrences of each one
     *
     * @return <ThermodynamicFirstOrderContribution, occurrences>
     */
    public Map<ThermodynamicFirstOrderContribution, Integer> getFirstOrderContributions() {
        if (firstOrderContributions == null) {
            Map<ThermodynamicFirstOrderContribution, Integer> firstOrderContribution = new HashMap<>();
            findFirstOrderGroups(this.rootContributionGroup, firstOrderContribution);
            this.firstOrderContributions = firstOrderContribution;
        }
        return firstOrderContributions;
    }

    /**
     * Return a map with the present ThermodynamicSecondOrderContribution's and the occurrences of each one
     *
     * @return <ThermodynamicSecondOrderContribution, occurrences>
     */
    public Map<ThermodynamicSecondOrderContribution, Integer> getSecondOrderContributions() {
        if (secondOrderContributions == null) {
            Map<ThermodynamicSecondOrderContribution, Integer> secondOrderContributions = new HashMap<>();
            findSecondOrderGroups(this.rootContributionGroup, secondOrderContributions);
            this.secondOrderContributions = secondOrderContributions;
        }
        return secondOrderContributions;
    }

    public ContributionGroupNode getGroupAt(int i) {
        return pickAllGroups().get(i);
    }

    public ArrayList<ContributionGroupNode> pickAllGroups() {
        ArrayList<ContributionGroupNode> groupNodes = new ArrayList<>();
        return pickGroups(rootContributionGroup, groupNodes);
    }

    private ArrayList<ContributionGroupNode> pickGroups(ContributionGroupNode contributionGroup, ArrayList<ContributionGroupNode> groupNodes) {
        if (contributionGroup != null) {
            groupNodes.add(contributionGroup);
            for (ContributionGroupNode subGroup : contributionGroup.getSubGroups())
                pickGroups(subGroup, groupNodes);
        }
        return groupNodes;
    }

    public MoleculeGroups getGroupsArray() {
        ArrayList<ContributionGroupNode> groupsNodes = pickAllGroups();
        int n = groupsNodes.size();
        int[] groups = new int[n];
        for (int i = 0; i < n; i++) {
            groups[i] = groupsNodes.get(i).getGroupCode();
        }
        MoleculeGroups moleculeGroups = new MoleculeGroups(groups);
        return moleculeGroups;
    }

    private void findFirstOrderGroups(ContributionGroupNode contributionGroup, Map<ThermodynamicFirstOrderContribution, Integer> firstOrderContributions) {
        ThermodynamicFirstOrderContribution thermodynamicFirstOrderContribution = CamdRunner.CONTRIBUTION_GROUPS.getThermodynamicFirstOrderContributionsGroups().get(contributionGroup.getGroupCode());
        if (firstOrderContributions.containsKey(thermodynamicFirstOrderContribution))
            firstOrderContributions.replace(thermodynamicFirstOrderContribution, firstOrderContributions.get(thermodynamicFirstOrderContribution) + 1);
        else
            firstOrderContributions.put(thermodynamicFirstOrderContribution, 1);

        for (ContributionGroupNode subGroup : contributionGroup.getSubGroups())
            findFirstOrderGroups(subGroup, firstOrderContributions);
    }

    private void findSecondOrderGroups(ContributionGroupNode contributionGroup, Map<ThermodynamicSecondOrderContribution, Integer> secondOrderContributions) {
        Map<ContributionGroupNode, ThermodynamicSecondOrderContribution> branchSecondOrderContributions = CamdRunner.CONTRIBUTION_GROUPS.getThermodynamicFirstOrderContributionsGroups().get(contributionGroup.getGroupCode()).getSecondOrderContributions();
        for (Map.Entry<ContributionGroupNode, ThermodynamicSecondOrderContribution> contributionNodeEntry : branchSecondOrderContributions.entrySet()) {
            if (secondOrderContributions.containsKey(contributionNodeEntry.getValue()))
                continue;
            int occurrences = contributionGroup.contains(contributionNodeEntry.getKey());
            if (occurrences > 0)
                secondOrderContributions.put(contributionNodeEntry.getValue(), occurrences);
        }

        for (ContributionGroupNode subGroup : contributionGroup.getSubGroups())
            findSecondOrderGroups(subGroup, secondOrderContributions);
    }

    /**
     * this method create the molec until valence =0 (first restriction) or
     * the number of molecules is equal to maxNumof Groups allow
     */
    @Override
    public Molecule clone() {
        return new Molecule(this);
    }

    @Override
    public String toString() {
        String show = "";
        ArrayList<ContributionGroupNode> a = pickAllGroups();
        for (int i = 0; i < a.size(); i++) {
            if (i > 0)
                show += "-";
            show += CamdRunner.CONTRIBUTION_GROUPS.findGroupName(a.get(i).getGroupCode());
        }
        return show;
    }
}
