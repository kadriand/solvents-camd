package co.unal.camd.properties.parameters.unifac;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContributionGroup {

    @Data
    public static class Family {
        private String name;
        private List<Main> mainGroups = new ArrayList<>();

        public Family(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            List<Integer> groups = mainGroups.stream().map(Main::getId).collect(Collectors.toList());
            return String.format("Group family %s : %s", name, Arrays.toString(groups.toArray()));
        }
    }

    @Data
    public static class Main {
        private String name;
        private Integer id;

        public Main(Integer id, String name) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return String.format("Group %s [%d]", name, id);
        }
    }

}
