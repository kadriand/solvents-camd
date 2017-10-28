package co.unal.camd.methods.gani;

import co.unal.camd.model.molecule.UnifacGroupNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
public class GaniThermoPhysicalSecondOrderContribution {

    private int code;
    private String groupsDescription;

    private Double boilingPoint;
    private Double meltingPoint;
    private Double gibbsEnergy;
    private Double liquidMolarVolume;

    private List<UnifacGroupNode> groupConfigurations = new ArrayList<>();

    public GaniThermoPhysicalSecondOrderContribution(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuilder configurations = new StringBuilder("");
        return String.format(Locale.ROOT, "Second order groups %d [%s] - : boilingPoint:%f ,meltingPoint:%f ,gibbsEnergy:%f ,liquidMolarVolume:%f, groups: %s",
                code, groupsDescription, boilingPoint, meltingPoint, gibbsEnergy, liquidMolarVolume, configurations);
    }
}