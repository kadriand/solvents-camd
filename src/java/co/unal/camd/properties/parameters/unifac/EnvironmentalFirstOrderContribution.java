package co.unal.camd.properties.parameters.unifac;

import co.unal.camd.properties.model.ContributionGroupNode;
import lombok.Data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class EnvironmentalFirstOrderContribution {
    private int code;
    private String groupName;

    private Double waterLC50FM;
    private Double waterLC50DM;
    private Double oralLD50;
    private Double waterLogWS;
    private Double waterBFC;

    private Double airEUAc;
    private Double airEUAnc;
    private Double airERAc;
    private Double airERAnc;

    public EnvironmentalFirstOrderContribution(int code) {
        this.code = code;
    }

    /**
     * <UNIFAC codes Contributions nodes , EnvironmentalSecondOrderContribution containing this EnvironmentalFirstOrderContribution >
     */
    private Map<ContributionGroupNode, EnvironmentalSecondOrderContribution> secondOrderContributions = new HashMap<>();

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Group contributions of %s [%d] : waterLC50FM : %f , waterLC50DM : %f , oralLD50 : %f , waterLogWS : %f , waterBFC : %f , airERAc : %f , airERAnc : %f , airEUAc : %f , airEUAnc : %f",
                groupName, code, waterLC50FM, waterLC50DM, oralLD50, waterLogWS, waterBFC, airERAc, airERAnc, airEUAc, airEUAnc);
    }

}
