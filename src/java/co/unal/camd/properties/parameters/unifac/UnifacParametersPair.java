package co.unal.camd.properties.parameters.unifac;

import lombok.Data;

import java.util.Objects;

@Data
public class UnifacParametersPair {
    private Integer i;
    private Integer j;

    public UnifacParametersPair(Integer i, Integer j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof UnifacParametersPair))
            return false;
        if (other == this)
            return true;
        UnifacParametersPair otherCasted = (UnifacParametersPair) other;
        return this.i == otherCasted.i && this.j == otherCasted.j;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }
}
