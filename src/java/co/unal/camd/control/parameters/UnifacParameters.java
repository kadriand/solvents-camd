package co.unal.camd.control.parameters;


import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.InputStream;
import java.util.Iterator;

/**
 * Manage all the operations between the Molecules, and the UNIFAC method
 * to estimate properties
 *
 * @author FAMILIA MORENO
 */
public class UnifacParameters {

    /**
     * Unifac parameters file path in resources directory (/src/resources/)
     */
    private static String PARAMETERS_WORKBOOK_PATH = "/ParametrosUnifac.xls";
    private byte SHEETS_SIZE;
    private HSSFWorkbook book;

    //UNIFAC Interaction Parameters Matrix and variables
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
    public UnifacParameters() {
        try (InputStream parametersWorkbookIS = UnifacParameters.class.getResourceAsStream(PARAMETERS_WORKBOOK_PATH)) {
            System.out.println("Loading UNIFAC parameters file");
            POIFSFileSystem fs = new POIFSFileSystem(parametersWorkbookIS);
            book = new HSSFWorkbook(fs);
            SHEETS_SIZE = (byte) book.getNumberOfSheets();
            loadInteractions();
            loadGroupsData();
            loadSecondOrderParameters();
            loadProbabilities();
            parametersWorkbookIS.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //        this.sheets = new HSSFSheet[SHEETS_SIZE];
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void procesar() {
        for (byte i = 0; i < SHEETS_SIZE; i++) {
            HSSFSheet sheet = book.getSheetAt(i);
            System.out.println("Hoja: " + book.getSheetName(i));
            Iterator rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                HSSFRow fila = (HSSFRow) rowIterator.next();
                Iterator cellIterator = fila.cellIterator();
                while (cellIterator.hasNext()) {
                    HSSFCell cell = (HSSFCell) cellIterator.next();
                    System.out.print("\t" + cell.toString());
                    // Imprime el contenido de la celda (valores o formulas)
                }
                System.out.println();
            }
        }
    }

    /**
     * Read of sheets:
     * Parámetros interacción aij
     * Parámetros bij
     * Parámetros interacción cij
     */
    private void loadInteractions() {
        int row;
        int col;
        for (byte i = 0; i < 3; i++) {
            HSSFSheet sheet = book.getSheetAt(i);
            System.out.println("Hoja: " + book.getSheetName(i));
            Iterator rowIterator = sheet.rowIterator();
            row = 0;
            while (rowIterator.hasNext()) {
                HSSFRow fila = (HSSFRow) rowIterator.next();
                Iterator cellIterator = fila.cellIterator();
                col = 0;
                while (cellIterator.hasNext()) {
                    HSSFCell cell = (HSSFCell) cellIterator.next();
                    ijParams[i][row][col] = cell.toString();
                    // Imprime el contenido de la celda (valores o formulas)
                    col = col + 1;
                }
                row = row + 1;
            }
        }
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
            HSSFSheet sheet = book.getSheetAt(i + 3);
            System.out.println("FIRST ORDER GROUPS Hoja: " + book.getSheetName(i + 3));
            Iterator rowIterator = sheet.rowIterator();
            row = 0;
            while (rowIterator.hasNext()) {
                HSSFRow fila = (HSSFRow) rowIterator.next();
                Iterator cellIterator = fila.cellIterator();
                col = 0;
                while (cellIterator.hasNext()) {
                    HSSFCell cell = (HSSFCell) cellIterator.next();
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
            HSSFSheet sheet = book.getSheetAt(i + 11);
            System.out.println("SECOND ORDER PARAMETERS Hoja: " + book.getSheetName(i + 11));
            Iterator iteratorSheets = sheet.rowIterator();
            row = 0;
            while (iteratorSheets.hasNext()) {
                HSSFRow fila = (HSSFRow) iteratorSheets.next();
                Iterator iteratorCeldas = fila.cellIterator();
                col = 0;
                while (iteratorCeldas.hasNext()) {
                    HSSFCell celda = (HSSFCell) iteratorCeldas.next();
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
        HSSFSheet sheet = book.getSheetAt(13);
        System.out.println("Hoja: " + book.getSheetName(13));
        Iterator iteratorSheets = sheet.rowIterator();
        row = 0;
        while (iteratorSheets.hasNext()) {
            HSSFRow fila = (HSSFRow) iteratorSheets.next();
            Iterator iteratorCeldas = fila.cellIterator();
            col = 0;
            while (iteratorCeldas.hasNext()) {
                HSSFCell celda = (HSSFCell) iteratorCeldas.next();
                mainGroupProbabilities[row][col] = celda.toString();
                //System.out.print("\t" + celda.readableString());
                // Imprime el contenido de la celda (valores o formulas)
                col = col + 1;
            }
            row = row + 1;
        }
    }
}

