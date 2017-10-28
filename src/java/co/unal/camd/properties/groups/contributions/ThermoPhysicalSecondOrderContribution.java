package co.unal.camd.properties.groups.contributions;

import co.unal.camd.properties.model.ContributionGroupNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
public class ThermoPhysicalSecondOrderContribution {

    private int groupsCase;
    private String groupsDescription;

    private Double boilingPoint;
    private Double meltingPoint;
    private Double gibbsEnergy;
    private Double liquidMolarVolume;

    private List<ContributionGroupNode> groupConfigurations = new ArrayList<>();

    public ThermoPhysicalSecondOrderContribution(int groupsCase) {
        this.groupsCase = groupsCase;
    }

    @Override
    public String toString() {
        StringBuilder configurations = new StringBuilder("");
        return String.format(Locale.ROOT, "Second order groups %d [%s] - : boilingPoint:%f ,meltingPoint:%f ,gibbsEnergy:%f ,liquidMolarVolume:%f, groups: %s",
                groupsCase, groupsDescription, boilingPoint, meltingPoint, gibbsEnergy, liquidMolarVolume, configurations);
    }
}