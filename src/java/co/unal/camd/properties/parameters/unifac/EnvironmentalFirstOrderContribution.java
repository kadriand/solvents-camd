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

    private double waterLC50FM;
    private double waterLC50DM;
    private double oralLD50;
    private double waterLogWS;
    private double waterBFC;

    private double airEUAc;
    private double airEUAnc;
    private double airERAc;
    private double airERAnc;

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
