package co.unal.camd.properties.parameters;

import co.unal.camd.properties.model.ContributionGroupNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kadri on 15/03/2017.
 */
public class ProblemDefaults {

    public static double TEMPERATURE = 298.15; // K
    public static int ITERATIONS = 50; // int


    public static void main(String... args) {
        String text = "2|3|4(82|81|14).29|30|85|32|33|35.5(8).6/42.3|4.2";

        String[] differentAlternatives = text.split("/");
        List<ContributionGroupNode> configurations = new ArrayList<>();

        for (String differentAlternative : differentAlternatives) {
            List<String> singleConfigurations = new ArrayList<>();
            breakAlternative(differentAlternative, singleConfigurations);
            singleConfigurations.forEach(singleConfiguration -> {
                ContributionGroupNode secondGroupConfiguration = parseSecondGroup(singleConfiguration);
                configurations.add(secondGroupConfiguration);
                String[] groups = singleConfiguration.split("[|().]+");
                int biggerGroup = -1;
                for (String group : groups) {
                    Integer groupCode = Integer.valueOf(group);
                    biggerGroup = groupCode > biggerGroup ? groupCode : biggerGroup;
                }
            });
        }
        System.out.println(configurations);
    }

    private static ContributionGroupNode parseSecondGroup(String singleConfiguration) {
        Pattern mainGroupPattern = Pattern.compile("^[^(]*");
        Pattern subgroupPattern = Pattern.compile("\\(([^)]+)\\)");
        ContributionGroupNode mainGroupNode = null;
        ContributionGroupNode formerGroupNode = null;
        ContributionGroupNode currentGroup;

        String[] groups = singleConfiguration.split("\\.");
        for (String group : groups) {
            Matcher mainGroupMatcher = mainGroupPattern.matcher(group);
            mainGroupMatcher.find();
            Integer mainGroup = Integer.valueOf(mainGroupMatcher.group(0));

            currentGroup = new ContributionGroupNode(mainGroup);
            if (mainGroupNode == null)
                mainGroupNode = currentGroup;
            else
                formerGroupNode.getSubGroups().add(currentGroup);

            if (group.matches(".*\\(.*\\).*")) {
                Matcher subgroupMatcher = subgroupPattern.matcher(group);
                while (subgroupMatcher.find()) {
                    int groupMatch = Integer.valueOf(subgroupMatcher.group(1));
                    currentGroup.getSubGroups().add(new ContributionGroupNode(groupMatch));
                }
            }
            formerGroupNode = currentGroup;
        }
        return mainGroupNode;
    }

    public static void breakAlternative(String alternative, List<String> alternatives) {
        if (!alternative.contains("|")) {
            alternatives.add(alternative);
            System.out.println(alternative);
            return;
        }

        Pattern groupOptionsPattern = Pattern.compile("(?:^|[().])*([^(^)^.]*\\|[^(^)^.]*)(?:[().]|$)*");
        Matcher groupOptionsMatcher = groupOptionsPattern.matcher(alternative);
        if (groupOptionsMatcher.find()) {
            String groupsMatch = groupOptionsMatcher.group(1);
            String[] groupAlternatives = groupsMatch.split("\\|");
            for (String groupAlternative : groupAlternatives) {
                String newAlternative = alternative.replaceFirst(Pattern.quote(groupsMatch), groupAlternative);
                breakAlternative(newAlternative, alternatives);
            }
        }
    }

}
