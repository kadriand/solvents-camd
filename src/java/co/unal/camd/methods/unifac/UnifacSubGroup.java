package co.unal.camd.methods.unifac;

import lombok.Data;

import java.util.Locale;

@Data
public class UnifacSubGroup {
    private int code;
    private String groupName;
    private int valence;
    private double molecularWeight;
    private UnifacMainGroup mainGroup;
    private String smilesPattern;
    private boolean aliphaticContent;
    private int strongGroupsNumber;

    private Double rParam;
    private Double qParam;

    //    private GaniThermoPhysicalFirstOrderContribution thermoPhysicalFirstContribution = new GaniThermoPhysicalFirstOrderContribution();

    public UnifacSubGroup(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Group contributions of %s [%d] : mainGroups:%s , rParam:%.4f , qParam:%.4f , valence:%d",
                groupName, code, mainGroup, rParam, qParam, valence);
    }

}
