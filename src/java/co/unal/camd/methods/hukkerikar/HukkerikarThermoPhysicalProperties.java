package co.unal.camd.methods.hukkerikar;

import co.unal.camd.methods.hukkerikar.thermophysical.BoilingPoint;
import co.unal.camd.methods.hukkerikar.thermophysical.GibbsEnergy;
import co.unal.camd.methods.hukkerikar.thermophysical.LiquidMolarVolume;
import co.unal.camd.methods.hukkerikar.thermophysical.MeltingPoint;
import co.unal.camd.model.ThermoPhysicalProperties;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(chain = true)
public class HukkerikarThermoPhysicalProperties extends ThermoPhysicalProperties {

    @Getter
    private double molecularWeight;

    @Getter
    private Map<HukkerikarFirstOrderContribution, Integer> firstOrderContributions;

    @Getter
    private Map<HukkerikarSecondOrderContribution, Integer> secondOrderContributions;

    public HukkerikarThermoPhysicalProperties(Molecule molecule) {
        UnifacGroupNode rootContributionGroup = molecule.readRootContributionGroup();
        this.firstOrderContributions = HukkerikarProperties.findFirstOrderGroups(rootContributionGroup);
        this.secondOrderContributions = HukkerikarProperties.findSecondOrderGroups(rootContributionGroup);
        this.molecularWeight = molecule.getMolecularWeight();
        this.groupsSummary = resumeGroups();
    }

    public void compute() {
        this.boilingPoint = BoilingPoint.compute(this);
        this.meltingPoint = MeltingPoint.compute(this);
        this.gibbsEnergy = GibbsEnergy.compute(this);
        this.liquidMolarVolume = LiquidMolarVolume.compute(this);
        this.density = this.molecularWeight / this.liquidMolarVolume / 1000;
    }

    private String resumeGroups() {
        StringBuilder result = new StringBuilder();
        this.firstOrderContributions.forEach((contributionGroup, occurrences) -> result.append(String.format("%sx%s ", contributionGroup.getUnifacCode(), occurrences)));
        result.append("- ");
        this.firstOrderContributions.forEach((contributionGroup, occurrences) -> result.append(String.format("%sx%s ", contributionGroup.getCode(), occurrences)));
        if (this.secondOrderContributions.size() > 0) {
            result.append("[");
            this.secondOrderContributions.forEach((contributionGroup, occurrences) -> result.append(String.format("%sx%s ", contributionGroup.getCode(), occurrences)));
            result.append("]");
        }
        return result.toString().replaceAll("x1|\\s$", "").replaceFirst("\\s]", "]");
    }

}
