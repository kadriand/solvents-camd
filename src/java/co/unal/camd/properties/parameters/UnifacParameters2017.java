package co.unal.camd.properties.parameters;


import co.unal.camd.properties.parameters.unifac.GroupContributionData;
import co.unal.camd.properties.parameters.unifac.UnifacInteractionData;
import co.unal.camd.properties.parameters.unifac.UnifacParametersPair;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manage all the operations between the Molecules, and the UNIFAC method
 * to estimate properties
 *
 * @author FAMILIA MORENO
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

    //    UNIFAC Interaction Parameters Matrix and variables
    @Getter
    private String[][][] ijParams = new String[3][1000][1000];
    @Getter
    private String[][][] groupsData = new String[8][50][50];
    @Getter
    private String[][][] secondOrderParameters = new String[2][210][7];
    @Getter
    private String[][] mainGroupProbabilities = new String[100][3];

    /**
     * constructors for load the info
     */
    public UnifacParameters2017() {
        try (InputStream unifacWBIS = UnifacParameters2017.class.getResourceAsStream(UNIFAC_WORKBOOK_PATH);
             InputStream thermoContributionsWBIS = UnifacParameters2017.class.getResourceAsStream(THERMOPROPS_WORKBOOK_PATH)) {
            System.out.println("Loading UNIFAC parameters file");
            unifacWorkbook = new XSSFWorkbook(unifacWBIS);
            contributionsWorkbook = new XSSFWorkbook(thermoContributionsWBIS);
            loadijInteractions();
            loadUnifacGroupContributions();
            loadThermoGroupContributions();

            loadGroupsData();
            loadSecondOrderParameters();
            loadProbabilities();

            unifacWBIS.close();
            thermoContributionsWBIS.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Read of sheet:
     * -  UNIFAC-DORTMUND-Interactions
     */
    private void loadijInteractions() {
        int row;
        XSSFSheet interactionsSheet = unifacWorkbook.getSheetAt(0);
        System.out.println("UNIFAC DORTMUND PARAMETERTS sheet: " + unifacWorkbook.getSheetName(0));
        row = 1;

        // Second Row
        XSSFRow currentRow = interactionsSheet.getRow(row);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer iParam = (int) currentRow.getCell(0).getNumericCellValue();
                Integer jParam = (int) currentRow.getCell(1).getNumericCellValue();

                UnifacParametersPair parametersPair = new UnifacParametersPair(iParam, jParam);
                UnifacInteractionData interactionData = new UnifacInteractionData(parametersPair);

                readInteractionsCells(currentRow, interactionData);
                System.out.println(interactionData);

                unifacInteractions.put(parametersPair, interactionData);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + row);
                e.printStackTrace();
            }

            currentRow = interactionsSheet.getRow(++row);
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

    /**
     * Read of sheet:
     * -  UNIFAC-DORTMUND-Interactions
     */
    private void loadUnifacGroupContributions() {
        int unifacRow;
        XSSFSheet unifacRQSheet = unifacWorkbook.getSheetAt(1);
        System.out.println("UNIFAC R AND Q sheet: " + unifacWorkbook.getSheetName(1));
        unifacRow = 1;


        //TODO CONTINUE

        // Second Row
        XSSFRow currentRow = unifacRQSheet.getRow(unifacRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                GroupContributionData contributionData = new GroupContributionData(groupId);
                readUnifacGroupsParams(currentRow, contributionData);
                System.out.println(contributionData);
                groupContributions.put(groupId, contributionData);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + unifacRow);
                e.printStackTrace();
            }

            currentRow = unifacRQSheet.getRow(++unifacRow);
        }

    }

    /**
     * Read of sheet:
     * -  UNIFAC-DORTMUND-Interactions
     */
    private void loadThermoGroupContributions() {
        int gcRow;
        XSSFSheet contributionsSheet = unifacWorkbook.getSheetAt(0);
        System.out.println("UNIFAC R AND Q Hoja: " + unifacWorkbook.getSheetName(0));
        gcRow = 1;

        // Second Row
        XSSFRow currentRow = contributionsSheet.getRow(gcRow);
        while (currentRow != null && validateNumericCell(currentRow.getCell(0))) {
            try {
                Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
                GroupContributionData contributionData = new GroupContributionData(groupId);
                readUnifacGroupsParams(currentRow, contributionData);
                System.out.println(contributionData);
                groupContributions.put(groupId, contributionData);
            } catch (Exception e) {
                System.out.println("\nRow failed: " + gcRow);
                e.printStackTrace();
            }

            currentRow = contributionsSheet.getRow(++gcRow);
        }

    }

    private void readUnifacGroupsParams(XSSFRow currentRow, GroupContributionData contributionData) {
        XSSFCell rowCell;
        rowCell = currentRow.getCell(1);
        if (validateNumericCell(rowCell))
            contributionData.setMainGroup((int) rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(2);
        if (rowCell != null)
            contributionData.setGroupName(rowCell.getStringCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            contributionData.setRParam(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            contributionData.setQParam(rowCell.getNumericCellValue());
    }

    private boolean validateNumericCell(XSSFCell nextRowCell) {
        if (nextRowCell == null)
            return false;
        if (CellType.NUMERIC != nextRowCell.getCellTypeEnum() && CellType.BLANK != nextRowCell.getCellTypeEnum())
            System.out.println("*" + nextRowCell.getStringCellValue());
        return CellType.NUMERIC == nextRowCell.getCellTypeEnum();
    }

    /**
     * load the information of all groups, call the excel document, from the sheet 4 up to the 10
     * <p>
     * The order of the sheets correspond to tree valences 1-4 and the ones for ar cy and 0
     */
    private void loadGroupsData() {
        int row;
        int col;

        for (byte i = 0; i < 7; i++) {
            XSSFSheet sheet = unifacWorkbook.getSheetAt(i + 3);
            System.out.println("FIRST ORDER GROUPS Hoja: " + unifacWorkbook.getSheetName(i + 3));
            Iterator rowIterator = sheet.rowIterator();
            row = 0;
            while (rowIterator.hasNext()) {
                XSSFRow fila = (XSSFRow) rowIterator.next();
                Iterator cellIterator = fila.cellIterator();
                col = 0;
                while (cellIterator.hasNext()) {
                    XSSFCell cell = (XSSFCell) cellIterator.next();
                    groupsData[i][row][col] = cell.toString();
                    col++;
                }
                row++;
            }
        }
    }

    /**
     * load the information of the second order groups, call the excel document, from the sheets 11 and 12
     * <p>
     * The order of the sheets correspond to tree valences 1-4 and the ones for ar cy and 0
     */
    private void loadSecondOrderParameters() {
        int row;
        int col;
        for (byte i = 0; i < 2; i++) {
            XSSFSheet sheet = unifacWorkbook.getSheetAt(i + 11);
            System.out.println("SECOND ORDER PARAMETERS Hoja: " + unifacWorkbook.getSheetName(i + 11));
            Iterator iteratorSheets = sheet.rowIterator();
            row = 0;
            while (iteratorSheets.hasNext()) {
                XSSFRow fila = (XSSFRow) iteratorSheets.next();
                Iterator iteratorCeldas = fila.cellIterator();
                col = 0;
                while (iteratorCeldas.hasNext()) {
                    XSSFCell celda = (XSSFCell) iteratorCeldas.next();
                    secondOrderParameters[i][row][col] = celda.toString();
                    col = col + 1;
                }
                row = row + 1;
            }
        }
    }

    private void loadProbabilities() {
        int row;
        int col;
        XSSFSheet sheet = unifacWorkbook.getSheetAt(13);
        System.out.println("Hoja: " + unifacWorkbook.getSheetName(13));
        Iterator iteratorSheets = sheet.rowIterator();
        row = 0;
        while (iteratorSheets.hasNext()) {
            XSSFRow fila = (XSSFRow) iteratorSheets.next();
            Iterator iteratorCeldas = fila.cellIterator();
            col = 0;
            while (iteratorCeldas.hasNext()) {
                XSSFCell celda = (XSSFCell) iteratorCeldas.next();
                mainGroupProbabilities[row][col] = celda.toString();
                //System.out.print("\t" + celda.readableString());
                // Imprime el contenido de la celda (valores o formulas)
                col = col + 1;
            }
            row = row + 1;
        }
    }
}

