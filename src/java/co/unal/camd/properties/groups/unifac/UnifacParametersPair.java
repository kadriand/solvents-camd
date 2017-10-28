package co.unal.camd.properties.groups.unifac;

import lombok.Data;

import java.util.Objects;

@Data
public class UnifacParametersPair {
    private int i;
    private int j;

    public UnifacParametersPair(int i, int j) {
        if (i == j) {
            this.i = 0;
            this.j = 0;
            return;
        }
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
        return (this.i == otherCasted.i && this.j == otherCasted.j) || (this.i == this.j && otherCasted.i == otherCasted.j);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }
}
