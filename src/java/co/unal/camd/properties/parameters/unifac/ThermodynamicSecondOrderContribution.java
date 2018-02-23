package co.unal.camd.properties.parameters.unifac;

import co.unal.camd.properties.model.ContributionGroupNode;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Data
public class ThermodynamicSecondOrderContribution {

    private int groupsCase;
    private String groupsDescription;
    private Double boilingPoint;
    private Double meltingPoint;
    private Double gibbsEnergy;
    private Double liquidMolarVolume;
    @Setter(AccessLevel.NONE)
    private List<int[]> rawGroupsConfigurations = new ArrayList<>();
    private List<ContributionGroupNode> groupConfigurations = new ArrayList<>();

    public ThermodynamicSecondOrderContribution(int groupsCase) {
        this.groupsCase = groupsCase;
    }

    @Override
    public String toString() {
        StringBuilder configurations = new StringBuilder("");
        rawGroupsConfigurations.stream().forEach(groups -> configurations.append(Arrays.toString(groups) + " "));
        return String.format(Locale.ROOT, "Second order parameters %d [%s] - : boilingPoint:%f ,meltingPoint:%f ,gibbsEnergy:%f ,liquidMolarVolume:%f, groups: %s", groupsCase, groupsDescription, boilingPoint, meltingPoint, gibbsEnergy, liquidMolarVolume, configurations);
    }
}