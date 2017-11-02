package co.unal.camd.properties.parameters;


import co.unal.camd.properties.parameters.unifac.GroupContributionData;
import co.unal.camd.properties.parameters.unifac.ContributionGroup;
import co.unal.camd.properties.parameters.unifac.SecondOrderContributionData;
import co.unal.camd.properties.parameters.unifac.UnifacInteractionData;
import co.unal.camd.properties.parameters.unifac.UnifacParametersPair;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manage all the operations between the Molecules, and the UNIFAC method
 * to estimate properties
 *
 * @author Kevin Adrián Rodríguez Ruiz
 */
public class UnifacParameters2017 {

    /**
     * Unifac parameters file path in resources directory (/src/resources/)
     */
    private static String UNIFAC_WORKBOOK_PATH = "/properties/Unifac-2017.xlsx";
    private XSSFWorkbook unifacWorkbook;
    private static String THERMOPROPS_WORKBOOK_PATH = "/properties/ThermoPropsContributions.xlsx";
    private XSSFWorkbook contributionsWorkbook;

    @Getter
    private Map<UnifacParametersPair, UnifacInteractionData> unifacInteractions = new HashMap<>();

    @Getter
    private Map<Integer, GroupContributionData> groupContributions = new HashMap<>();

    private Map<Integer, ContributionGroup.Main> mainGroups = new HashMap<>();

    @Getter
    private List<ContributionGroup.Family> familyGroups = new ArrayList<>();

    @Getter
    private Map<Integer, SecondOrderContributionData> secondOrderGroupsContributions = new HashMap<>();

    //    UNIFAC Interaction Parameters Matrix and variables
    @Getter
    private String[][] mainGroupProbabilities = new String[100][3];

    /**
     * constructors for load the info
     */
    public UnifacParameters2017() {
        try (InputStream unifacWBIS = UnifacParameters2017.class.getResourceAsStream(UNIFAC_WORKBOOK_PATH);
             InputStream thermoContributionsWBIS = UnifacParameters2017.class.getResourceAsStream(THERMOPROPS_WORKBOOK_PATH)) {
            System.out.println("Loading UNIFAC parameters file: " + UNIFAC_WORKBOOK_PATH);
            unifacWorkbook = new XSSFWorkbook(unifacWBIS);
            System.out.println("Loading thermodynamical properties contributions parameters file: " + THERMOPROPS_WORKBOOK_PATH);
            contributionsWorkbook = new XSSFWorkbook(thermoContributionsWBIS);

            loadUnifacijInteractions();
            loadGroupContributions();
            loadSecondOrderContributions();
            loadFamilyGroups();

            unifacWBIS.close();
            thermoContributionsWBIS.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        XSSFRow currentRow = interactionsSheet.getRow(ijRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer iParam = (int) currentRow.getCell(0).getNumericCellValue();
                Integer jParam = (int) currentRow.getCell(1).getNumericCellValue();
                UnifacParametersPair parametersPair = new UnifacParametersPair(iParam, jParam);
                UnifacInteractionData unifacData = new UnifacInteractionData(parametersPair);
                readInteractionsCells(currentRow, unifacData);
                System.out.println(unifacData);
                unifacInteractions.put(parametersPair, unifacData);
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
        loadThermoGroupContributions();
        groupContributions.forEach((integer, groupContribution) -> System.out.println(groupContribution));
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
                GroupContributionData contributionData = new GroupContributionData(groupId);
                readUnifacRQParams(currentRow, contributionData);
                groupContributions.put(groupId, contributionData);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + unifacRow);
                e.printStackTrace();
            }
            currentRow = unifacRQSheet.getRow(++unifacRow);
        }
    }

    private void readUnifacRQParams(XSSFRow currentRow, GroupContributionData contributionData) {
        XSSFCell rowCell;
        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell)) {
            int mainGroupId = (int) rowCell.getNumericCellValue();
            ContributionGroup.Main mainGroup = mainGroups.putIfAbsent(mainGroupId, new ContributionGroup.Main(mainGroupId, currentRow.getCell(2).getStringCellValue()));
            contributionData.setMainGroup(mainGroup);
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

    /**
     * Read of the parameters required in the estimation of density, boiling point, melting point, Gibss free energy and dielectric constant for different contribution groups from the sheet:
     * -  Contributions
     * In file
     * -  ThermoPropsContributions.xlsx
     */
    private void loadThermoGroupContributions() {
        XSSFSheet contributionsSheet = contributionsWorkbook.getSheetAt(0);
        System.out.println("\nTHERMODYNAMICAL PROPERTIES Sheet: " + contributionsWorkbook.getSheetName(0));
        // Second Row
        int tgcRow = 1;

        XSSFRow currentRow = contributionsSheet.getRow(tgcRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                GroupContributionData contributionData = groupContributions.get(groupId);
                readThermoContributionsParams(currentRow, contributionData);
                groupContributions.put(groupId, contributionData);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + tgcRow);
                e.printStackTrace();
            }
            currentRow = contributionsSheet.getRow(++tgcRow);
        }
    }

    private void readThermoContributionsParams(XSSFRow currentRow, GroupContributionData contributionData) {
        XSSFCell rowCell;

        rowCell = currentRow.getCell(3);
        if (rowCell != null)
            System.out.println(String.format(">> %s should match %s", rowCell.getStringCellValue(), contributionData.getGroupName()));

        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell))
            contributionData.setValence((int) rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            contributionData.setMolecularWeight(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            contributionData.setBoilingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            contributionData.setMeltingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(7);
        if (validateNumericCell(rowCell))
            contributionData.setFreeEnergy(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(8);
        if (validateNumericCell(rowCell))
            contributionData.setDipoleMoment(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(9);
        if (validateNumericCell(rowCell))
            contributionData.setDipoleMomentH1i(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(10);
        if (validateNumericCell(rowCell))
            contributionData.setDipoleMomentH1i(rowCell.getNumericCellValue());

        // DENSITY PARAMETERS
        rowCell = currentRow.getCell(11);
        if (validateNumericCell(rowCell))
            contributionData.setDensityAd1(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(12);
        if (validateNumericCell(rowCell))
            contributionData.setDensityBd1(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(13);
        if (validateNumericCell(rowCell))
            contributionData.setDensityCd1(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(14);
        if (validateNumericCell(rowCell))
            contributionData.setDensityAd2(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(15);
        if (validateNumericCell(rowCell))
            contributionData.setDensityBd2(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(16);
        if (validateNumericCell(rowCell))
            contributionData.setDensityCd2(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(17);
        if (validateNumericCell(rowCell))
            contributionData.setDensityAd3(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(18);
        if (validateNumericCell(rowCell))
            contributionData.setDensityBd3(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(19);
        if (validateNumericCell(rowCell))
            contributionData.setDensityCd4(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(21);
        if (validateNumericCell(rowCell))
            contributionData.setDensityAd4(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(21);
        if (validateNumericCell(rowCell))
            contributionData.setDensityBd4(rowCell.getNumericCellValue());
        rowCell = currentRow.getCell(22);
        if (validateNumericCell(rowCell))
            contributionData.setLiquidMolarVolume(rowCell.getNumericCellValue());
    }

    /**
     * Read of the parameters required in the estimation of density, boiling point, melting point, Gibss free energy and dielectric constant for different contribution groups from the sheet:
     * -  SecondOrdenParams
     * In file
     * -  ThermoPropsContributions.xlsx
     */
    private void loadSecondOrderContributions() {
        XSSFSheet secondOrderGroupsSheet = contributionsWorkbook.getSheetAt(1);
        System.out.println("\nSECOND ORDER GROUPS Sheet: " + contributionsWorkbook.getSheetName(1));
        // Second Row
        int soGroupRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(soGroupRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupsCase = (int) currentRow.getCell(0).getNumericCellValue();
                SecondOrderContributionData secondOrderContribution = new SecondOrderContributionData(groupsCase);
                readSecondGroupContributionsParams(currentRow, secondOrderContribution);
                secondOrderGroupsContributions.put(groupsCase, secondOrderContribution);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + soGroupRow);
                e.printStackTrace();
            }
            currentRow = secondOrderGroupsSheet.getRow(++soGroupRow);
        }
        loadSecondOrderRelationships();
        secondOrderGroupsContributions.forEach((integer, secondOrderContribution) -> System.out.println(secondOrderContribution));
    }

    private void readSecondGroupContributionsParams(XSSFRow currentRow, SecondOrderContributionData secondOrderContribution) {
        XSSFCell rowCell;
        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell))
            secondOrderContribution.setBoilingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(2);
        if (rowCell != null)
            secondOrderContribution.setMeltingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(3);
        if (validateNumericCell(rowCell))
            secondOrderContribution.setGibbsEnergy(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            secondOrderContribution.setLiquidMolarVolume(rowCell.getNumericCellValue());
    }

    /**
     * Read the groups relationships between the second order parameters and the contribution groups from the sheet:
     * -  SecondOrdenRels
     * In file
     * -  ThermoPropsContributions.xlsx
     */
    private void loadSecondOrderRelationships() {
        XSSFSheet secondOrderGroupsSheet = contributionsWorkbook.getSheetAt(2);
        System.out.println("\nSECOND ORDER RELATIONSHIPS Sheet: " + contributionsWorkbook.getSheetName(2));
        // Second Row
        int soRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(soRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupCase = (int) currentRow.getCell(0).getNumericCellValue();
                SecondOrderContributionData secondOrderContribution = secondOrderGroupsContributions.get(groupCase);
                List<Integer> contributionsGroups = new ArrayList<>();
                Iterator<Cell> cellsIterator = currentRow.cellIterator();
                while (cellsIterator.hasNext()) {
                    XSSFCell rowCell = (XSSFCell) cellsIterator.next();
                    if (rowCell.getColumnIndex() > 0 && validateNumericCell(rowCell))
                        contributionsGroups.add((int) rowCell.getNumericCellValue());
                }
                Integer[] groupsArray = contributionsGroups.toArray(new Integer[0]);
                secondOrderContribution.getGroupsConfigurations().add(groupsArray);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + soRow);
                e.printStackTrace();
            }
            currentRow = secondOrderGroupsSheet.getRow(++soRow);
        }
    }

    /**
     * Read the family groups as seen in the right side groups selector
     *
     * @see co.unal.camd.view.ContributionGroupsPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    private void loadFamilyGroups() {
        XSSFSheet secondOrderGroupsSheet = unifacWorkbook.getSheetAt(2);
        System.out.println("\nFAMILY GROUPS Sheet: " + unifacWorkbook.getSheetName(2));
        // Second Row
        int gcRow = 1;

        XSSFRow currentRow = secondOrderGroupsSheet.getRow(gcRow);
        while (currentRow != null && currentRow.getCell(0) != null && validateNumericCell(currentRow.getCell(1))) {
            try {
                String familyName = currentRow.getCell(0).getStringCellValue();
                ContributionGroup.Family family = new ContributionGroup.Family(familyName);
                Iterator<Cell> cellsIterator = currentRow.cellIterator();
                while (cellsIterator.hasNext()) {
                    XSSFCell rowCell = (XSSFCell) cellsIterator.next();
                    if (rowCell.getColumnIndex() > 0 && validateNumericCell(rowCell)) {
                        int mainGroupId = (int) rowCell.getNumericCellValue();
                        ContributionGroup.Main mainGroup = mainGroups.get(mainGroupId);
                        if (mainGroup != null)
                            family.getMainGroups().add(mainGroup);
                        else
                            System.out.println(String.format("main group %d not found", mainGroupId));
                    }
                }
                System.out.println(family);
                familyGroups.add(family);
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
        if (CellType.NUMERIC != cell.getCellTypeEnum() && CellType.BLANK != cell.getCellTypeEnum())
            System.out.println(String.format("(!) %s : %s", cell.getReference(), cell.getRichStringCellValue()));
        return CellType.NUMERIC == cell.getCellTypeEnum();
    }

}

