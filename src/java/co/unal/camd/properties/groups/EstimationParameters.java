package co.unal.camd.properties.groups;


import co.unal.camd.properties.ProblemParameters;
import co.unal.camd.properties.groups.contributions.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.groups.contributions.EnvironmentalSecondOrderContribution;
import co.unal.camd.properties.groups.contributions.ThermoPhysicalSecondOrderContribution;
import co.unal.camd.properties.groups.unifac.ContributionGroup;
import co.unal.camd.properties.groups.unifac.FamilyGroup;
import co.unal.camd.properties.groups.unifac.MainGroup;
import co.unal.camd.properties.groups.unifac.UnifacInteractionData;
import co.unal.camd.properties.groups.unifac.UnifacParametersPair;
import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.view.CamdRunner;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Manage all the paramameters required to estimate properties using the Unifac method, Gani-Marrero method, ...
 *
 * @author Kevin Adrián Rodríguez Ruiz
 */
public class EstimationParameters {

    private static final Logger LOGGER = LoggerFactory.getLogger(EstimationParameters.class);

    private final Pattern GROUP_OPTIONS_PATTERN = Pattern.compile("(?:^|[().])*([^(^)^.]*\\|[^(^)^.]*)(?:[().]|$)*");
    private final Pattern MAIN_GROUP_PATTERN = Pattern.compile("^[^(]*");
    private final Pattern SUBGROUP_PATTERN = Pattern.compile("\\(([^)]+)\\)");

    /**
     * A functional unit is every atom different from carbon, or every Carbon with triple bond
     */
    private Pattern FUNCTIONAL_UNITS_PATTERN = Pattern.compile("[A-BD-Z](?:[a-z]*)|C[a-z]|C[\\W]*#");

    /**
     * A true to this matcher meants that the group has Carbon atoms
     */
    private String HAS_CARBON_MATCHER = ".*C(?:[^a-z].*|$)";

    /**
     * According to the Constantinou & Gani, greater contribution groups will lead to invalid properties estimations
     */
    private static final int MAX_ALLOWED_UNIFAC_GROUP = 103;

    /**
     * Unifac groups file path in resources directory (/src/resources/)
     */
    private static String UNIFAC_WORKBOOK_PATH = "/properties/Unifac-2017.xlsx";
    private XSSFWorkbook unifacWorkbook;
    private static String THERMOPROPS_WORKBOOK_PATH = "/properties/ThermoPropsContributions.xlsx";
    private XSSFWorkbook thermoPropsWorkbook;
    private static String ENVIRONMENTAL_PROPS_WORKBOOK_PATH = "/properties/HukkerikarEnvironmental.xlsx";
    private XSSFWorkbook environmantalWorkbook;

    @Getter
    protected Map<UnifacParametersPair, UnifacInteractionData> unifacInteractions = new HashMap<>();

    /**
     * <UNIFAC code, thermo-physical first order contributions>
     */
    @Getter
    protected Map<Integer, ContributionGroup> thermoPhysicalFirstOrderContributions = new HashMap<>();

    /**
     * <valence, list of c. groups>
     */
    @Getter
    protected Map<Integer, List<ContributionGroup>> valenceContributionGroups = new HashMap<Integer, List<ContributionGroup>>() {{
        put(0, new ArrayList<>());
        put(1, new ArrayList<>());
        put(2, new ArrayList<>());
        put(3, new ArrayList<>());
        put(4, new ArrayList<>());
        put(5, new ArrayList<>());
        put(6, new ArrayList<>());
    }};

    /**
     * <main group code, main group>
     */
    @Getter
    protected Map<Integer, MainGroup> unifacMainGroups = new HashMap<>();

    /**
     * List of C. groups families, its probabilities are also stored
     * <family group code, familiy group>
     */
    @Getter
    protected Map<Integer, FamilyGroup> unifacFamilyGroups = new HashMap<>();

    /**
     * <contribution case, second order data>
     */
    @Getter
    protected Map<Integer, ThermoPhysicalSecondOrderContribution> thermoPhysicalSecondOrderContributions = new HashMap<>();

    // ENVIRONMENTAL CONTRIBUTIONS

    /**
     * <code, first order contributions>
     */
    @Getter
    protected Map<Integer, EnvironmentalFirstOrderContribution> environmentalFirstOrderContributions = new HashMap<>();

    /**
     * <code, second order contributions>
     */
    @Getter
    protected Map<Integer, EnvironmentalSecondOrderContribution> environmentalSecondOrderContributions = new HashMap<>();

    /**
     * <UNIFAC group Id, Hukkerikar group Id>
     */
    @Getter
    protected Map<Integer, Integer> hukkerikarGroupsEquivalences = new HashMap<>();

    /**
     * constructors for load the info
     */
    public EstimationParameters() {
        try (InputStream unifacWBIS = EstimationParameters.class.getResourceAsStream(UNIFAC_WORKBOOK_PATH);
             InputStream thermoContributionsWBIS = EstimationParameters.class.getResourceAsStream(THERMOPROPS_WORKBOOK_PATH);
             InputStream environmentalContributionsWBIS = EstimationParameters.class.getResourceAsStream(ENVIRONMENTAL_PROPS_WORKBOOK_PATH);) {
            LOGGER.info("Loading UNIFAC groups file: {}", UNIFAC_WORKBOOK_PATH);
            unifacWorkbook = new XSSFWorkbook(unifacWBIS);
            LOGGER.info("Loading thermodynamical properties contributions groups file: {}", THERMOPROPS_WORKBOOK_PATH);
            thermoPropsWorkbook = new XSSFWorkbook(thermoContributionsWBIS);

            LOGGER.info("Loading environmental properties contributions groups file: {}", ENVIRONMENTAL_PROPS_WORKBOOK_PATH);
            environmantalWorkbook = new XSSFWorkbook(environmentalContributionsWBIS);

            loadUnifacijInteractions();
            loadGroupContributions();
            loadSecondOrderContributions();
            loadFamilyGroups();
            loadHukkerikarEquivalences();
            loadEnvironmentalFirstOrderContributions();
            loadEnvironmentalSecondOrderContributions();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Find the name of a contribution group
     *
     * @param groupCode
     * @return
     */
    public final String findGroupName(int groupCode) {
        return thermoPhysicalFirstOrderContributions.get(groupCode).getGroupName();
    }

    /**
     * Find the code of a contribution group given teh name
     *
     * @param name
     * @return
     */
    public final int findGroupCode(String name) {
        ContributionGroup contributionGroup = thermoPhysicalFirstOrderContributions.values().stream().filter(oneContributionGroup -> Objects.equals(name, oneContributionGroup.getGroupName())).findFirst().get();
        return contributionGroup.getCode();
    }

    /**
     * Read of the Unifac ij interaction groups from the sheet:
     * -  UNIFAC-DORTMUND-Interactions
     * In file
     * -  Unifac-2017.xlsx
     */
    private void loadUnifacijInteractions() {
        XSSFSheet interactionsSheet = unifacWorkbook.getSheetAt(0);
        LOGGER.info("");
        LOGGER.info("UNIFAC DORTMUND PARAMETERTS sheet: {}", unifacWorkbook.getSheetName(0));
        // Second Row
        int ijRow = 1;

        // Element for interactions where i=j
        UnifacParametersPair parametersPair = new UnifacParametersPair(0, 0);
        UnifacInteractionData unifacInteractions = new UnifacInteractionData(parametersPair).setAij(0.0).setBij(0.0).setCij(0.0).setAji(0.0).setBji(0.0).setCji(0.0);
        this.unifacInteractions.put(parametersPair, unifacInteractions);

        XSSFRow currentRow = interactionsSheet.getRow(ijRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer iParam = (int) currentRow.getCell(0).getNumericCellValue();
                Integer jParam = (int) currentRow.getCell(1).getNumericCellValue();
                parametersPair = new UnifacParametersPair(iParam, jParam);
                unifacInteractions = new UnifacInteractionData(parametersPair);
                readInteractionsCells(currentRow, unifacInteractions);
                LOGGER.debug("unifacInteractions {}", unifacInteractions);
                this.unifacInteractions.put(parametersPair, unifacInteractions);
            } catch (Exception e) {
                LOGGER.error("\nRow failed: {}", ijRow, e);
            }
            currentRow = interactionsSheet.getRow(++ijRow);
        }
    }

    private void readInteractionsCells(XSSFRow currentRow, UnifacInteractionData interactionData) {
        XSSFCell rowCell;
        rowCell = currentRow.getCell(2);
        if (validateNumericCell(rowCell))
            interactionData.setAij(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(3);
        if (validateNumericCell(rowCell))
            interactionData.setBij(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            interactionData.setCij(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            interactionData.setAji(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            interactionData.setBji(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(7);
        if (validateNumericCell(rowCell))
            interactionData.setCji(rowCell.getNumericCellValue());
    }

    private void loadGroupContributions() {
        loadUnifacGroupContributions();
        loadThermodynamicGroupContributions();
        thermoPhysicalFirstOrderContributions.forEach((integer, groupContribution) -> LOGGER.debug("groupContribution {}", groupContribution));
    }

    /**
     * Read of the Unifac surface and volume Q and R groups for different contribution groups from the sheet:
     * -  UNIFAC-DORTMUND-SurfaceVolume
     * In file
     * -  Unifac-2017.xlsx
     */
    private void loadUnifacGroupContributions() {
        XSSFSheet unifacRQSheet = unifacWorkbook.getSheetAt(1);
        LOGGER.info("");
        LOGGER.info("UNIFAC R AND Q sheet: {}", unifacWorkbook.getSheetName(1));
        // Second Row
        int unifacRow = 1;

        XSSFRow currentRow = unifacRQSheet.getRow(unifacRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                if (groupId > MAX_ALLOWED_UNIFAC_GROUP)
                    break;
                ContributionGroup firstOrderContribution = new ContributionGroup(groupId);
                readUnifacRQParams(currentRow, firstOrderContribution);
                readSmiles(currentRow, firstOrderContribution);
                thermoPhysicalFirstOrderContributions.put(groupId, firstOrderContribution);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", unifacRow, e);
            }
            currentRow = unifacRQSheet.getRow(++unifacRow);
        }
    }

    private void readUnifacRQParams(XSSFRow currentRow, ContributionGroup contributionGroup) {
        XSSFCell rowCell;
        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell)) {
            int mainGroupId = (int) rowCell.getNumericCellValue();
            MainGroup mainGroup = unifacMainGroups.getOrDefault(mainGroupId, new MainGroup(mainGroupId, currentRow.getCell(2).getStringCellValue()));
            unifacMainGroups.putIfAbsent(mainGroupId, mainGroup);
            contributionGroup.setMainGroup(mainGroup);
            mainGroup.getContributionGroups().add(contributionGroup);
        }

        rowCell = currentRow.getCell(3);
        if (rowCell != null)
            contributionGroup.setGroupName(rowCell.getStringCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            contributionGroup.setRParam(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            contributionGroup.setQParam(rowCell.getNumericCellValue());
    }

    /**
     * Read of the groups required in the estimation of density, boiling point, melting point, Gibss free energy and dielectric constant for different contribution groups from the sheet:
     * -  Contributions
     * In file
     * -  ThermoPropsContributions.xlsx
     */
    private void loadThermodynamicGroupContributions() {
        XSSFSheet contributionsSheet = thermoPropsWorkbook.getSheetAt(0);
        LOGGER.info("");
        LOGGER.info("THERMODYNAMICAL PROPERTIES Sheet: {}", thermoPropsWorkbook.getSheetName(0));
        // Second Row
        int tgcRow = 1;

        XSSFRow currentRow = contributionsSheet.getRow(tgcRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                if (thermoPhysicalFirstOrderContributions.containsKey(groupId)) {
                    ContributionGroup firstOrderContribution = thermoPhysicalFirstOrderContributions.get(groupId);
                    readThermodynamicContributions(currentRow, firstOrderContribution);
                } else
                    LOGGER.warn("No thermo Physical First Order Contribution found for group Id : {}", groupId);
            } catch (Exception e) {
                LOGGER.error("\nRow failed: {}", tgcRow, e);
            }
            currentRow = contributionsSheet.getRow(++tgcRow);
        }
    }

    private void readSmiles(XSSFRow currentRow, ContributionGroup contributionGroup) {
        XSSFCell rowCell = currentRow.getCell(6);
        if (rowCell == null)
            return;
        String smilesPattern = rowCell.getStringCellValue().trim();
        contributionGroup.setSmilesPattern(smilesPattern);
        contributionGroup.setAliphaticContent(smilesPattern.matches(HAS_CARBON_MATCHER));
        contributionGroup.setFunctionalElementsNumber(countFunctionalElements(smilesPattern));
    }

    private int countFunctionalElements(String smilesPattern) {
        Matcher matcher = FUNCTIONAL_UNITS_PATTERN.matcher(smilesPattern);
        int occurrences = 0;
        while (matcher.find())
            occurrences++;
        //        LOGGER.info("{} : elems {} - carbon {}", smilesPattern + " - " + occurrences + " - " + smilesPattern.matches(HAS_CARBON_MATCHER));
        return occurrences;
    }

    private void readThermodynamicContributions(XSSFRow currentRow, ContributionGroup thermodynamicContribution) {
        XSSFCell rowCell;

        rowCell = currentRow.getCell(3);
        if (rowCell != null)
            LOGGER.debug(String.format(">> %s should match %s", rowCell.getStringCellValue(), thermodynamicContribution.getGroupName()));

        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell)) {
            int valence = (int) rowCell.getNumericCellValue();
            thermodynamicContribution.setValence(valence);
            valenceContributionGroups.get(valence).add(thermodynamicContribution);
        }

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().setMolecularWeight(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().setBoilingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().setMeltingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(7);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().setGibbsFreeEnergy(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(8);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().setDipoleMoment(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(9);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().setDipoleMomentH1i(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(10);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().setLiquidMolarVolume(rowCell.getNumericCellValue());

        // DENSITY PARAMETERS
        rowCell = currentRow.getCell(11);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityA()[0] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(12);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityB()[0] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(13);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityC()[0] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(14);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityA()[1] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(15);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityB()[1] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(16);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityC()[1] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(17);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityA()[2] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(18);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityB()[2] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(19);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityC()[2] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(21);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityA()[3] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(21);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityB()[3] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(22);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getThermoPhysicalFirstContribution().getDensityC()[3] = rowCell.getNumericCellValue();
    }

    /**
     * Read of the groups required in the estimation of density, boiling point, melting point, Gibss free energy and dielectric constant for different contribution groups from the sheet:
     * -  SecondOrdenParams
     * In file
     * -  ThermoPropsContributions.xlsx
     */
    private void loadSecondOrderContributions() {
        XSSFSheet secondOrderGroupsSheet = thermoPropsWorkbook.getSheetAt(1);
        LOGGER.info("");
        LOGGER.info("SECOND ORDER GROUPS Sheet: {}", thermoPropsWorkbook.getSheetName(1));
        // Second Row
        int soGroupRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(soGroupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                ThermoPhysicalSecondOrderContribution secondOrderContribution = readSecondGroupContributionsParams(currentRow);
                readSecondOrderConfigurations(currentRow, secondOrderContribution);

                thermoPhysicalSecondOrderContributions.put(secondOrderContribution.getGroupsCase(), secondOrderContribution);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", soGroupRow, e);
            } finally {
                currentRow = secondOrderGroupsSheet.getRow(++soGroupRow);
            }
        }
        thermoPhysicalSecondOrderContributions.forEach((integer, secondOrderContribution) -> LOGGER.debug("secondOrderContribution {}", secondOrderContribution));
    }

    private ThermoPhysicalSecondOrderContribution readSecondGroupContributionsParams(XSSFRow currentRow) {
        Integer groupsCase = (int) currentRow.getCell(0).getNumericCellValue();
        ThermoPhysicalSecondOrderContribution secondOrderContribution = new ThermoPhysicalSecondOrderContribution(groupsCase);

        XSSFCell rowCell = currentRow.getCell(1);
        if (rowCell != null)
            secondOrderContribution.setGroupsDescription(rowCell.getStringCellValue().trim());

        rowCell = currentRow.getCell(2);
        if (validateNumericCell(rowCell))
            secondOrderContribution.setBoilingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(3);
        if (rowCell != null)
            secondOrderContribution.setMeltingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            secondOrderContribution.setGibbsEnergy(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            secondOrderContribution.setLiquidMolarVolume(rowCell.getNumericCellValue());

        return secondOrderContribution;
    }

    private void readSecondOrderConfigurations(XSSFRow currentRow, ThermoPhysicalSecondOrderContribution secondOrderContribution) {
        XSSFCell rowCell = currentRow.getCell(6);
        if (rowCell == null)
            return;

        String rawGroupCases = rowCell.getCellTypeEnum() == CellType.NUMERIC ? NumberToTextConverter.toText(rowCell.getNumericCellValue()) : rowCell.getStringCellValue().trim();
        String[] differentAlternatives = rawGroupCases.split("/");
        for (String differentAlternative : differentAlternatives) {
            List<String> singleConfigurations = breakAlternative(differentAlternative);
            singleConfigurations.forEach(singleConfiguration -> {
                ContributionGroupNode secondGroupConfiguration = parseNodeGroupsConfiguration(singleConfiguration);
                secondOrderContribution.getGroupConfigurations().add(secondGroupConfiguration);
                appendToFirstOrderContributions(singleConfiguration, secondGroupConfiguration, secondOrderContribution);
            });
        }
    }

    private void appendToFirstOrderContributions(String singleConfiguration, ContributionGroupNode secondGroupConfiguration, ThermoPhysicalSecondOrderContribution secondOrderContribution) {
        String[] rawGroups = singleConfiguration.split("[|().]+");
        int[] groups = Stream.of(rawGroups).mapToInt(Integer::parseInt).toArray();
        int biggerGroup = Arrays.stream(groups).max().getAsInt();
        if (thermoPhysicalFirstOrderContributions.containsKey(biggerGroup))
            thermoPhysicalFirstOrderContributions.get(biggerGroup).getSecondOrderContributions().put(secondGroupConfiguration, secondOrderContribution);
    }

    public ContributionGroupNode parseGroupsConfiguration(String textConfiguration) {
        return parseNodeGroupsConfiguration(textConfiguration);
    }

    private ContributionGroupNode parseNodeGroupsConfiguration(String singleConfiguration) {
        ContributionGroupNode mainGroupNode = null;
        ContributionGroupNode formerGroupNode = null;
        ContributionGroupNode currentGroup;

        String[] groups = singleConfiguration.split("\\.");
        for (String group : groups) {
            Matcher mainGroupMatcher = MAIN_GROUP_PATTERN.matcher(group);
            mainGroupMatcher.find();
            Integer headGroupCode = Integer.valueOf(mainGroupMatcher.group(0));
            ContributionGroup contributionGroup = thermoPhysicalFirstOrderContributions.get(headGroupCode);
            currentGroup = new ContributionGroupNode(contributionGroup);
            if (mainGroupNode == null)
                mainGroupNode = currentGroup;
            else
                formerGroupNode.getSubGroups().add(currentGroup);

            if (group.matches(".*\\(.*\\).*")) {
                Matcher subgroupMatcher = SUBGROUP_PATTERN.matcher(group);
                while (subgroupMatcher.find()) {
                    int groupMatch = Integer.valueOf(subgroupMatcher.group(1));
                    ContributionGroup matchContributionGroup = thermoPhysicalFirstOrderContributions.get(groupMatch);
                    currentGroup.getSubGroups().add(new ContributionGroupNode(matchContributionGroup));
                }
            }
            formerGroupNode = currentGroup;
        }
        return mainGroupNode;
    }

    private List<String> breakAlternative(String alternative) {
        List<String> singleConfigurations = new ArrayList<>();
        breakAlternative(alternative, singleConfigurations);
        return singleConfigurations;
    }

    private void breakAlternative(String alternative, List<String> alternatives) {
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
                breakAlternative(newAlternative, alternatives);
            }
        }
    }

    /**
     * Read the environmental groups according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  FamilyGroups
     * In file
     * -  HukkerikarEnvironmental.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadEnvironmentalFirstOrderContributions() {
        XSSFSheet firstOrderGroupsSheet = environmantalWorkbook.getSheetAt(0);
        LOGGER.info("");
        LOGGER.info("ENVIRONMENTAL FIRST ORDER CONTRIBUTIONS Sheet: {}", environmantalWorkbook.getSheetName(0));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = firstOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                EnvironmentalFirstOrderContribution environmentalContribution = readFirstOrderEnvironmentalContribution(currentRow);
                environmentalFirstOrderContributions.put(environmentalContribution.getCode(), environmentalContribution);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", groupRow, e);
            }
            currentRow = firstOrderGroupsSheet.getRow(++groupRow);
        }
    }

    /**
     * Read the environmental groups according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  FamilyGroups
     * In file
     * -  HukkerikarEnvironmental.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadEnvironmentalSecondOrderContributions() {
        XSSFSheet secondOrderGroupsSheet = environmantalWorkbook.getSheetAt(1);
        LOGGER.info("");
        LOGGER.info("ENVIRONMENTAL SECOND ORDER CONTRIBUTIONS Sheet: {}", environmantalWorkbook.getSheetName(1));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                EnvironmentalSecondOrderContribution environmentalContribution = readSecondOrderEnvironmentalContribution(currentRow);
                readEnvironmentalSecondOrderConfigurations(currentRow, environmentalContribution);
                environmentalSecondOrderContributions.put(environmentalContribution.getCode(), environmentalContribution);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", groupRow, e);
            }
            currentRow = secondOrderGroupsSheet.getRow(++groupRow);
        }
    }

    private void readEnvironmentalSecondOrderConfigurations(XSSFRow currentRow, EnvironmentalSecondOrderContribution secondOrderContribution) {
        XSSFCell rowCell = currentRow.getCell(22);
        if (rowCell == null)
            return;

        String rawGroupCases = rowCell.getCellTypeEnum() == CellType.NUMERIC ? NumberToTextConverter.toText(rowCell.getNumericCellValue()) : rowCell.getStringCellValue().trim();
        String[] differentAlternatives = rawGroupCases.split("/");
        for (String differentAlternative : differentAlternatives) {
            List<String> singleConfigurations = breakAlternative(differentAlternative);
            singleConfigurations.forEach(singleConfiguration -> {
                ContributionGroupNode secondGroupConfiguration = parseNodeGroupsConfiguration(singleConfiguration);
                secondOrderContribution.getGroupConfigurations().add(secondGroupConfiguration);
                appendToEnvironmentalFirstOrderContributions(singleConfiguration, secondGroupConfiguration, secondOrderContribution);
            });
        }
    }

    private void appendToEnvironmentalFirstOrderContributions(String singleConfiguration, ContributionGroupNode secondGroupConfiguration, EnvironmentalSecondOrderContribution secondOrderContribution) {
        String[] rawGroups = singleConfiguration.split("[|().]+");
        int[] groups = Stream.of(rawGroups).mapToInt(Integer::parseInt).toArray();
        int biggerGroup = Arrays.stream(groups).max().getAsInt();
        Integer hukkerikarEquivalence = hukkerikarGroupsEquivalences.get(biggerGroup);
        if (environmentalFirstOrderContributions.containsKey(hukkerikarEquivalence))
            environmentalFirstOrderContributions.get(hukkerikarEquivalence).getSecondOrderContributions().put(secondGroupConfiguration, secondOrderContribution);
    }

    /**
     * Read the equivalences between the unifac groups and the Hukkerikar groups
     * Sheet;
     * -  FamilyGroups
     * In file
     * -  HukkerikarEnvironmental.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadHukkerikarEquivalences() {
        XSSFSheet equivalencesSheet = environmantalWorkbook.getSheetAt(3);
        LOGGER.info("");
        LOGGER.info("ENVIRONMENTAL FUNCTIONAL GROUPS EQUIVALENCES Sheet: {}", environmantalWorkbook.getSheetName(3));

        // Third Row
        int groupRow = 2;

        XSSFRow currentRow = equivalencesSheet.getRow(groupRow);
        while (currentRow != null && (validateNumericCell(currentRow.getCell(0)) || validateNumericCell(currentRow.getCell(5)))) {
            if (validateNumericCell(currentRow.getCell(0)) && validateNumericCell(currentRow.getCell(5)))
                try {
                    int unifacGroupId = (int) currentRow.getCell(0).getNumericCellValue();
                    int hukkerikarGroupId = (int) currentRow.getCell(5).getNumericCellValue();
                    hukkerikarGroupsEquivalences.put(unifacGroupId, hukkerikarGroupId);
                } catch (Exception e) {
                    LOGGER.error("Row failed: {}", groupRow, e);
                }
            currentRow = equivalencesSheet.getRow(++groupRow);
        }
    }

    private EnvironmentalFirstOrderContribution readFirstOrderEnvironmentalContribution(XSSFRow currentRow) {
        Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
        EnvironmentalFirstOrderContribution environmentalContribution = new EnvironmentalFirstOrderContribution(groupId);
        XSSFCell rowCell;

        rowCell = currentRow.getCell(1);
        if (rowCell != null)
            environmentalContribution.setGroupName(rowCell.getStringCellValue());

        rowCell = currentRow.getCell(2);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLC50FM(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(3);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLC50DM(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            environmentalContribution.setOralLD50(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLogWS(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterBFC(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(12);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirEUAc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(13);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirEUAnc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(14);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirERAc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(15);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirERAnc(rowCell.getNumericCellValue());

        return environmentalContribution;
    }

    private EnvironmentalSecondOrderContribution readSecondOrderEnvironmentalContribution(XSSFRow currentRow) {
        Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
        EnvironmentalSecondOrderContribution environmentalContribution = new EnvironmentalSecondOrderContribution(groupId);
        XSSFCell rowCell;

        rowCell = currentRow.getCell(1);
        if (rowCell != null)
            environmentalContribution.setGroupDescription(rowCell.getStringCellValue());

        rowCell = currentRow.getCell(2);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLC50FM(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(3);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLC50DM(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            environmentalContribution.setOralLD50(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLogWS(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterBFC(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(9);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirEUAc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(10);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirEUAnc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(11);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirERAc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(12);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirERAnc(rowCell.getNumericCellValue());

        return environmentalContribution;
    }

    /**
     * Read the family groups as seen in the right side groups selector
     * Sheet;
     * -  FamilyGroups
     * In file
     * -  Unifac-2017.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadFamilyGroups() {
        XSSFSheet secondOrderGroupsSheet = unifacWorkbook.getSheetAt(2);
        LOGGER.info("");
        LOGGER.info("FAMILY GROUPS Sheet: {}", unifacWorkbook.getSheetName(2));
        // Second Row
        int gcRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(gcRow);
        while (currentRow != null && currentRow.getCell(0) != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer familyIndex = (int) currentRow.getCell(0).getNumericCellValue();
                String familyName = currentRow.getCell(1).getStringCellValue();
                FamilyGroup familyGroup = new FamilyGroup(familyName);
                Iterator<Cell> cellsIterator = currentRow.cellIterator();
                while (cellsIterator.hasNext()) {
                    XSSFCell rowCell = (XSSFCell) cellsIterator.next();
                    if (rowCell.getColumnIndex() > 1 && validateNumericCell(rowCell)) {
                        int mainGroupCode = (int) rowCell.getNumericCellValue();
                        MainGroup mainGroup = unifacMainGroups.get(mainGroupCode);
                        if (mainGroup != null) {
                            familyGroup.getMainGroups().add(mainGroup);
                            mainGroup.setFamilyGroup(familyGroup);
                        } else
                            LOGGER.warn("main group {} not found", mainGroupCode);
                    }
                }
                LOGGER.debug("familyGroup {}", familyGroup);
                unifacFamilyGroups.put(familyIndex, familyGroup);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", gcRow, e);
            }
            currentRow = secondOrderGroupsSheet.getRow(++gcRow);
        }
    }

    private boolean validateNumericCell(XSSFCell cell) {
        if (cell == null)
            return false;
        if (CellType.NUMERIC != cell.getCellTypeEnum() && CellType.BLANK != cell.getCellTypeEnum() && cell.getRichStringCellValue().toString().trim().length() > 0)
            LOGGER.warn(String.format("(!) %s : %s", cell.getReference(), cell.getRichStringCellValue()));
        return CellType.NUMERIC == cell.getCellTypeEnum();
    }

    public void defaultFamilyProbabilities() {
        CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().entrySet().stream()
                .filter(familyEntry -> IntStream.of(ProblemParameters.DEFAULT_UNCHECKED_FAMILIES).anyMatch(i -> i == familyEntry.getKey()))
                .forEach(familyGroupEntry -> familyGroupEntry.getValue().setProbability(0));
    }

}

