package co.unal.camd.methods;

import co.unal.camd.methods.unifac.UnifacSubGroup;
import co.unal.camd.model.molecule.UnifacGroupNode;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParametersParserUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParametersParserUtils.class);

    private static final Pattern GROUP_OPTIONS_PATTERN = Pattern.compile("(?:^|[().])*([^(^)^.]*\\|[^(^)^.]*)(?:[().]|$)*");
    private static final Pattern MAIN_GROUPS_SPLITTER_PATTERN = Pattern.compile("\\d+(?:\\([^)]*\\))*(?=\\.|$)");
    private static final Pattern MAIN_GROUP_PATTERN = Pattern.compile("^[^(]*");
    private static final Pattern NESTED_GROUP_PATTERN = Pattern.compile("\\(([^)]+)\\)");
    private static final String NESTED_GROUP_MATCHER = ".*\\(.*\\).*";

    static Map<Integer, UnifacSubGroup> unifacContributions = new HashMap<>();

    static boolean validateNumericCell(XSSFCell cell) {
        if (cell == null)
            return false;
        if (CellType.NUMERIC != cell.getCellTypeEnum() && CellType.BLANK != cell.getCellTypeEnum() && cell.getRichStringCellValue().toString().trim().length() > 0)
            LOGGER.warn(String.format("(!) %s : %s", cell.getReference(), cell.getRichStringCellValue()));
        return CellType.NUMERIC == cell.getCellTypeEnum();
    }

    /***
     * Given a Second Order Contribution Configurations String, the method generate the different possibilities
     *
     * @param alternative String
     *
     * @return String of singles configurations
     */
    static List<String> breakConfigurationAlternatives(String alternative) {
        List<String> singleConfigurations = new ArrayList<>();
        breakConfigurationAlternatives(alternative, singleConfigurations);
        return singleConfigurations;
    }

    /**
     * Recursive helper method for {@link co.unal.camd.methods.ParametersParserUtils#breakConfigurationAlternatives(java.lang.String)}
     *
     * @param alternative
     * @param alternatives
     */
    private static void breakConfigurationAlternatives(String alternative, List<String> alternatives) {
        if (alternative.isEmpty())
            return;

        if (!alternative.contains("|")) {
            alternatives.add(alternative);
            return;
        }

        Matcher groupOptionsMatcher = GROUP_OPTIONS_PATTERN.matcher(alternative);
        if (groupOptionsMatcher.find()) {
            String groupsMatch = groupOptionsMatcher.group(1);
            String[] groupAlternatives = groupsMatch.split("\\|");
            for (String groupAlternative : groupAlternatives) {
                String newAlternative = alternative.replaceFirst(Pattern.quote(groupsMatch), groupAlternative);
                breakConfigurationAlternatives(newAlternative, alternatives);
            }
        }
    }

    /**
     * Parse an Unifac groups configuration String into a {@link UnifacGroupNode} object
     *
     * @param singleConfiguration Unifac configuration
     * @return UnifacGroupNode
     */
    static UnifacGroupNode parseNodeGroupsConfiguration(String singleConfiguration) {
        UnifacGroupNode mainGroupNode = null;
        UnifacGroupNode formerGroupNode = null;
        UnifacGroupNode currentGroup;

        Matcher matcher = MAIN_GROUPS_SPLITTER_PATTERN.matcher(singleConfiguration);
        while (matcher.find()) {
            String group = matcher.group(0);
            Matcher mainGroupMatcher = MAIN_GROUP_PATTERN.matcher(group);
            mainGroupMatcher.find();
            Integer headGroupCode = Integer.valueOf(mainGroupMatcher.group(0));
            UnifacSubGroup unifacSubGroup = unifacContributions.get(headGroupCode);
            currentGroup = new UnifacGroupNode(unifacSubGroup);
            if (mainGroupNode == null)
                mainGroupNode = currentGroup;
            else
                formerGroupNode.getSubGroups().add(currentGroup);

            if (group.matches(NESTED_GROUP_MATCHER)) {
                Matcher subgroupMatcher = NESTED_GROUP_PATTERN.matcher(group);
                while (subgroupMatcher.find()) {
                    UnifacGroupNode nestedGroupConfiguration = parseNodeGroupsConfiguration(subgroupMatcher.group(1));
                    currentGroup.getSubGroups().add(nestedGroupConfiguration);
                }
            }
            formerGroupNode = currentGroup;
        }
        return mainGroupNode;
    }
}
