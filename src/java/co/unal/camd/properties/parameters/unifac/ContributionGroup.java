package co.unal.camd.properties.parameters.unifac;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContributionGroup {
    public static int[] DEFAULT_UNCHECKED_FAMILIES = new int[]{3, 13, 16, 17};
    public static int MAX_GROUP_DATA = 103;

    @Data
    public static class Family {
        private String name;
        private List<Main> mainGroups = new ArrayList<>();
        private double probability = 1;

        public Family(String name) {
            this.name = name;
        }

        public String readableMainGroups() {
            List<String> groupsNames = mainGroups.stream()
                    .map(ContributionGroup.Main::getName)
                    .collect(Collectors.toList());
            String familyLabel = Arrays.toString(groupsNames.toArray());
            return familyLabel.replaceAll("\\[|\\]|\\s", "");
        }

        @Override
        public String toString() {
            List<Integer> groups = mainGroups.stream().map(Main::getCode).collect(Collectors.toList());
            return String.format("Group family %s : %s", name, Arrays.toString(groups.toArray()));
        }
    }

    @Data
    public static class Main {
        private String name;
        private int code;

        public Main(int code, String name) {
            this.name = name;
            this.code = code;
        }

        @Override
        public String toString() {
            return String.format("Group %s [%d]", name, code);
        }
    }

}
