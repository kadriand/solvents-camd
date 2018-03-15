package co.unal.camd.properties.parameters;


import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.parameters.unifac.ContributionGroup;
import co.unal.camd.properties.parameters.unifac.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.EnvironmentalSecondOrderContribution;
import co.unal.camd.properties.parameters.unifac.ThermoPhysicalFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.ThermoPhysicalSecondOrderContribution;
import co.unal.camd.properties.parameters.unifac.UnifacInteractionData;
import co.unal.camd.properties.parameters.unifac.UnifacParametersPair;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Manage all the paramameters required to estimate properties using the Unifac method, Gani-Marrero method, ...
 *
 * @author Kevin Adrián Rodríguez Ruiz
 */
public class EstimationParameters {

    private final Pattern GROUP_OPTIONS_PATTERN = Pattern.compile("(?:^|[().])*([^(^)^.]*\\|[^(^)^.]*)(?:[().]|$)*");
    private final Pattern MAIN_GROUP_PATTERN = Pattern.compile("^[^(]*");
    private final Pattern SUBGROUP_PATTERN = Pattern.compile("\\(([^)]+)\\)");

    /**
     * Unifac parameters file path in resources directory (/src/resources/)
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
     * <code, first order contributions>
     */
    @Getter
    protected Map<Integer, ThermoPhysicalFirstOrderContribution> thermoPhysicalFirstOrderContributions = new HashMap<>();

    /**
     * <valence, list of c. groups>
     */
    @Getter
    protected Map<Integer, List<ThermoPhysicalFirstOrderContribution>> valenceContributionGroups = new HashMap<Integer, List<ThermoPhysicalFirstOrderContribution>>() {{
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
    protected Map<Integer, ContributionGroup.Main> unifacMainGroups = new HashMap<>();

    /**
     * List of C. groups families, its probabilities are also stored
     */
    @Getter
    protected Map<Integer, ContributionGroup.Family> unifacFamilyGroups = new HashMap<>();

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
            System.out.println("Loading UNIFAC parameters file: " + UNIFAC_WORKBOOK_PATH);
            unifacWorkbook = new XSSFWorkbook(unifacWBIS);
            System.out.println("Loading thermodynamical properties contributions parameters file: " + THERMOPROPS_WORKBOOK_PATH);
            thermoPropsWorkbook = new XSSFWorkbook(thermoContributionsWBIS);

            System.out.println("Loading environmental properties contributions parameters file: " + ENVIRONMENTAL_PROPS_WORKBOOK_PATH);
            environmantalWorkbook = new XSSFWorkbook(environmentalContributionsWBIS);

            loadUnifacijInteractions();
            loadGroupContributions();
            loadSecondOrderContributions();
            loadFamilyGroups();
            loadHukkerikarEquivalences();
            loadEnvironmentalFirstOrderContributions();
            loadEnvironmentalSecondOrderContributions();

            unifacWBIS.close();
            thermoContributionsWBIS.close();
            environmentalContributionsWBIS.close();
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
        ThermoPhysicalFirstOrderContribution contributionGroup = thermoPhysicalFirstOrderContributions.values().stream().filter(oneContributionGroup -> Objects.equals(name, oneContributionGroup.getGroupName())).findFirst().get();
        return contributionGroup.getCode();
    }

    //TODO HANDLE WITH AROMATICS AND STUFF
    public final double getProbability(int contributionGroupCode) {
        ContributionGroup.Main mainGroup = thermoPhysicalFirstOrderContributions.get(contributionGroupCode).getMainGroup();
        Optional<ContributionGroup.Family> family = unifacFamilyGroups.values().stream().filter(oneFamily -> oneFamily.getMainGroups().stream().anyMatch(main -> main.equals(mainGroup))).findFirst();
        return family.map(ContributionGroup.Family::getProbability).orElse(0.0);
    }

    /**
     * Read of the Unifac ij interaction parameters from the sheet:
     * -  UNIFAC-DORTMUND-Interactions
     * In file
     * -  Unifac-2017.xlsx
     */
    private void loadUnifacijInteractions() {
        XSSFSheet interactionsSheet = unifacWorkbook.getSheetAt(0);
        System.out.println("\nUNIFAC DORTMUND PARAMETERTS sheet: " + unifacWorkbook.getSheetName(0));
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
                debug(unifacInteractions);
                this.unifacInteractions.put(parametersPair, unifacInteractions);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + ijRow);
                e.printStackTrace();
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
        thermoPhysicalFirstOrderContributions.forEach((integer, groupContribution) -> debug(groupContribution));
    }

    /**
     * Read of the Unifac surface and volume Q and R parameters for different contribution groups from the sheet:
     * -  UNIFAC-DORTMUND-SurfaceVolume
     * In file
     * -  Unifac-2017.xlsx
     */
    private void loadUnifacGroupContributions() {
        XSSFSheet unifacRQSheet = unifacWorkbook.getSheetAt(1);
        System.out.println("\nUNIFAC R AND Q sheet: " + unifacWorkbook.getSheetName(1));
        // Second Row
        int unifacRow = 1;

        XSSFRow currentRow = unifacRQSheet.getRow(unifacRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                ThermoPhysicalFirstOrderContribution contributionData = new ThermoPhysicalFirstOrderContribution(groupId);
                readUnifacRQParams(currentRow, contributionData);
                readSmiles(currentRow, contributionData);
                thermoPhysicalFirstOrderContributions.put(groupId, contributionData);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + unifacRow);
                e.printStackTrace();
            }
            currentRow = unifacRQSheet.getRow(++unifacRow);
        }
    }

    private void readUnifacRQParams(XSSFRow currentRow, ThermoPhysicalFirstOrderContribution contributionData) {
        XSSFCell rowCell;
        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell)) {
            int mainGroupId = (int) rowCell.getNumericCellValue();
            unifacMainGroups.putIfAbsent(mainGroupId, new ContributionGroup.Main(mainGroupId, currentRow.getCell(2).getStringCellValue()));
            contributionData.setMainGroup(unifacMainGroups.get(mainGroupId));
        }

        rowCell = currentRow.getCell(3);
        if (rowCell != null)
            contributionData.setGroupName(rowCell.getStringCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            contributionData.setRParam(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            contributionData.setQParam(rowCell.getNumericCellValue());
    }

    private void readSmiles(XSSFRow currentRow, ThermoPhysicalFirstOrderContribution contributionData) {
        XSSFCell rowCell;
        rowCell = currentRow.getCell(6);
        if (rowCell != null)
            contributionData.setSmilesPattern(rowCell.getStringCellValue().trim());
    }

    /**
     * Read of the parameters required in the estimation of density, boiling point, melting point, Gibss free energy and dielectric constant for different contribution groups from the sheet:
     * -  Contributions
     * In file
     * -  ThermoPropsContributions.xlsx
     */
    private void loadThermodynamicGroupContributions() {
        XSSFSheet contributionsSheet = thermoPropsWorkbook.getSheetAt(0);
        System.out.println("\nTHERMODYNAMICAL PROPERTIES Sheet: " + thermoPropsWorkbook.getSheetName(0));
        // Second Row
        int tgcRow = 1;

        XSSFRow currentRow = contributionsSheet.getRow(tgcRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                ThermoPhysicalFirstOrderContribution contributionData = thermoPhysicalFirstOrderContributions.get(groupId);
                readThermodynamicContributions(currentRow, contributionData);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + tgcRow);
                e.printStackTrace();
            }
            currentRow = contributionsSheet.getRow(++tgcRow);
        }
    }

    private void readThermodynamicContributions(XSSFRow currentRow, ThermoPhysicalFirstOrderContribution thermodynamicContribution) {
        XSSFCell rowCell;

        rowCell = currentRow.getCell(3);
        if (rowCell != null)
            debug(String.format(">> %s should match %s", rowCell.getStringCellValue(), thermodynamicContribution.getGroupName()));

        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell)) {
            int valence = (int) rowCell.getNumericCellValue();
            thermodynamicContribution.setValence(valence);
            valenceContributionGroups.get(valence).add(thermodynamicContribution);
        }

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.setMolecularWeight(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.setBoilingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.setMeltingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(7);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.setGibbsFreeEnergy(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(8);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.setDipoleMoment(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(9);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.setDipoleMomentH1i(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(10);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.setLiquidMolarVolume(rowCell.getNumericCellValue());

        // DENSITY PARAMETERS
        rowCell = currentRow.getCell(11);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityA()[0] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(12);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityB()[0] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(13);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityC()[0] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(14);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityA()[1] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(15);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityB()[1] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(16);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityC()[1] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(17);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityA()[2] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(18);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityB()[2] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(19);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityC()[2] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(21);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityA()[3] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(21);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityB()[3] = rowCell.getNumericCellValue();
        rowCell = currentRow.getCell(22);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getDensityC()[3] = rowCell.getNumericCellValue();
    }

    /**
     * Read of the parameters required in the estimation of density, boiling point, melting point, Gibss free energy and dielectric constant for different contribution groups from the sheet:
     * -  SecondOrdenParams
     * In file
     * -  ThermoPropsContributions.xlsx
     */
    private void loadSecondOrderContributions() {
        XSSFSheet secondOrderGroupsSheet = thermoPropsWorkbook.getSheetAt(1);
        System.out.println("\nSECOND ORDER GROUPS Sheet: " + thermoPropsWorkbook.getSheetName(1));
        // Second Row
        int soGroupRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(soGroupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                ThermoPhysicalSecondOrderContribution secondOrderContribution = readSecondGroupContributionsParams(currentRow);
                readSecondOrderConfigurations(currentRow, secondOrderContribution);

                thermoPhysicalSecondOrderContributions.put(secondOrderContribution.getGroupsCase(), secondOrderContribution);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + soGroupRow);
                e.printStackTrace();
            } finally {
                currentRow = secondOrderGroupsSheet.getRow(++soGroupRow);
            }
        }
        thermoPhysicalSecondOrderContributions.forEach((integer, secondOrderContribution) -> debug(secondOrderContribution));
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
            Integer mainGroup = Integer.valueOf(mainGroupMatcher.group(0));

            currentGroup = new ContributionGroupNode(mainGroup);
            if (mainGroupNode == null)
                mainGroupNode = currentGroup;
            else
                formerGroupNode.getSubGroups().add(currentGroup);

            if (group.matches(".*\\(.*\\).*")) {
                Matcher subgroupMatcher = SUBGROUP_PATTERN.matcher(group);
                while (subgroupMatcher.find()) {
                    int groupMatch = Integer.valueOf(subgroupMatcher.group(1));
                    currentGroup.getSubGroups().add(new ContributionGroupNode(groupMatch));
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
     * Read the environmental parameters according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  FamilyGroups
     * In file
     * -  HukkerikarEnvironmental.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadEnvironmentalFirstOrderContributions() {
        XSSFSheet firstOrderGroupsSheet = environmantalWorkbook.getSheetAt(0);
        System.out.println("\nENVIRONMENTAL FIRST ORDER CONTRIBUTIONS Sheet: " + environmantalWorkbook.getSheetName(0));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = firstOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                EnvironmentalFirstOrderContribution environmentalContribution = readFirstOrderEnvironmentalContribution(currentRow);
                environmentalFirstOrderContributions.put(environmentalContribution.getCode(), environmentalContribution);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + groupRow);
                e.printStackTrace();
            }
            currentRow = firstOrderGroupsSheet.getRow(++groupRow);
        }
    }

    /**
     * Read the environmental parameters according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  FamilyGroups
     * In file
     * -  HukkerikarEnvironmental.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadEnvironmentalSecondOrderContributions() {
        XSSFSheet secondOrderGroupsSheet = environmantalWorkbook.getSheetAt(1);
        System.out.println("\nENVIRONMENTAL SECOND ORDER CONTRIBUTIONS Sheet: " + environmantalWorkbook.getSheetName(1));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                EnvironmentalSecondOrderContribution environmentalContribution = readSecondOrderEnvironmentalContribution(currentRow);
                readEnvironmentalSecondOrderConfigurations(currentRow, environmentalContribution);
                environmentalSecondOrderContributions.put(environmentalContribution.getCode(), environmentalContribution);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + groupRow);
                e.printStackTrace();
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
        System.out.println("\nENVIRONMENTAL FUNCTIONAL GROUPS EQUIVALENCES Sheet: " + environmantalWorkbook.getSheetName(3));
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
                    System.out.println("\nRow failed: " + groupRow);
                    e.printStackTrace();
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
        System.out.println("\nFAMILY GROUPS Sheet: " + unifacWorkbook.getSheetName(2));
        // Second Row
        int gcRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(gcRow);
        while (currentRow != null && currentRow.getCell(0) != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer familyIndex = (int) currentRow.getCell(0).getNumericCellValue();
                String familyName = currentRow.getCell(1).getStringCellValue();
                ContributionGroup.Family family = new ContributionGroup.Family(familyName);
                Iterator<Cell> cellsIterator = currentRow.cellIterator();
                while (cellsIterator.hasNext()) {
                    XSSFCell rowCell = (XSSFCell) cellsIterator.next();
                    if (rowCell.getColumnIndex() > 1 && validateNumericCell(rowCell)) {
                        int mainGroupCode = (int) rowCell.getNumericCellValue();
                        ContributionGroup.Main mainGroup = unifacMainGroups.get(mainGroupCode);
                        if (mainGroup != null)
                            family.getMainGroups().add(mainGroup);
                        else
                            System.out.println(String.format("main group %d not found", mainGroupCode));
                    }
                }
                debug(family);
                unifacFamilyGroups.put(familyIndex, family);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + gcRow);
                e.printStackTrace();
            }
            currentRow = secondOrderGroupsSheet.getRow(++gcRow);
        }
    }

    private boolean validateNumericCell(XSSFCell cell) {
        if (cell == null)
            return false;
        if (CellType.NUMERIC != cell.getCellTypeEnum() && CellType.BLANK != cell.getCellTypeEnum() && cell.getRichStringCellValue().toString().trim().length() > 0)
            warning(String.format("(!) %s : %s", cell.getReference(), cell.getRichStringCellValue()));
        return CellType.NUMERIC == cell.getCellTypeEnum();
    }

    private void debug(Object object) {
        //        System.out.println(object);
    }

    private void warning(Object object) {
        System.out.println(object);
    }
}

