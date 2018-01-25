package co.unal.camd.properties.parameters.unifac;

import lombok.Data;

import java.util.Locale;

@Data
public class EnvironmentalSecondOrderContribution {
    private int code;
    private ContributionGroup.Main mainGroup;
    private String groupDescription;

    private Double waterLC50FM;
    private Double waterLC50DM;
    private Double oralLD50;
    private Double waterLogWS;
    private Double waterBFC;

    private Double airEUAc;
    private Double airEUAnc;
    private Double airERAc;
    private Double airERAnc;


    public EnvironmentalSecondOrderContribution(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Group contributions of %s [%d] : mainGroup : %s , waterLC50FM : %f , waterLC50DM : %f , oralLD50 : %f , waterLogWS : %f , waterBFC : %f , airERAc : %f , airERAnc : %f , airEUAc : %f , airEUAnc : %f",
                groupDescription, code, mainGroup, waterLC50FM, waterLC50DM, oralLD50, waterLogWS, waterBFC, airERAc, airERAnc, airEUAc, airEUAnc);
    }

}
