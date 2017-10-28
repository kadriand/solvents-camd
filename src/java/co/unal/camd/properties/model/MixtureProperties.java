package co.unal.camd.properties.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MixtureProperties {
    private double ks;
    private double solventLoss;

    public MixtureProperties(double ks, double solventLoss) {
        this.ks = ks;
        this.solventLoss = solventLoss;
    }
}
