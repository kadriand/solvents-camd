package co.unal.camd.properties.model;

import co.unal.camd.availability.CompoundEntry;
import co.unal.camd.availability.CompoundSource;
import co.unal.camd.descriptors.CdkUtils;
import co.unal.camd.properties.environmental.AqueousSolubilityLogWs;
import co.unal.camd.properties.environmental.BioConcentrationFactorBFC;
import co.unal.camd.properties.environmental.DaphniaMagnaLC50DM;
import co.unal.camd.properties.environmental.FatheadMinnowLC50FM;
import co.unal.camd.properties.environmental.OralRatLD50;
import co.unal.camd.properties.environmental.RuralAirEmissionCarcinERAC;
import co.unal.camd.properties.environmental.RuralAirEmissionNonCarcinERANC;
import co.unal.camd.properties.environmental.UrbanAirEmissionCarcinEUAC;
import co.unal.camd.properties.environmental.UrbanAirEmissionNonCarcinEUANC;
import co.unal.camd.properties.groups.contributions.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.groups.contributions.EnvironmentalSecondOrderContribution;
import co.unal.camd.properties.groups.contributions.ThermoPhysicalSecondOrderContribution;
import co.unal.camd.properties.groups.unifac.ContributionGroup;
import co.unal.camd.properties.methods.BoilingPoint;
import co.unal.camd.properties.methods.Density;
import co.unal.camd.properties.methods.DielectricConstant;
import co.unal.camd.properties.methods.GibbsEnergy;
import co.unal.camd.properties.methods.MeltingPoint;
import co.unal.camd.properties.methods.MolecularWeight;
import co.unal.camd.view.CamdRunner;
import com.co.evolution.model.individual.IndividualImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this create ramdom molecules
 *
 * @author Kevin Adrián Rodríguez Ruiz
 */
@Accessors(chain = true)
public class Molecule extends IndividualImpl<Molecule> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Molecule.class);

    private ContributionGroupNode rootContributionGroup;

    private Integer functionalGroupsCount;

    private String smiles;

    @Getter
    private Double composition;

    private ThermoPhysicalProperties thermoPhysicalProperties;

    private EnvironmentalProperties environmentalProperties;

    @Getter
    @Setter
    private MixtureProperties mixtureProperties;

    private Map<ContributionGroup, Integer> firstOrderContributions;

    private Map<ThermoPhysicalSecondOrderContribution, Integer> secondOrderContributions;

    private Map<EnvironmentalFirstOrderContribution, Integer> environmentalFirstOrderContributions;

    private Map<EnvironmentalSecondOrderContribution, Integer> environmentalSecondOrderContributions;

    @Getter
    private List<CompoundEntry> availabilityEntries;

    @Getter
    @Setter
    private boolean suitable = true;

    public Molecule(Molecule molecule) {
        this.setFitness(molecule.getFitness());
        this.rootContributionGroup = molecule.rootContributionGroup.clone();
        this.composition = molecule.composition;
    }

    public Molecule(ContributionGroupNode rootContributionGroup) {
        rootContributionGroup.reorderGroupTree();
        this.rootContributionGroup = rootContributionGroup;
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
        this.smiles = null;
        this.functionalGroupsCount = null;
        return this.rootContributionGroup;
    }

    public ThermoPhysicalProperties getThermoPhysicalProperties() {
        if (thermoPhysicalProperties == null) {
            thermoPhysicalProperties = new ThermoPhysicalProperties();
            double gibbsEnergy = GibbsEnergy.compute(this);
            double boilingPoint = BoilingPoint.compute(this);
            double meltingPoint = MeltingPoint.compute(this);
            double molecularWeight = MolecularWeight.compute(this);
            double density = Density.compute(this);
            DielectricConstant dielectricConstant = new DielectricConstant(this);
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
            double waterLogWS = AqueousSolubilityLogWs.compute(this);
            double fatheadMinnowLC50FM = FatheadMinnowLC50FM.compute(this);
            double daphniaMagnaLC50DM = DaphniaMagnaLC50DM.compute(this);
            double waterBFC = BioConcentrationFactorBFC.compute(this);
            double oralRatLD50 = OralRatLD50.compute(this);
            double urbanEUAC = UrbanAirEmissionCarcinEUAC.compute(this);
            double urbanEUANC = UrbanAirEmissionNonCarcinEUANC.compute(this);
            double ruralERAC = RuralAirEmissionCarcinERAC.compute(this);
            double ruralERANC = RuralAirEmissionNonCarcinERANC.compute(this);

            environmentalProperties
                    .setWaterLogWS(waterLogWS)
                    .setWaterLC50FM(fatheadMinnowLC50FM)
                    .setWaterLC50DM(daphniaMagnaLC50DM)
                    .setRatLD50(waterBFC)
                    .setWaterBFC(oralRatLD50)
                    .setAirEUAc(urbanEUAC)
                    .setAirEUAnc(urbanEUANC)
                    .setAirERAc(ruralERAC)
                    .setAirERAnc(ruralERANC);
        }
        return environmentalProperties;
    }

    public int getSize() {
        int size = rootContributionGroup.countSubgroupsDownStream();
        return size;
    }

    public Integer getFunctionalElementsCount() {
        if (this.functionalGroupsCount == null)
            this.functionalGroupsCount = rootContributionGroup.countFunctionalElements();
        return functionalGroupsCount;
    }

    public String getSmiles() {
        if (this.smiles == null) {
            String smiles = rootContributionGroup.buildSmiles();
            String uniqueSmiles = CdkUtils.smilesToUnique(smiles);
            // LOGGER.info(String.format("SMILES {} vs {}", smiles, uniqueSmiles));
            this.smiles = uniqueSmiles;
        }
        return smiles;
    }

    /**
     * Return a map with the present ContributionGroup's and the occurrences of each one
     *
     * @return <ContributionGroup, occurrences>
     */
    public Map<ContributionGroup, Integer> getFirstOrderContributions() {
        if (firstOrderContributions == null) {
            Map<ContributionGroup, Integer> firstOrderContribution = new HashMap<>();
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
     * Return a map with the present ContributionGroup's and the occurrences of each one
     *
     * @return <ContributionGroup, occurrences>
     */
    public Map<EnvironmentalFirstOrderContribution, Integer> getEnvironmentalFirstOrderContributions() {
        if (environmentalFirstOrderContributions == null) {
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

    public ArrayList<ContributionGroupNode> pickAllGroups() {
        return rootContributionGroup.collectAllTreeGroups();
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

    private void findFirstOrderGroups(ContributionGroupNode contributionGroup, Map<ContributionGroup, Integer> firstOrderContributions) {
        ContributionGroup firstOrderContribution = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().get(contributionGroup.getGroupCode());
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

    public String unifacCodes() {
        StringBuilder show = new StringBuilder();
        ArrayList<ContributionGroupNode> pickedGroups = pickAllGroups();
        for (ContributionGroupNode pickedGroup : pickedGroups) {
            if (show.length() > 0)
                show.append(",");
            show.append(pickedGroup.getGroupCode());
        }

        return show.toString();
    }

    @Override
    public double getPenalization() {
        this.availabilityEntries = CamdRunner.AVAILABILITY_FINDER.findCompound(this);

        boolean epaPresence = availabilityEntries.stream().anyMatch(compoundEntry -> CompoundSource.EPA == compoundEntry.getSource());
        boolean waitOkPresence = availabilityEntries.stream().anyMatch(compoundEntry -> CompoundSource.ZINC_WAITOK == compoundEntry.getSource());
        if (epaPresence || waitOkPresence)
            return 0.0;
        boolean boutiqueOkPresence = availabilityEntries.stream().anyMatch(compoundEntry -> CompoundSource.ZINC_BOUTIQUE == compoundEntry.getSource());
        if (boutiqueOkPresence)
            return 0.5;
        boolean annotatedPresence = availabilityEntries.stream().anyMatch(compoundEntry -> CompoundSource.ZINC_ANNOTATED == compoundEntry.getSource());
        if (annotatedPresence)
            return 1.0;
        return 2.0;
    }

    @Override
    public String toString() {
        StringBuilder show = new StringBuilder();
        ArrayList<ContributionGroupNode> pickedGroups = pickAllGroups();
        for (ContributionGroupNode pickedGroup : pickedGroups) {
            if (show.length() > 0)
                show.append(",");
            show.append(pickedGroup.getGroupCode());
        }
        return show.toString();
    }

}
