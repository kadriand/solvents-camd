package co.unal.camd.properties.groups.contributions;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Locale;

/***
 * {@link <a href="https://www.epa.gov/chemical-research/distributed-structure-searchable-toxicity-dsstox-database">'Learn more'</a>}
 *
 */
@Data
@Accessors(chain = true)
public class ThermoPhysicalFirstOrderContribution {

    private Double molecularWeight;
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

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Group contributions :  molecularWeight:%f , boilingPoint:%f , gibbsFreeEnergy:%f , meltingPoint:%f , dipoleMoment:%f , dipoleMomentH1i:%f , liquidMolarVolume:%f , densityA:%s , densityB:%s , densityC:%s",
                molecularWeight, boilingPoint, gibbsFreeEnergy, meltingPoint, dipoleMoment, dipoleMomentH1i, liquidMolarVolume, Arrays.toString(densityA), Arrays.toString(densityB), Arrays.toString(densityC));
    }
}
