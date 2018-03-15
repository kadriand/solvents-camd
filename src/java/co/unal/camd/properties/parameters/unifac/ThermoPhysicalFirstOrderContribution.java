package co.unal.camd.properties.parameters.unifac;

import co.unal.camd.properties.model.ContributionGroupNode;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class ThermoPhysicalFirstOrderContribution {
    private int code;
    private ContributionGroup.Main mainGroup;
    private String groupName;
    private String smilesPattern;

    private Double rParam;
    private Double qParam;

    private Integer valence;
    private Double molecularWeight;
    private Double boilingPoint;
    private Double gibbsFreeEnergy;
    private Double meltingPoint;
    private Double dipoleMoment;
    private Double dipoleMomentH1i;
    private Double liquidMolarVolume;
    // TODO CHECK IF THE FOUR ARE NECESSARY
    private Double[] densityA = new Double[4];
    private Double[] densityB = new Double[4];
    private Double[] densityC = new Double[4];

    private Map<ContributionGroupNode, ThermoPhysicalSecondOrderContribution> secondOrderContributions = new HashMap<>();

    public ThermoPhysicalFirstOrderContribution(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Group contributions of %s [%d] : mainGroup:%s , rParam:%.4f , qParam:%.4f , valence:%d , molecularWeight:%f , boilingPoint:%f , gibbsFreeEnergy:%f , meltingPoint:%f , dipoleMoment:%f , dipoleMomentH1i:%f , liquidMolarVolume:%f , densityA:%s , densityB:%s , densityC:%s",
                groupName, code, mainGroup, rParam, qParam, valence, molecularWeight, boilingPoint, gibbsFreeEnergy, meltingPoint, dipoleMoment, dipoleMomentH1i, liquidMolarVolume, Arrays.toString(densityA), Arrays.toString(densityB), Arrays.toString(densityC));
    }

}
