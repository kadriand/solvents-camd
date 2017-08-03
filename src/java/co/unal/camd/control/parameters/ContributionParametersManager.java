package co.unal.camd.control.parameters;

import java.util.ArrayList;


/**
 * Finds the parameters used for the computing of chemical properties
 */
public class ContributionParametersManager {

    private UnifacParameters unifacParameters;
    private String[][][] allGroups;
    private String[][][] Paramij;
    private String[][][] secondOrderGroups;
    private String[][] probabilities;
    private int codeOfRow;
    private int valence;

    /**
     * Manager of the parameters used by the unifac based methods
     */
    public ContributionParametersManager() {
        allGroups = new String[8][50][50];
        unifacParameters = new UnifacParameters();
        unifacParameters.loadUnifac();
        unifacParameters.loadInfoGroups();
        unifacParameters.loadSecondOrderParameters();
        unifacParameters.loadProbabilities();
        Paramij = unifacParameters.getParamij();
        allGroups = unifacParameters.getAllGroups();
        secondOrderGroups = unifacParameters.getSecondOrderParameters();
        probabilities = unifacParameters.getProbabilities();
    }

    /**
     * if(functional==true){
     * valence=aValence;
     * <p>
     * int numGroupOfValence=(int)(Double.parseDouble(op.getAllGroups()[valence-1][0][0]));
     * codeOfRow=(int)(Math.random()*numGroupOfValence)+1;//random row to choose the group
     * <p>
     * }
     * }
     */

    public String[][][] getParamij() {
        return Paramij;
    }

    public String[][][] getSecondOrderParam() {
        return secondOrderGroups;
    }

    private void getValenceAndCodeofRowByName(String aName) {
        for (int j = 0; j <= 6; j++) {
            for (int i = 1; i <= getTotalNumberOfGroupOfValence(j + 1); i++) {
                if (aName == allGroups[j][i][2]) {
                    codeOfRow = i;
                    valence = j + 1;////////////revisar
                }
            }
        }
    }

    private void getValenceAndCodeofRowByRefCode(int refCode) {
        int a = (int) (Double.parseDouble(allGroups[1][1][3]));
        for (int j = 0; j <= 6; j++) {
            for (int i = 1; i <= getTotalNumberOfGroupOfValence(j + 1); i++) {
                a = (int) (Double.parseDouble(allGroups[j][i][3]));
                if (refCode == a) {
                    valence = j + 1;
                    codeOfRow = i;
                }
            }

        }
    }

    public void getCodeOfRowBNameOrRefCode(Object toSearch) {
        if (toSearch instanceof String) {
            String name = (String) toSearch;
            getValenceAndCodeofRowByName(name);
        } else if (toSearch instanceof Integer) {
            int refCode = (Integer) toSearch;
            getValenceAndCodeofRowByRefCode(refCode);
        }
    }

    public void setProbability(int code, double proba) {
        String probability = Double.toString(proba);
        int[] a = {1};
        int[] b = {2, 37};
        int[] c = {3, 4, 8, 17, 18, 25, 27, 52};
        int[] d = {5, 6, 31};
        int[] e = {9};
        int[] f = {10};
        int[] g = {11, 12, 41};
        int[] h = {13};
        int[] i = {14, 15, 16};
        int[] j = {19};
        int[] k = {20, 44};
        int[] l = {37, 21, 22, 23, 24, 45, 32, 33, 40};
        int[] m = {28, 29, 52};
        int[] n = {34};
        int[] o = {47, 48, 49};
        int[] p = {42};
        int[] q = {43, 46};
        int[] r = {53};
        int[] s = {26};
        int[] t = {7};
        int[] u = {30};
        int[] v = {35};
        int[] w = {36};

        int[][] principalCodes = {a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w};
        int dim = principalCodes[code].length;
        for (int z = 0; z < dim; z++) {
            int principalGroupCode = principalCodes[code][z];
            probabilities[principalGroupCode][1] = probability;
        }
    }

    public double getProbability(int principalGroupCode) {
        return Double.parseDouble(probabilities[principalGroupCode][1]);
    }

    public String getGlobalGroupName(int principalGroupCode) {
        return (probabilities[principalGroupCode][0]);
    }

    public String getPrincipalGroupNames(int code) {
        String show = "";

        int[] a = {1};
        int[] b = {2, 37};
        int[] c = {3, 4, 8, 17, 18, 25, 27, 52};
        int[] d = {5, 6, 31};
        int[] e = {9};
        int[] f = {10};
        int[] g = {11, 12, 41};
        int[] h = {13};
        int[] i = {14, 15, 16};
        int[] j = {19};
        int[] k = {20, 44};
        int[] l = {37, 21, 22, 23, 24, 45, 32, 33, 40};
        int[] m = {28, 29, 52};
        int[] n = {34};
        int[] o = {47, 48, 49};
        int[] p = {42};
        int[] q = {43, 46};
        int[] r = {53};
        int[] s = {26};
        int[] t = {7};
        int[] u = {30};
        int[] v = {35};
        int[] w = {36};

        int[][] principalCodes = {a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w};
        int dim = principalCodes[code - 1].length;
        for (int z = 0; z < dim; z++) {
            int principalGroupCode = principalCodes[code - 1][z];
            show = show + probabilities[principalGroupCode][2] + ",";
        }
        return show;
    }

    public double getProbability(int valence, int codeOfRow) {
        int p = (int) (Double.parseDouble(allGroups[valence - 1][codeOfRow][1]));
        return getProbability(p);
    }

    public int getPrincipalGroupCode(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        int a = (int) (Double.parseDouble(allGroups[valence - 1][codeOfRow][1]));
        return a;
    }

    public int getRefCode(String aName) {
        getValenceAndCodeofRowByName(aName);
        return (int) (Double.parseDouble(allGroups[valence - 1][codeOfRow][3]));
    }

    public int getRefCode(int valence, int codeOfRow) {
        return (int) (Double.parseDouble(allGroups[valence - 1][codeOfRow][3]));
    }

    public String getName(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return allGroups[valence - 1][codeOfRow][2];
    }

    public int getValence(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        //System.out.println("valence"+valence);
        return valence;
    }

    public void setPseudoValenceIncreaseIn1() {
        valence = valence + 1;
    }

    public double getR(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][4]);
    }

    public double getQ(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][5]);
    }

    public double getPM(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][6]);
    }

    public double getConstantPTeb(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][7]);
    }

    public double getG(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][8]);
    }

    public double[][] getDensityConstants(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        double[][] ABC = new double[4][4];
        for (int i = 0; i <= 3; i++) {
            ABC[i][0] = Double.parseDouble(allGroups[valence - 1][codeOfRow][9 + i * 3]);
            ABC[i][1] = Double.parseDouble(allGroups[valence - 1][codeOfRow][10 + i * 3]);
            ABC[i][2] = Double.parseDouble(allGroups[valence - 1][codeOfRow][11 + i * 3]);
        }
        return ABC;
    }

    public double getMeltTemp(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][21]);
    }

    public double getDM(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][22]);
    }

    public double getMV(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][23]);
    }

    public double getHi(Object toSearch) {
        getCodeOfRowBNameOrRefCode(toSearch);
        return Double.parseDouble(allGroups[valence - 1][codeOfRow][24]);
    }

    public int getTotalNumberOfGroupOfValence(int valence) {

        return (int) (Double.parseDouble(allGroups[valence - 1][0][0]));
    }

    public int getCodeOfRow() {
        return codeOfRow;
    }

    public String[][][] getAllGroups() {
        return allGroups;
    }

    public double getTemperatureSecondOrderParameter(int caseNum) {
        double paramT = 0.0;
        paramT = Double.parseDouble(secondOrderGroups[1][caseNum][0]);
        return paramT;
    }

    public double getGibbsESecondOrderParameter(int caseNum) {
        double paramGE = 0.0;
        paramGE = Double.parseDouble(secondOrderGroups[1][caseNum][1]);
        return paramGE;
    }

    public double getMVolumeSecondOrderParameter(int caseNum) {
        double paramMvolume = 0.0;
        paramMvolume = Double.parseDouble(secondOrderGroups[1][caseNum][3]);
        return paramMvolume;
    }

    public double getTfusSecondOrderParameter(int caseNum) {
        double paramTfus = 0.0;
        paramTfus = Double.parseDouble(secondOrderGroups[1][caseNum][2]);
        return paramTfus;
    }

    public ArrayList<String[]> getSecondOrderGroupCase(double root) {
        ArrayList<String[]> caseNum = new ArrayList<String[]>();
        double n = 0;
        int i = 0;
        while (n <= root) {
            if (n == root) {
                caseNum.add(secondOrderGroups[0][i]);
                //for(int j = 0 ; j<secondOrderGroups[0][i].length;j++){
                //System.out.println("\t"+secondOrderGroups[0][i][j]);
                //}
                //System.out.println("\n");
            }
            i++;
            n = Double.parseDouble(secondOrderGroups[0][i][1]);
        }
        return caseNum;
    }
    /**
     public static void main(String[] args) {
     // TODO Auto-generated method stub
     GenotypeChemistry Gc= new GenotypeChemistry();
     Gc.getSecondOrderGroupCase(4);
     }
     */
}
