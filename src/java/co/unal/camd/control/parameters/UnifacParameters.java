package co.unal.camd.control.parameters;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

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
    public static String _PARAMETERS_PATH = "/ParametrosUnifac.xls";

    public static byte _SHEETS_SIZE;

    private HSSFWorkbook book;

    private HSSFSheet[] sheets;

    //UNIFAC Interaction Parameters Matrix and variables
    private String[][][] Paramij = new String[3][1000][1000];

    private String[][][] allGroups = new String[8][50][50];

    private String[][][] secondOrderParameters = new String[2][210][7];

    private String[][] principalGroupProbabilities = new String[100][3];
    private double GAMMA;

    private double temperature;

    /**
     * constructors for load the info
     */
    public UnifacParameters() {
        try {
            System.out.println("Loading UNIFAC parameters file");
            POIFSFileSystem fs = new POIFSFileSystem(UnifacParameters.class.getResourceAsStream(_PARAMETERS_PATH));
            book = new HSSFWorkbook(fs);
            _SHEETS_SIZE = (byte) book.getNumberOfSheets();
        } catch (Exception ex) {
        }
        this.sheets = new HSSFSheet[_SHEETS_SIZE];
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void procesar() {
        for (byte i = 0; i < _SHEETS_SIZE; i++) {
            sheets[i] = book.getSheetAt(i);
            System.out.println("Hoja: " + book.getSheetName(i));
            Iterator iteratorFilas = sheets[i].rowIterator();
            while (iteratorFilas.hasNext()) {
                HSSFRow fila = (HSSFRow) iteratorFilas.next();
                Iterator iteratorCeldas = fila.cellIterator();
                while (iteratorCeldas.hasNext()) {
                    HSSFCell celda = (HSSFCell) iteratorCeldas.next();
                    System.out.print("\t" + celda.toString());
                    // Imprime el contenido de la celda (valores o formulas)
                }
                System.out.println();
            }
        }
    }

    ///////////////////////////////////////////////load UNIFAC///////////////////////////////////////////////////
    public void loadUnifac() {
        int row = 0;
        int col = 0;
        for (byte i = 0; i < 3; i++) {
            sheets[i] = book.getSheetAt(i);
            //System.out.println("Hoja: " + book.getSheetName(i));
            Iterator iteratorFilas = sheets[i].rowIterator();
            row = 0;
            while (iteratorFilas.hasNext()) {
                HSSFRow fila = (HSSFRow) iteratorFilas.next();
                Iterator iteratorCeldas = fila.cellIterator();
                col = 0;
                while (iteratorCeldas.hasNext()) {
                    HSSFCell celda = (HSSFCell) iteratorCeldas.next();
                    Paramij[i][row][col] = celda.toString();
                    //System.out.print("\t" + celda.toString());
                    // Imprime el contenido de la celda (valores o formulas)
                    col = col + 1;
                }
                //System.out.println();
                row = row + 1;
            }
        }
    }

    // get the paramaeter aij, bij and cij matrix
    public String[][][] getParamij() {
        return Paramij;
    }

    public String[][][] getAllGroups() {
        return allGroups;
    }

    public String[][][] getSecondOrderParameters() {
        return secondOrderParameters;
    }

    public String[][] getProbabilities() {
        return principalGroupProbabilities;
    }

    public HSSFWorkbook getBook() {
        return book;
    }

    ///////////////////////////////////////////////load info//////////////////////////////////////////////////////////////////////////

    /**
     * load the information of all groups, call the excel document, sice the sheet 3 at 9
     * for the tree valences 1-4 ar cy and 0
     */
    public void loadInfoGroups() {

        int row = 0;
        int col = 0;

        for (byte i = 0; i < 7; i++) {
            sheets[i + 3] = book.getSheetAt(i + 3);
            System.out.println("Hoja: " + book.getSheetName(i + 3));
            Iterator iteratorFilas = sheets[i + 3].rowIterator();
            row = 0;
            while (iteratorFilas.hasNext()) {
                HSSFRow fila = (HSSFRow) iteratorFilas.next();
                Iterator iteratorCeldas = fila.cellIterator();
                col = 0;
                while (iteratorCeldas.hasNext()) {
                    HSSFCell celda = (HSSFCell) iteratorCeldas.next();
                    allGroups[i][row][col] = celda.toString();
                    // Imprime el contenido de la celda (valores o formulas)
                    col = col + 1;
                }

                row = row + 1;
            }
        }
    }

    public void loadSecondOrderParameters() {

        int row = 0;
        int col = 0;

        for (byte i = 0; i < 2; i++) {
            sheets[i + 11] = book.getSheetAt(i + 11);
            System.out.println("Hoja: " + book.getSheetName(i + 11));
            Iterator iteratorFilas = sheets[i + 11].rowIterator();
            row = 0;
            while (iteratorFilas.hasNext()) {
                HSSFRow fila = (HSSFRow) iteratorFilas.next();
                Iterator iteratorCeldas = fila.cellIterator();
                col = 0;
                while (iteratorCeldas.hasNext()) {
                    HSSFCell celda = (HSSFCell) iteratorCeldas.next();
                    secondOrderParameters[i][row][col] = celda.toString();
                    //System.out.print("\t" + celda.toString());
                    // Imprime el contenido de la celda (valores o formulas)
                    col = col + 1;
                }
                //System.out.println();
                row = row + 1;
            }
        }
    }

    public void loadProbabilities() {

        int row = 0;
        int col = 0;
        sheets[13] = book.getSheetAt(13);
        System.out.println("Hoja: " + book.getSheetName(13));
        Iterator iteratorFilas = sheets[13].rowIterator();
        row = 0;
        while (iteratorFilas.hasNext()) {
            HSSFRow fila = (HSSFRow) iteratorFilas.next();
            Iterator iteratorCeldas = fila.cellIterator();
            col = 0;
            while (iteratorCeldas.hasNext()) {
                HSSFCell celda = (HSSFCell) iteratorCeldas.next();
                principalGroupProbabilities[row][col] = celda.toString();
                //System.out.print("\t" + celda.toString());
                // Imprime el contenido de la celda (valores o formulas)
                col = col + 1;
            }
            //System.out.println();
            row = row + 1;
        }
    }
}

