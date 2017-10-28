package co.unal.camd.methods.hukkerikar;

import co.unal.camd.methods.hukkerikar.environmental.BioConcentrationFactorBFC;
import co.unal.camd.methods.hukkerikar.environmental.DaphniaMagnaLC50DM;
import co.unal.camd.methods.hukkerikar.environmental.FatheadMinnowLC50FM;
import co.unal.camd.methods.hukkerikar.environmental.OralRatLD50;
import co.unal.camd.methods.hukkerikar.environmental.RuralAirEmissionCarcinERAC;
import co.unal.camd.methods.hukkerikar.environmental.RuralAirEmissionNonCarcinERANC;
import co.unal.camd.methods.hukkerikar.environmental.UrbanAirEmissionCarcinEUAC;
import co.unal.camd.methods.hukkerikar.environmental.UrbanAirEmissionNonCarcinEUANC;
import co.unal.camd.methods.hukkerikar.environmental.WaterSolubilityWs;
import co.unal.camd.model.EnvironmentalProperties;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.model.molecule.Molecule;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(chain = true)
public class HukkerikarEnvironmentalProperties extends EnvironmentalProperties {

    @Getter
    private double molecularWeight;

    @Getter
    private Map<HukkerikarFirstOrderContribution, Integer> firstOrderContributions;

    @Getter
    private Map<HukkerikarSecondOrderContribution, Integer> secondOrderContributions;

    public HukkerikarEnvironmentalProperties(Molecule molecule) {
        UnifacGroupNode rootContributionGroup = molecule.readRootContributionGroup();
        this.firstOrderContributions = HukkerikarProperties.findFirstOrderGroups(rootContributionGroup);
        this.secondOrderContributions = HukkerikarProperties.findSecondOrderGroups(rootContributionGroup);
        this.molecularWeight = molecule.getMolecularWeight();
        this.groupsSummary = resumeGroups();
    }

    public void compute() {
        this.waterLogLC50FM = FatheadMinnowLC50FM.compute(this);
        this.waterLogLC50DM = DaphniaMagnaLC50DM.compute(this);
        this.ratLogLD50 = OralRatLD50.compute(this);
        this.waterLogWS = WaterSolubilityWs.compute(this) - Math.log10(this.molecularWeight) - 3;
        this.waterLogBFC = BioConcentrationFactorBFC.compute(this);
        this.airUrbanEUAc = UrbanAirEmissionCarcinEUAC.compute(this);
        this.airUrbanEUAnc = UrbanAirEmissionNonCarcinEUANC.compute(this);
        this.airRuralERAc = RuralAirEmissionCarcinERAC.compute(this);
        this.airRuralERAnc = RuralAirEmissionNonCarcinERANC.compute(this);
    }

    private String resumeGroups() {
        StringBuilder result = new StringBuilder();
        this.firstOrderContributions.forEach((contributionGroup, occurrences) -> result.append(String.format("%sx%s ", contributionGroup.getCode(), occurrences)));
        if (this.secondOrderContributions.size() > 0) {
            result.append("[");
            this.secondOrderContributions.forEach((contributionGroup, occurrences) -> result.append(String.format("%sx%s ", contributionGroup.getCode(), occurrences)));
            result.append("]");
        }
        return result.toString().replaceAll("x1|\\s$", "").replaceFirst("\\s]", "]");
    }
}
