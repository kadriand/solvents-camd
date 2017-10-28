package co.unal.camd.methods.unifac;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "subGroups")
public class UnifacMainGroup {
    private String name;
    private int code;
    /**
     * Reverse relationship
     */
    private FamilyGroup familyGroup;

    private List<UnifacSubGroup> subGroups = new ArrayList<>();

    public UnifacMainGroup(int code, String name) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("UnifacMainGroup Group %s [%d]", name, code);
    }
}
