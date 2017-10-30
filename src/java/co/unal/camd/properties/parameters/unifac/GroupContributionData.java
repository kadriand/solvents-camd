package co.unal.camd.properties.parameters.unifac;

import lombok.Data;

import java.util.Locale;

@Data
public class GroupContributionData {
    private Integer groupId;
    private Integer mainGroup;
    private String groupName;
    private Double rParam;
    private Double qParam;

    private Integer valence;
    private Double molecularWeight;
    private Double boilingPoint;
    private Double freeEnergy;
    private Double meltingPoint;
    private Double dipoleMoment;
    private Double dipoleMomentH1i;
    private Double liquidMolarVolume;
    private Double densityAd1;
    private Double densityBd1;
    private Double densityCd1;
    // TODO CHECK IF NECESSARY
    private Double densityAd2;
    private Double densityBd2;
    private Double densityCd2;
    private Double densityAd3;
    private Double densityBd3;
    private Double densityCd3;
    private Double densityAd4;
    private Double densityBd4;
    private Double densityCd4;

    public GroupContributionData(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Group contributions of %s [%d] : mainGroup:%d , rParam:%.4f , qParam:%.4f valence:%d , molecularWeight:%f , boilingPoint:%f , freeEnergy:%f , meltingPoint:%f , dipoleMoment:%f , dipoleMomentH1i:%f , liquidMolarVolume:%f , densityAd1:%f , densityBd1:%f , densityCd1:%f",
                groupName, groupId, mainGroup, rParam, qParam, valence, molecularWeight, boilingPoint, freeEnergy, meltingPoint, dipoleMoment, dipoleMomentH1i, liquidMolarVolume, densityAd1, densityBd1, densityCd1);
    }

}
