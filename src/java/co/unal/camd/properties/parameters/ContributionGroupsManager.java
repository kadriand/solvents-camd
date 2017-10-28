package co.unal.camd.properties.parameters;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;


/**
 * Finds the parameters used for the computing of chemical properties
 */
public final class ContributionGroupsManager {

    @Getter
    private String[][][] groupsData;
    @Getter
    private String[][][] ijParameters;

    private String[][][] secondOrderGroups;

    private String[][] probabilities;
    @Getter
    private int codeOfRow;
    private int valence;

    /**
     * Manager of the parameters used by the unifac based methods
     */
    public ContributionGroupsManager() {
        groupsData = new String[8][50][50];
        UnifacParameters unifacParameters = new UnifacParameters();
        ijParameters = unifacParameters.getIjParams();
        groupsData = unifacParameters.getGroupsData();
        secondOrderGroups = unifacParameters.getSecondOrderParameters();
        probabilities = unifacParameters.getMainGroupProbabilities();
    }

    private void getValenceAndCodeOfRowByName(String aName) {
        for (int j = 0; j <= 6; j++)
            for (int i = 1; i <= getTotalNumberOfGroupOfValence(j + 1); i++) {
                if (!Objects.equals(aName, groupsData[j][i][2]))
                    continue;
                codeOfRow = i;
                valence = j + 1;
                //                System.out.println("ocurrency for " + aName);
                return;
            }
    }

    private void getValenceAndCodeOfRowByRefCode(int refCode) {
        int a;
        for (int j = 0; j <= 6; j++)
            for (int i = 1; i <= getTotalNumberOfGroupOfValence(j + 1); i++) {
                a = (int) (Double.parseDouble(groupsData[j][i][3]));
                if (refCode != a)
                    continue;
                valence = j + 1;
                codeOfRow = i;
                //                System.out.println("ocurrency for " + a);
                return;
            }
    }

    public final void resolveValence(Object toSearch) {
        if (toSearch instanceof String) {
            String name = (String) toSearch;
            getValenceAndCodeOfRowByName(name);
        } else if (toSearch instanceof Integer) {
            int refCode = (Integer) toSearch;
            getValenceAndCodeOfRowByRefCode(refCode);
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
        int p = (int) (Double.parseDouble(groupsData[valence - 1][codeOfRow][1]));
        return getProbability(p);
    }

    public int getPrincipalGroupCode(Object toSearch) {
        resolveValence(toSearch);
        int a = (int) (Double.parseDouble(groupsData[valence - 1][codeOfRow][1]));
        return a;
    }

    public int findGroupCode(String aName) {
        getValenceAndCodeOfRowByName(aName);
        return (int) (Double.parseDouble(groupsData[valence - 1][codeOfRow][3]));
    }

    public int findGroupCode(int valence, int codeOfRow) {
        return (int) (Double.parseDouble(groupsData[valence - 1][codeOfRow][3]));
    }

    public String findGroupName(Object toSearch) {
        resolveValence(toSearch);
        return groupsData[valence - 1][codeOfRow][2];
    }

    public int findGroupValence(Object toSearch) {
        resolveValence(toSearch);
        //System.out.println("valence"+valence);
        return valence;
    }

    public double getR(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][4]);
    }

    public double getQ(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][5]);
    }

    public double getPM(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][6]);
    }

    public double getConstantPTeb(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][7]);
    }

    public double getG(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][8]);
    }

    public double[][] getDensityConstants(Object toSearch) {
        resolveValence(toSearch);
        double[][] ABC = new double[4][4];
        for (int i = 0; i <= 3; i++) {
            ABC[i][0] = Double.parseDouble(groupsData[valence - 1][codeOfRow][9 + i * 3]);
            ABC[i][1] = Double.parseDouble(groupsData[valence - 1][codeOfRow][10 + i * 3]);
            ABC[i][2] = Double.parseDouble(groupsData[valence - 1][codeOfRow][11 + i * 3]);
        }
        return ABC;
    }

    public double getMeltTemp(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][21]);
    }

    public double getDM(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][22]);
    }

    public double getMV(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][23]);
    }

    public double getHi(Object toSearch) {
        resolveValence(toSearch);
        return Double.parseDouble(groupsData[valence - 1][codeOfRow][24]);
    }

    public int getTotalNumberOfGroupOfValence(int valence) {
        return (int) (Double.parseDouble(groupsData[valence - 1][0][0]));
    }

    public double getBoilingTempSecondOrderParameter(int caseNum) {
        return Double.parseDouble(secondOrderGroups[1][caseNum][0]);
    }

    public double getGibbsESecondOrderParameter(int caseNum) {
        return Double.parseDouble(secondOrderGroups[1][caseNum][1]);
    }

    public double getFusionTempSecondOrderParameter(int caseNum) {
        return Double.parseDouble(secondOrderGroups[1][caseNum][2]);
    }

    public double getMVolumeSecondOrderParameter(int caseNum) {
        return Double.parseDouble(secondOrderGroups[1][caseNum][3]);
    }

    public ArrayList<String[]> getSecondOrderGroupCase(double root) {
        ArrayList<String[]> caseNum = new ArrayList<>();
        double n = 0;
        int i = 0;
        while (n <= root) {
            if (n == root)
                caseNum.add(secondOrderGroups[0][i]);
            i++;
            n = Double.parseDouble(secondOrderGroups[0][i][1]);
        }
        return caseNum;
    }
}
