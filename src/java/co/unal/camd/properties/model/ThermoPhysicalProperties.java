package co.unal.camd.properties.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ThermoPhysicalProperties {
    private double gibbsEnergy;
    private double boilingPoint;
    private double density;
    private double meltingPoint;
    private double dielectricConstant;
    private double molecularWeight;
}
