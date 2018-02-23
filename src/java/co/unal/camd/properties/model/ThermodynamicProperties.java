package co.unal.camd.properties.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ThermodynamicProperties {
    private double gibbsEnergy;
    private double boilingPoint;
    private double density;
    private double meltingPoint;
    private double dielectricConstant;
    private double molecularWeight;
    private double ks;
    private double solventLoss;
}
