package co.unal.camd.properties.groups.unifac;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(exclude = "mainGroups")
public class FamilyGroup {
    private String name;

    /**
     * Reverse relationship
     */
    private List<MainGroup> mainGroups = new ArrayList<>();

    // TODO set better probabilities from any Excel WorkBook or from the program, this is a code burn attribute
    private double probability = 1;

    public FamilyGroup(String name) {
        this.name = name;
    }

    public String readableMainGroups() {
        List<String> groupsNames = mainGroups.stream()
                .map(MainGroup::getName)
                .collect(Collectors.toList());
        String familyLabel = Arrays.toString(groupsNames.toArray());
        return familyLabel.replaceAll("\\[|\\]|\\s", "");
    }

    @Override
    public String toString() {
        List<Integer> groups = mainGroups.stream().map(MainGroup::getCode).collect(Collectors.toList());
        return String.format("FamilyGroup Group %s : %s", name, Arrays.toString(groups.toArray()));
    }
}
