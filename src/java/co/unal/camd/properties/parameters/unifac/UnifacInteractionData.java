package co.unal.camd.properties.parameters.unifac;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Locale;

@Data
@Accessors(chain = true)
public class UnifacInteractionData {
    private double aij;
    private double bij;
    private double cij;
    private double aji;
    private double bji;
    private double cji;
    private UnifacParametersPair parametersPair;

    public UnifacInteractionData(UnifacParametersPair parametersPair) {
        this.parametersPair = parametersPair;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Unifac interactions [%d][%d] : aij:%f , bij:%f , cij:%f , aji:%f , bji:%f , cji:%f", parametersPair.getI(), parametersPair.getJ(), aij, bij, cij, aji, bji, cji);
    }
}
