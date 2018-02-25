package co.unal.camd.properties.model;

import co.unal.camd.properties.methods.BoilingPoint;
import co.unal.camd.properties.methods.Density;
import co.unal.camd.properties.methods.DielectricConstant;
import co.unal.camd.properties.methods.Environmental;
import co.unal.camd.properties.methods.GibbsEnergy;
import co.unal.camd.properties.methods.MeltingPoint;
import co.unal.camd.properties.methods.MolecularWeight;
import co.unal.camd.properties.parameters.unifac.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.EnvironmentalSecondOrderContribution;
import co.unal.camd.properties.parameters.unifac.ThermoPhysicalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.ThermoPhysicalSecondOrderContribution;
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
    private double temperature = -1;

    @Getter
    private double x; //composición

    private ThermoPhysicalProperties thermoPhysicalProperties;

    private EnvironmentalProperties environmentalProperties;

    @Getter
    @Setter
    private MixtureProperties mixtureProperties;

    private Map<ThermoPhysicalFirstOrderContribution, Integer> firstOrderContributions;

    private Map<ThermoPhysicalSecondOrderContribution, Integer> secondOrderContributions;

    private Map<EnvironmentalFirstOrderContribution, Integer> environmentalFirstOrderContributions;

    private Map<EnvironmentalSecondOrderContribution, Integer> environmentalSecondOrderContributions;

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
        this.environmentalFirstOrderContributions = null;
        this.environmentalSecondOrderContributions = null;
        this.thermoPhysicalProperties = null;
        this.mixtureProperties = null;
        this.size = -1;
        return this.rootContributionGroup;
    }

    public ThermoPhysicalProperties getThermoPhysicalProperties() {
        if (thermoPhysicalProperties == null) {
            thermoPhysicalProperties = new ThermoPhysicalProperties();
            double gibbsEnergy = GibbsEnergy.compute(this);
            double boilingPoint = BoilingPoint.compute(this);
            double meltingPoint = MeltingPoint.compute(this);
            double molecularWeight = MolecularWeight.compute(this);
            double density = Density.compute(this, temperature);
            DielectricConstant dielectricConstant = new DielectricConstant(this, temperature);
            thermoPhysicalProperties.
                    setGibbsEnergy(gibbsEnergy)
                    .setBoilingPoint(boilingPoint)
                    .setDensity(density)
                    .setMeltingPoint(meltingPoint)
                    .setDielectricConstant(dielectricConstant.compute())
                    .setMolecularWeight(molecularWeight);
        }
        return thermoPhysicalProperties;
    }

    public EnvironmentalProperties getEnvironmentalProperties() {
        if (environmentalProperties == null) {
            environmentalProperties = new EnvironmentalProperties();
            double waterLogWS = Environmental.BioConcentrationFactor.compute(this);
            environmentalProperties.
                    setWaterLogWS(waterLogWS);
        }
        return environmentalProperties;
    }

    public int getSize() {
        if (this.size == -1)
            this.size = rootContributionGroup.countTotalGroups();
        return size;
    }

    /**
     * Return a map with the present ThermoPhysicalFirstOrderContribution's and the occurrences of each one
     *
     * @return <ThermoPhysicalFirstOrderContribution, occurrences>
     */
    public Map<ThermoPhysicalFirstOrderContribution, Integer> getFirstOrderContributions() {
        if (firstOrderContributions == null) {
            Map<ThermoPhysicalFirstOrderContribution, Integer> firstOrderContribution = new HashMap<>();
            findFirstOrderGroups(this.rootContributionGroup, firstOrderContribution);
            this.firstOrderContributions = firstOrderContribution;
        }
        return firstOrderContributions;
    }

    /**
     * Return a map with the present ThermoPhysicalSecondOrderContribution's and the occurrences of each one
     *
     * @return <ThermoPhysicalSecondOrderContribution, occurrences>
     */
    public Map<ThermoPhysicalSecondOrderContribution, Integer> getSecondOrderContributions() {
        if (secondOrderContributions == null) {
            Map<ThermoPhysicalSecondOrderContribution, Integer> secondOrderContributions = new HashMap<>();
            findSecondOrderGroups(this.rootContributionGroup, secondOrderContributions);
            this.secondOrderContributions = secondOrderContributions;
        }
        return secondOrderContributions;
    }

    /**
     * Return a map with the present ThermoPhysicalFirstOrderContribution's and the occurrences of each one
     *
     * @return <ThermoPhysicalFirstOrderContribution, occurrences>
     */
    public Map<EnvironmentalFirstOrderContribution, Integer> getEnvironmentalFirstOrderContributions() {
        if (firstOrderContributions == null) {
            Map<EnvironmentalFirstOrderContribution, Integer> firstOrderContribution = new HashMap<>();
            findEnvironmentalFirstOrderGroups(this.rootContributionGroup, firstOrderContribution);
            this.environmentalFirstOrderContributions = firstOrderContribution;
        }
        return environmentalFirstOrderContributions;
    }

    /**
     * Return a map with the present ThermoPhysicalSecondOrderContribution's and the occurrences of each one
     *
     * @return <ThermoPhysicalSecondOrderContribution, occurrences>
     */
    public Map<EnvironmentalSecondOrderContribution, Integer> getEnvironmentalSecondOrderContributions() {
        if (environmentalSecondOrderContributions == null) {
            Map<EnvironmentalSecondOrderContribution, Integer> secondOrderContributions = new HashMap<>();
            findEnvironmentalSecondOrderGroups(this.rootContributionGroup, secondOrderContributions);
            this.environmentalSecondOrderContributions = secondOrderContributions;
        }
        return environmentalSecondOrderContributions;
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

    private void findFirstOrderGroups(ContributionGroupNode contributionGroup, Map<ThermoPhysicalFirstOrderContribution, Integer> firstOrderContributions) {
        ThermoPhysicalFirstOrderContribution firstOrderContribution = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(contributionGroup.getGroupCode());
        if (firstOrderContributions.containsKey(firstOrderContribution))
            firstOrderContributions.replace(firstOrderContribution, firstOrderContributions.get(firstOrderContribution) + 1);
        else
            firstOrderContributions.put(firstOrderContribution, 1);

        for (ContributionGroupNode subGroup : contributionGroup.getSubGroups())
            findFirstOrderGroups(subGroup, firstOrderContributions);
    }

    private void findSecondOrderGroups(ContributionGroupNode contributionGroup, Map<ThermoPhysicalSecondOrderContribution, Integer> secondOrderContributions) {
        Map<ContributionGroupNode, ThermoPhysicalSecondOrderContribution> branchSecondOrderContributions = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(contributionGroup.getGroupCode()).getSecondOrderContributions();
        for (Map.Entry<ContributionGroupNode, ThermoPhysicalSecondOrderContribution> contributionNodeEntry : branchSecondOrderContributions.entrySet()) {
            if (secondOrderContributions.containsKey(contributionNodeEntry.getValue()))
                continue;
            int occurrences = contributionGroup.contains(contributionNodeEntry.getKey());
            if (occurrences > 0)
                secondOrderContributions.put(contributionNodeEntry.getValue(), occurrences);
        }

        for (ContributionGroupNode subGroup : contributionGroup.getSubGroups())
            findSecondOrderGroups(subGroup, secondOrderContributions);
    }

    private void findEnvironmentalFirstOrderGroups(ContributionGroupNode contributionGroup, Map<EnvironmentalFirstOrderContribution, Integer> firstOrderContributions) {
        Integer hukkerikarCode = CamdRunner.CONTRIBUTION_GROUPS.getHukkerikarGroupsEquivalences().get(contributionGroup.getGroupCode());
        EnvironmentalFirstOrderContribution firstOrderContribution = CamdRunner.CONTRIBUTION_GROUPS.getEnvironmentalFirstOrderContributions().get(hukkerikarCode);
        if (firstOrderContributions.containsKey(firstOrderContribution))
            firstOrderContributions.replace(firstOrderContribution, firstOrderContributions.get(firstOrderContribution) + 1);
        else
            firstOrderContributions.put(firstOrderContribution, 1);

        for (ContributionGroupNode subGroup : contributionGroup.getSubGroups())
            findEnvironmentalFirstOrderGroups(subGroup, firstOrderContributions);
    }

    private void findEnvironmentalSecondOrderGroups(ContributionGroupNode contributionGroup, Map<EnvironmentalSecondOrderContribution, Integer> secondOrderContributions) {
        Integer hukkerikarCode = CamdRunner.CONTRIBUTION_GROUPS.getHukkerikarGroupsEquivalences().get(contributionGroup.getGroupCode());
        Map<ContributionGroupNode, EnvironmentalSecondOrderContribution> branchSecondOrderContributions = CamdRunner.CONTRIBUTION_GROUPS.getEnvironmentalFirstOrderContributions().get(hukkerikarCode).getSecondOrderContributions();
        for (Map.Entry<ContributionGroupNode, EnvironmentalSecondOrderContribution> contributionNodeEntry : branchSecondOrderContributions.entrySet()) {
            if (secondOrderContributions.containsKey(contributionNodeEntry.getValue()))
                continue;
            int occurrences = contributionGroup.contains(contributionNodeEntry.getKey());
            if (occurrences > 0)
                secondOrderContributions.put(contributionNodeEntry.getValue(), occurrences);
        }

        for (ContributionGroupNode subGroup : contributionGroup.getSubGroups())
            findEnvironmentalSecondOrderGroups(subGroup, secondOrderContributions);
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
