package co.unal.camd.methods.unifac;

import lombok.Data;

import java.util.Objects;

@Data
public class Unifacij {
    private int i;
    private int j;

    public Unifacij(int i, int j) {
        if (i == j) {
            this.i = 0;
            this.j = 0;
            return;
        }
        this.i = i;
        this.j = j;
    }

    public Unifacij(UnifacSubGroup iGroup, UnifacSubGroup jGroup) {
        this(iGroup.getMainGroup().getCode(), jGroup.getMainGroup().getCode());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Unifacij))
            return false;
        if (other == this)
            return true;
        Unifacij otherUnifacij = (Unifacij) other;
        return (this.i == otherUnifacij.i && this.j == otherUnifacij.j) || (this.i == this.j && otherUnifacij.i == otherUnifacij.j);
    }

    @Override
    public int hashCode() {
        // Needed to make the equals work well in Maps
        return Objects.hash(i, j);
    }
}
