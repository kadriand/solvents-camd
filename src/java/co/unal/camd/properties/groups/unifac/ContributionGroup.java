package co.unal.camd.properties.groups.unifac;

import co.unal.camd.properties.groups.contributions.ThermoPhysicalFirstOrderContribution;
import co.unal.camd.properties.groups.contributions.ThermoPhysicalSecondOrderContribution;
import co.unal.camd.properties.model.ContributionGroupNode;
import lombok.Data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class ContributionGroup {
    private int code;
    private String groupName;
    private Integer valence = -1;
    private MainGroup mainGroup;
    private String smilesPattern;
    private boolean aliphaticContent;
    private int functionalElementsNumber;

    private Double rParam;
    private Double qParam;

    private ThermoPhysicalFirstOrderContribution thermoPhysicalFirstContribution = new ThermoPhysicalFirstOrderContribution();
    private Map<ContributionGroupNode, ThermoPhysicalSecondOrderContribution> secondOrderContributions = new HashMap<>();

    public ContributionGroup(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Group contributions of %s [%d] : mainGroups:%s , rParam:%.4f , qParam:%.4f , valence:%d",
                groupName, code, mainGroup, rParam, qParam, valence);
    }

}
