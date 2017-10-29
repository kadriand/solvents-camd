package co.unal.camd.properties.parameters.unifac;

import lombok.Data;

@Data
public class UnifacInteractionData {
    private Double aij;
    private Double bij;
    private Double cij;
    private Double aji;
    private Double bji;
    private Double cji;
    private UnifacParametersPair parametersPair;

    public UnifacInteractionData(UnifacParametersPair parametersPair) {
        this.parametersPair = parametersPair;
    }

    @Override
    public String toString() {
        return String.format("Interactions [%d][%d] : rParam:%f , qParam:%f , groupName:%f , mainGroup:%f , bji:%f , cji:%f", parametersPair.getI(), parametersPair.getJ(), aij, bij, cij, aji, bji, cji);
    }
}
