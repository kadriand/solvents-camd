package co.unal.camd.methods;


import co.unal.camd.methods.gani.GaniThermoPhysicalFirstOrderContribution;
import co.unal.camd.methods.gani.GaniThermoPhysicalSecondOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;
import co.unal.camd.methods.unifac.FamilyGroup;
import co.unal.camd.methods.unifac.UnifacMainGroup;
import co.unal.camd.methods.unifac.UnifacSubGroup;
import co.unal.camd.methods.unifac.UnifacPairInteractions;
import co.unal.camd.methods.unifac.Unifacij;
import co.unal.camd.model.molecule.UnifacGroupNode;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static co.unal.camd.methods.ParametersParserUtils.validateNumericCell;

/**
 * Manage all the parameters required to estimate properties using the Unifac method, Gani-Marrero method, ...
 *
 * @author Kevin Adrián Rodríguez Ruiz
 */
public class EstimationConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(EstimationConstants.class);

    /**
     * A functional unit is every atom different from carbon, or every Carbon with triple bond
     */
    private Pattern STRONG_GROUPS_PATTERN = Pattern.compile("[A-BD-Z](?:[a-z]*)|C[a-z]|C[\\W]*#");

    /**
     * A true to this matcher means that the group has Carbon atoms
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
    private static String HUKKERIKAR_PROPS_WORKBOOK_PATH = "/properties/Hukkerikar.xlsx";
    private XSSFWorkbook hukkerikarWorkbook;

    @Getter
    protected Map<Unifacij, UnifacPairInteractions> unifacInteractions = new HashMap<>();

    /**
     * <UNIFAC code, thermo-physical first order contributions>
     */
    @Getter
    protected Map<Integer, UnifacSubGroup> unifacContributions = new HashMap<>();

    /**
     * <valence, list of c. groups>
     */
    @Getter
    protected Map<Integer, List<UnifacSubGroup>> valenceContributionGroups = new HashMap<Integer, List<UnifacSubGroup>>() {{
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
    protected Map<Integer, UnifacMainGroup> unifacMainGroups = new HashMap<>();

    /**
     * List of C. groups families, its probabilities are also stored
     * <family group code, familiy group>
     */
    @Getter
    protected Map<Integer, FamilyGroup> unifacFamilyGroups = new HashMap<>();

    /**
     * <UNIFAC code, thermo-physical first order contributions>
     */
    @Getter
    protected Map<Integer, GaniThermoPhysicalFirstOrderContribution> thermoPhysicalFirstOrderContributions = new HashMap<>();

    /**
     * <contribution case, second order data>
     */
    @Getter
    protected Map<Integer, GaniThermoPhysicalSecondOrderContribution> thermoPhysicalSecondOrderContributions = new HashMap<>();

    // ENVIRONMENTAL CONTRIBUTIONS

    /**
     * <code, first order contributions>
     */
    @Getter
    protected Map<Integer, HukkerikarFirstOrderContribution> hukkerikarFirstOrderContributions = new HashMap<>();

    /**
     * <code, second order contributions>
     */
    @Getter
    protected Map<Integer, HukkerikarSecondOrderContribution> hukkerikarSecondOrderContributions = new HashMap<>();

    /**
     * <UNIFAC group Id, Hukkerikar group Id>
     */
    @Getter
    protected Map<Integer, Integer> unifacHukkerikarGroupsEquivalences = new HashMap<>();

    /**
     * constructors for load the info
     */
    public EstimationConstants() {
        try (InputStream unifacWBIS = EstimationConstants.class.getResourceAsStream(UNIFAC_WORKBOOK_PATH);
             InputStream thermoContributionsWBIS = EstimationConstants.class.getResourceAsStream(THERMOPROPS_WORKBOOK_PATH);
             InputStream environmentalContributionsWBIS = EstimationConstants.class.getResourceAsStream(HUKKERIKAR_PROPS_WORKBOOK_PATH);) {
            LOGGER.info("Loading UNIFAC groups file: {}", UNIFAC_WORKBOOK_PATH);
            unifacWorkbook = new XSSFWorkbook(unifacWBIS);
            LOGGER.info("Loading thermodynamical properties contributions groups file: {}", THERMOPROPS_WORKBOOK_PATH);
            thermoPropsWorkbook = new XSSFWorkbook(thermoContributionsWBIS);

            LOGGER.info("Loading environmental properties contributions groups file: {}", HUKKERIKAR_PROPS_WORKBOOK_PATH);
            hukkerikarWorkbook = new XSSFWorkbook(environmentalContributionsWBIS);

            loadUnifacijInteractions();
            loadGroupContributions();
            loadSecondOrderContributions();
            loadFamilyGroups();
            loadHukkerikarEquivalences();
            loadHukkerikarThermoPhysicalFirstOrderContributions();
            loadHukkerikarEnvironmentalFirstOrderContributions();
            loadHukkerikarSecondOrderConfigurations();
            loadHukkerikarThermoPhysicalSecondOrderContributions();
            loadHukkerikarEnvironmentalSecondOrderContributions();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        Unifacij parametersPair = new Unifacij(0, 0);
        UnifacPairInteractions unifacInteractions = new UnifacPairInteractions(parametersPair).setAij(0.0).setBij(0.0).setCij(0.0).setAji(0.0).setBji(0.0).setCji(0.0);
        this.unifacInteractions.put(parametersPair, unifacInteractions);

        XSSFRow currentRow = interactionsSheet.getRow(ijRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer iParam = (int) currentRow.getCell(0).getNumericCellValue();
                Integer jParam = (int) currentRow.getCell(1).getNumericCellValue();
                parametersPair = new Unifacij(iParam, jParam);
                unifacInteractions = new UnifacPairInteractions(parametersPair);
                readInteractionsCells(currentRow, unifacInteractions);
                LOGGER.debug("unifacInteractions {}", unifacInteractions);
                this.unifacInteractions.put(parametersPair, unifacInteractions);
            } catch (Exception e) {
                LOGGER.error("\nRow failed: {}", ijRow, e);
            }
            currentRow = interactionsSheet.getRow(++ijRow);
        }
    }

    private void readInteractionsCells(XSSFRow currentRow, UnifacPairInteractions interactionData) {
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
        unifacContributions.forEach((integer, groupContribution) -> LOGGER.debug("groupContribution {}", groupContribution));
        ParametersParserUtils.unifacContributions = this.unifacContributions;
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
                UnifacSubGroup firstOrderContribution = new UnifacSubGroup(groupId);
                readUnifacRQParams(currentRow, firstOrderContribution);
                readSmiles(currentRow, firstOrderContribution);
                unifacContributions.put(groupId, firstOrderContribution);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", unifacRow, e);
            }
            currentRow = unifacRQSheet.getRow(++unifacRow);
        }
    }

    private void readUnifacRQParams(XSSFRow currentRow, UnifacSubGroup unifacSubGroup) {
        XSSFCell rowCell;
        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell)) {
            int mainGroupId = (int) rowCell.getNumericCellValue();
            UnifacMainGroup unifacMainGroup = unifacMainGroups.getOrDefault(mainGroupId, new UnifacMainGroup(mainGroupId, currentRow.getCell(2).getStringCellValue()));
            unifacMainGroups.putIfAbsent(mainGroupId, unifacMainGroup);
            unifacSubGroup.setMainGroup(unifacMainGroup);
            unifacMainGroup.getSubGroups().add(unifacSubGroup);
        }

        rowCell = currentRow.getCell(3);
        if (rowCell != null)
            unifacSubGroup.setGroupName(rowCell.getStringCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            unifacSubGroup.setRParam(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            unifacSubGroup.setQParam(rowCell.getNumericCellValue());
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
                if (unifacContributions.containsKey(groupId)) {
                    UnifacSubGroup unifacSubGroup = unifacContributions.get(groupId);
                    GaniThermoPhysicalFirstOrderContribution firstOrderContribution = new GaniThermoPhysicalFirstOrderContribution(unifacSubGroup);
                    readThermodynamicContributions(currentRow, firstOrderContribution);
                    this.thermoPhysicalFirstOrderContributions.put(groupId, firstOrderContribution);
                } else
                    LOGGER.warn("No thermo Physical First Order Contribution found for group Id : {}", groupId);
            } catch (Exception e) {
                LOGGER.error("\nRow failed: {}", tgcRow, e);
            }
            currentRow = contributionsSheet.getRow(++tgcRow);
        }
    }

    private void readSmiles(XSSFRow currentRow, UnifacSubGroup unifacSubGroup) {
        XSSFCell rowCell = currentRow.getCell(6);
        if (rowCell == null)
            return;
        String smilesPattern = rowCell.getStringCellValue().trim();
        unifacSubGroup.setSmilesPattern(smilesPattern);
        unifacSubGroup.setAliphaticContent(smilesPattern.matches(HAS_CARBON_MATCHER));
        unifacSubGroup.setStrongGroupsNumber(countStrongGroups(smilesPattern));
    }

    private int countStrongGroups(String smilesPattern) {
        Matcher matcher = STRONG_GROUPS_PATTERN.matcher(smilesPattern);
        int occurrences = 0;
        while (matcher.find())
            occurrences++;
        //        LOGGER.info("{} : elems {} - carbon {}", smilesPattern + " - " + occurrences + " - " + smilesPattern.matches(HAS_CARBON_MATCHER));
        return occurrences;
    }

    private void readThermodynamicContributions(XSSFRow currentRow, GaniThermoPhysicalFirstOrderContribution thermodynamicContribution) {
        XSSFCell rowCell;

        rowCell = currentRow.getCell(3);
        if (rowCell != null)
            LOGGER.debug(String.format(">> %s should match %s", rowCell.getStringCellValue(), thermodynamicContribution.getGroupName()));

        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell)) {
            int valence = (int) rowCell.getNumericCellValue();
            thermodynamicContribution.getUnifacSubGroup().setValence(valence);
            valenceContributionGroups.get(valence).add(thermodynamicContribution.getUnifacSubGroup());
        }

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            thermodynamicContribution.getUnifacSubGroup().setMolecularWeight(rowCell.getNumericCellValue());

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
                GaniThermoPhysicalSecondOrderContribution secondOrderContribution = readSecondGroupContributionsParams(currentRow);
                readSecondOrderConfigurations(currentRow, secondOrderContribution);

                thermoPhysicalSecondOrderContributions.put(secondOrderContribution.getCode(), secondOrderContribution);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", soGroupRow, e);
            } finally {
                currentRow = secondOrderGroupsSheet.getRow(++soGroupRow);
            }
        }
        thermoPhysicalSecondOrderContributions.forEach((integer, secondOrderContribution) -> LOGGER.debug("secondOrderContribution {}", secondOrderContribution));
    }

    private GaniThermoPhysicalSecondOrderContribution readSecondGroupContributionsParams(XSSFRow currentRow) {
        Integer groupsCase = (int) currentRow.getCell(0).getNumericCellValue();
        GaniThermoPhysicalSecondOrderContribution secondOrderContribution = new GaniThermoPhysicalSecondOrderContribution(groupsCase);

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

    private void readSecondOrderConfigurations(XSSFRow currentRow, GaniThermoPhysicalSecondOrderContribution secondOrderContribution) {
        XSSFCell rowCell = currentRow.getCell(6);
        if (rowCell == null)
            return;

        String rawGroupCases = rowCell.getCellTypeEnum() == CellType.NUMERIC ? NumberToTextConverter.toText(rowCell.getNumericCellValue()) : rowCell.getStringCellValue().trim();
        String[] differentAlternatives = rawGroupCases.split("/");
        for (String differentAlternative : differentAlternatives) {
            List<String> singleConfigurations = ParametersParserUtils.breakConfigurationAlternatives(differentAlternative);
            singleConfigurations.forEach(singleConfiguration -> {
                UnifacGroupNode secondGroupConfiguration = ParametersParserUtils.parseNodeGroupsConfiguration(singleConfiguration);
                secondOrderContribution.getGroupConfigurations().add(secondGroupConfiguration);
                appendToFirstOrderContributions(singleConfiguration, secondGroupConfiguration, secondOrderContribution);
            });
        }
    }

    private void appendToFirstOrderContributions(String singleConfiguration, UnifacGroupNode secondGroupConfiguration, GaniThermoPhysicalSecondOrderContribution secondOrderContribution) {
        String[] rawGroups = singleConfiguration.split("[|().]+");
        int[] groups = Stream.of(rawGroups).mapToInt(Integer::parseInt).toArray();
        int biggerGroup = Arrays.stream(groups).max().getAsInt();
        if (thermoPhysicalFirstOrderContributions.containsKey(biggerGroup))
            thermoPhysicalFirstOrderContributions.get(biggerGroup).getSecondOrderContributions().put(secondGroupConfiguration, secondOrderContribution);
    }

    /**
     * Read the equivalences between the unifac groups and the Hukkerikar groups
     * Sheet;
     * -  first-equivalences
     * In file
     * -  Hukkerikar.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadHukkerikarEquivalences() {
        XSSFSheet equivalencesSheet = hukkerikarWorkbook.getSheetAt(0);
        LOGGER.info("");
        LOGGER.info("ENVIRONMENTAL FUNCTIONAL GROUPS EQUIVALENCES Sheet: {}", hukkerikarWorkbook.getSheetName(0));

        // Third Row
        int groupRow = 2;

        XSSFRow currentRow = equivalencesSheet.getRow(groupRow);
        while (currentRow != null && (validateNumericCell(currentRow.getCell(0)) || validateNumericCell(currentRow.getCell(5)))) {
            if (validateNumericCell(currentRow.getCell(0)) && validateNumericCell(currentRow.getCell(5)))
                try {
                    int unifacGroupId = (int) currentRow.getCell(0).getNumericCellValue();
                    int hukkerikarGroupId = (int) currentRow.getCell(5).getNumericCellValue();
                    unifacHukkerikarGroupsEquivalences.put(unifacGroupId, hukkerikarGroupId);
                    HukkerikarFirstOrderContribution firstOrderContribution = new HukkerikarFirstOrderContribution(hukkerikarGroupId, unifacGroupId);
                    hukkerikarFirstOrderContributions.put(firstOrderContribution.getCode(), firstOrderContribution);
                } catch (Exception e) {
                    LOGGER.error("Row failed: {}", groupRow, e);
                }
            currentRow = equivalencesSheet.getRow(++groupRow);
        }
        HukkerikarParser.hukkerikarGroupsEquivalences = this.unifacHukkerikarGroupsEquivalences;
        HukkerikarParser.hukkerikarFirstOrderContributions = this.hukkerikarFirstOrderContributions;
    }

    /**
     * Read the Thermo-Physical  groups according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  therm-phys-first
     * In file
     * -  Hukkerikar.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadHukkerikarThermoPhysicalFirstOrderContributions() {
        XSSFSheet firstOrderGroupsSheet = hukkerikarWorkbook.getSheetAt(2);
        LOGGER.info("");
        LOGGER.info("HUKKERIKAR THERMO-PHYSICAL FIRST ORDER CONTRIBUTIONS Sheet: {}", hukkerikarWorkbook.getSheetName(2));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = firstOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                HukkerikarFirstOrderContribution firstOrderContribution = hukkerikarFirstOrderContributions.getOrDefault(groupId, new HukkerikarFirstOrderContribution(groupId));
                hukkerikarFirstOrderContributions.putIfAbsent(firstOrderContribution.getCode(), firstOrderContribution);
                HukkerikarParser.readFirstOrderHukkerikarThermoPhysicalContribution(firstOrderContribution, currentRow);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", groupRow, e);
            }
            currentRow = firstOrderGroupsSheet.getRow(++groupRow);
        }
    }

    /**
     * Read the environmental groups according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  environmental-first
     * In file
     * -  Hukkerikar.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadHukkerikarEnvironmentalFirstOrderContributions() {
        XSSFSheet firstOrderGroupsSheet = hukkerikarWorkbook.getSheetAt(5);
        LOGGER.info("");
        LOGGER.info("HUKKERIKAR ENVIRONMENTAL FIRST ORDER CONTRIBUTIONS Sheet: {}", hukkerikarWorkbook.getSheetName(5));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = firstOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                HukkerikarFirstOrderContribution firstOrderContribution = hukkerikarFirstOrderContributions.getOrDefault(groupId, new HukkerikarFirstOrderContribution(groupId));
                hukkerikarFirstOrderContributions.putIfAbsent(firstOrderContribution.getCode(), firstOrderContribution);
                HukkerikarParser.readFirstOrderHukkerikarEnvironmentalContribution(firstOrderContribution, currentRow);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", groupRow, e);
            }
            currentRow = firstOrderGroupsSheet.getRow(++groupRow);
        }
    }

    /**
     * Read the environmental groups according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  second-equivalences
     * In file
     * -  Hukkerikar.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadHukkerikarSecondOrderConfigurations() {
        XSSFSheet secondOrderGroupsSheet = hukkerikarWorkbook.getSheetAt(1);
        LOGGER.info("");
        LOGGER.info("ENVIRONMENTAL SECOND ORDER CONTRIBUTIONS Sheet: {}", hukkerikarWorkbook.getSheetName(1));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                HukkerikarSecondOrderContribution environmentalContribution = HukkerikarParser.readHukkerikarSecondOrderConfigurations(currentRow);
                hukkerikarSecondOrderContributions.put(environmentalContribution.getCode(), environmentalContribution);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", groupRow, e);
            }
            currentRow = secondOrderGroupsSheet.getRow(++groupRow);
        }
    }

    /**
     * Read the environmental groups according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  therm-phys-second
     * In file
     * -  Hukkerikar.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadHukkerikarThermoPhysicalSecondOrderContributions() {
        XSSFSheet secondOrderGroupsSheet = hukkerikarWorkbook.getSheetAt(3);
        LOGGER.info("");
        LOGGER.info("ENVIRONMENTAL SECOND ORDER CONTRIBUTIONS Sheet: {}", hukkerikarWorkbook.getSheetName(3));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                HukkerikarSecondOrderContribution secondOrderContribution = hukkerikarSecondOrderContributions.getOrDefault(groupId, new HukkerikarSecondOrderContribution(groupId));
                hukkerikarSecondOrderContributions.putIfAbsent(secondOrderContribution.getCode(), secondOrderContribution);
                HukkerikarParser.readSecondOrderThermoPhysicalContribution(secondOrderContribution, currentRow);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", groupRow, e);
            }
            currentRow = secondOrderGroupsSheet.getRow(++groupRow);
        }
    }

    /**
     * Read the environmental groups according to the functional groups defined by Hukkerikar
     * Sheet;
     * -  environmental-second
     * In file
     * -  Hukkerikar.xlsx
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadHukkerikarEnvironmentalSecondOrderContributions() {
        XSSFSheet secondOrderGroupsSheet = hukkerikarWorkbook.getSheetAt(6);
        LOGGER.info("");
        LOGGER.info("ENVIRONMENTAL SECOND ORDER CONTRIBUTIONS Sheet: {}", hukkerikarWorkbook.getSheetName(6));
        // Second Row
        int groupRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(groupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                HukkerikarSecondOrderContribution secondOrderContribution = hukkerikarSecondOrderContributions.getOrDefault(groupId, new HukkerikarSecondOrderContribution(groupId));
                hukkerikarSecondOrderContributions.putIfAbsent(secondOrderContribution.getCode(), secondOrderContribution);
                HukkerikarParser.readSecondOrderEnvironmentalContribution(secondOrderContribution, currentRow);
            } catch (Exception e) {
                LOGGER.error("Row failed: {}", groupRow, e);
            }
            currentRow = secondOrderGroupsSheet.getRow(++groupRow);
        }
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
                        UnifacMainGroup unifacMainGroup = unifacMainGroups.get(mainGroupCode);
                        if (unifacMainGroup != null) {
                            familyGroup.getMainGroups().add(unifacMainGroup);
                            unifacMainGroup.setFamilyGroup(familyGroup);
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

    public void defaultFamilyProbabilities() {
        CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().entrySet().stream()
                .filter(familyEntry -> IntStream.of(ProblemParameters.DEFAULT_UNCHECKED_FAMILIES).anyMatch(i -> i == familyEntry.getKey()))
                .forEach(familyGroupEntry -> familyGroupEntry.getValue().setProbability(0));
    }

    /**
     * Parse an Unifac groups configuration String into a {@link UnifacGroupNode} object
     *
     * @param textConfiguration Unifac configuration
     * @return UnifacGroupNode
     */
    public UnifacGroupNode parseGroupsConfiguration(String textConfiguration) {
        return ParametersParserUtils.parseNodeGroupsConfiguration(textConfiguration);
    }
}

