package co.unal.camd.properties.groups.unifac;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "contributionGroups")
public class MainGroup {
    private String name;
    private int code;
    /**
     * Reverse relationship
     */
    private FamilyGroup familyGroup;

    private List<ContributionGroup> contributionGroups = new ArrayList<>();

    public MainGroup(int code, String name) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("MainGroup Group %s [%d]", name, code);
    }
}
