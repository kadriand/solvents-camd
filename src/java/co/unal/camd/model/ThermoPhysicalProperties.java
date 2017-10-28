package co.unal.camd.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class ThermoPhysicalProperties {

    protected double gibbsEnergy;
    protected double boilingPoint;
    protected double meltingPoint;
    protected double liquidMolarVolume;
    protected double density;

    protected String groupsSummary;

    public abstract void compute();
}
