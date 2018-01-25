package co.unal.camd.properties.parameters.unifac;

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
    private Double boilingPoint;
    private Double meltingPoint;
    private Double gibbsEnergy;
    private Double liquidMolarVolume;
    @Setter(AccessLevel.NONE)
    private List<Integer[]> groupsConfigurations = new ArrayList<>();

    public ThermodynamicSecondOrderContribution(int groupsCase) {
        this.groupsCase = groupsCase;
    }

    @Override
    public String toString() {
        StringBuilder configurations = new StringBuilder("");
        groupsConfigurations.stream().forEach(groups -> configurations.append(Arrays.toString(groups) + " "));
        return String.format(Locale.ROOT, "Second order parameters [%d] : boilingPoint:%f ,meltingPoint:%f ,gibbsEnergy:%f ,liquidMolarVolume:%f, groups: %s", groupsCase, boilingPoint, meltingPoint, gibbsEnergy, liquidMolarVolume, configurations);
    }
}