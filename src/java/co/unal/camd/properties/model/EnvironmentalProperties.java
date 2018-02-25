package co.unal.camd.properties.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EnvironmentalProperties {
    private double waterLC50FM;
    private double waterLC50DM;
    private double oralLD50;
    private double waterLogWS;
    private double waterBFC;

    private double airEUAc;
    private double airEUAnc;
    private double airERAc;
    private double airERAnc;
}
