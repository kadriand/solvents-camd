package co.unal.camd.methods.gani;

import co.unal.camd.methods.unifac.UnifacSubGroup;
import co.unal.camd.model.molecule.UnifacGroupNode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/***
 * {@link <a href="https://www.epa.gov/chemical-research/distributed-structure-searchable-toxicity-dsstox-database">'Learn more'</a>}
 *
 */
@Data
@Accessors(chain = true)
public class GaniThermoPhysicalFirstOrderContribution {
    private int code;
    private String groupName;

    private Double boilingPoint;
    private Double gibbsFreeEnergy;
    private Double meltingPoint;
    private Double dipoleMoment;
    private Double dipoleMomentH1i;
    private Double liquidMolarVolume;
    // TODO CHECK IF THE FOUR ARE NECESSARY
    private Double[] densityA = new Double[4];
    private Double[] densityB = new Double[4];
    private Double[] densityC = new Double[4];

    private Map<UnifacGroupNode, GaniThermoPhysicalSecondOrderContribution> secondOrderContributions = new HashMap<>();
    private UnifacSubGroup unifacSubGroup;

    public GaniThermoPhysicalFirstOrderContribution(UnifacSubGroup unifacSubGroup) {
        this.code = unifacSubGroup.getCode();
        this.groupName = unifacSubGroup.getGroupName();
        this.unifacSubGroup = unifacSubGroup;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Group contributions :  boilingPoint:%f , gibbsFreeEnergy:%f , meltingPoint:%f , dipoleMoment:%f , dipoleMomentH1i:%f , liquidMolarVolume:%f , densityA:%s , densityB:%s , densityC:%s",
                boilingPoint, gibbsFreeEnergy, meltingPoint, dipoleMoment, dipoleMomentH1i, liquidMolarVolume, Arrays.toString(densityA), Arrays.toString(densityB), Arrays.toString(densityC));
    }
}
