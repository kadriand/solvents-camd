package co.unal.camd.model.molecule;

import co.unal.camd.availability.CompoundEntry;
import co.unal.camd.methods.hukkerikar.HukkerikarEnvironmentalProperties;
import co.unal.camd.methods.hukkerikar.HukkerikarThermoPhysicalProperties;
import co.unal.camd.methods.unifac.MixtureProperties;
import co.unal.camd.model.EnvironmentalProperties;
import co.unal.camd.model.ThermoPhysicalProperties;
import co.unal.camd.utils.CdkUtils;
import co.unal.camd.view.CamdRunner;
import com.co.evolution.model.individual.IndividualImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * this create ramdom molecules
 *
 * @author Kevin Adrián Rodríguez Ruiz
 */
@Accessors(chain = true)
public class Molecule extends IndividualImpl<Molecule> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Molecule.class);

    private UnifacGroupNode rootContributionGroup;

    private Integer functionalGroupsCount;

    private Double molecularWeight;

    private String groupsText;

    private String smiles;

    private String inChIKey;

    @Getter
    @Setter
    private double composition;

    private ThermoPhysicalProperties thermoPhysicalProperties;

    private EnvironmentalProperties environmentalProperties;

    private MixtureProperties mixtureProperties;

    private ArrayList<UnifacGroupNode> allGroupsPicked;

    private List<CompoundEntry> availabilityEntries;

    @Getter
    @Setter
    private boolean suitable = true;

    public Molecule(Molecule molecule) {
        this.setFitness(molecule.getFitness());
        this.rootContributionGroup = molecule.rootContributionGroup.clone();
        this.composition = molecule.composition;
    }

    public Molecule(UnifacGroupNode rootContributionGroup) {
        rootContributionGroup.reorderGroupTree();
        this.rootContributionGroup = rootContributionGroup;
    }

    /**
     * Every time the rootContributionGroup is accessed, the second order groups get emptied
     *
     * @return
     */
    public UnifacGroupNode getRootContributionGroup() {
        this.thermoPhysicalProperties = null;
        this.environmentalProperties = null;
        this.mixtureProperties = null;
        this.availabilityEntries = null;
        this.smiles = null;
        this.molecularWeight = null;
        this.functionalGroupsCount = null;
        this.allGroupsPicked = null;
        return this.rootContributionGroup;
    }

    public UnifacGroupNode readRootContributionGroup() {
        return this.rootContributionGroup;
    }

    public ThermoPhysicalProperties getThermoPhysicalProperties() {
        if (thermoPhysicalProperties == null) {
            // thermoPhysicalProperties = new GaniThermoPhysicalProperties(this);
            thermoPhysicalProperties = new HukkerikarThermoPhysicalProperties(this);
            thermoPhysicalProperties.compute();
        }
        return thermoPhysicalProperties;
    }

    public EnvironmentalProperties getEnvironmentalProperties() {
        if (environmentalProperties == null) {
            environmentalProperties = new HukkerikarEnvironmentalProperties(this);
            environmentalProperties.compute();
        }
        return environmentalProperties;
    }

    public MixtureProperties getMixtureProperties() {
        if (mixtureProperties == null) {
            mixtureProperties = new MixtureProperties(this);
        }
        return mixtureProperties;
    }

    public List<CompoundEntry> getAvailabilityEntries() {
        if (availabilityEntries == null && CamdRunner.AVAILABILITY_FINDER != null)
            availabilityEntries = CamdRunner.AVAILABILITY_FINDER.findCompound(this);
        return availabilityEntries;
    }

    public int getSize() {
        int size = rootContributionGroup.countSubgroupsDownStream();
        return size;
    }

    public Integer getStrongGroupsCount() {
        if (this.functionalGroupsCount == null)
            this.functionalGroupsCount = rootContributionGroup.countStrongGroups();
        return functionalGroupsCount;
    }

    public Double getMolecularWeight() {
        if (this.molecularWeight == null)
            this.molecularWeight = this.pickAllGroups().stream().mapToDouble(group -> group.getUnifacSubGroup().getMolecularWeight()).sum();
        return molecularWeight;
    }

    public String getGroupsText() {
        if (this.groupsText == null) {
            StringBuilder result = new StringBuilder("[");
            this.pickAllGroups().forEach(groupNode -> result.append(groupNode.getUnifacSubGroup().getCode()).append(" "));
            this.groupsText = result.append("]").toString().replaceFirst("\\s]", "]");
        }
        return groupsText;
    }

    public String getSmiles() {
        if (this.smiles == null) {
            String smiles = rootContributionGroup.buildSmiles();
            String uniqueSmiles = CdkUtils.smilesToUnique(smiles);
            // LOGGER.info(String.format("SMILES {} vs {}", smiles, uniqueSmiles));
            this.smiles = uniqueSmiles;
        }
        return this.smiles;
    }

    public String getInChIKey() {
        if (this.inChIKey == null) {
            String smiles = rootContributionGroup.buildSmiles();
            String inChIKey = CdkUtils.smilesToInChIKey(smiles);
            this.inChIKey = inChIKey;
        }
        return this.inChIKey;
    }

    public ArrayList<UnifacGroupNode> pickAllGroups() {
        if (this.allGroupsPicked == null)
            this.allGroupsPicked = rootContributionGroup.collectAllTreeGroups();
        return this.allGroupsPicked;
    }

    @Override
    public Molecule clone() {
        return new Molecule(this);
    }

    @Override
    public String toString() {
        try {
            return this.getSmiles();
        } catch (Exception e) {
            return this.getGroupsText();
        }
    }

}
