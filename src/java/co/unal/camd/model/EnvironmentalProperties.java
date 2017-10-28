package co.unal.camd.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class EnvironmentalProperties {
    protected double waterLogLC50FM;
    protected double waterLogLC50DM;
    protected double ratLogLD50;
    protected double waterLogWS;
    protected double waterLogBFC;

    protected double airUrbanEUAc;
    protected double airUrbanEUAnc;
    protected double airRuralERAc;
    protected double airRuralERAnc;

    protected String groupsSummary;

    public abstract void compute();
}
