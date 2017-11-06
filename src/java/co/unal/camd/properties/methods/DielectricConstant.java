package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class DielectricConstant {

    private double sum = 0;
    private MoleculeGroups molecule;

    private double temperature;
    private double vapHeat;
    private double molarVolume;
    private double refracIndex;
    private double dipolarMoment;

    /*TODO move to parameters worksheet*/
    ArrayList<Integer> secondOrderCode;
    private int[] conditionG1 = {81, 82, 14, 18, 19, 20, 41, 55, 56};
    private int[] conditionGHC = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 70};
    private int[] condition1_9 = {1, 2, 3, 4, 5, 6, 7, 8, 70};
    private int[] conditionG3 = {44, 45, 48};
    private int[] conditionGND = {24, 25, 26, 44, 45, 48, 53, 63, 64, 71};
    private int[] conditionG2 = {42};

    private boolean isConditionG1;
    private boolean isConditionGHC;
    private boolean isCondition1_9;
    private boolean isConditionG3;
    private boolean isConditionGND;
    private boolean isConditionG2;


    public DielectricConstant(Molecule molecule, ArrayList<Integer> secOrder, double temperature) {
        this.temperature = temperature;
        this.secondOrderCode = secOrder;
        this.molecule = molecule.getGroupsArray();
        this.molecule.optimize();

        isConditionG1 = isConditionCase(conditionG1);
        isConditionGHC = isConditionCase(conditionGHC);
        isCondition1_9 = isConditionCase(condition1_9);
        isConditionG3 = isConditionCase(conditionG3);
        isConditionGND = isConditionCase(conditionGND);
        isConditionG2 = isConditionCase(conditionG2);
    }

    public double getVapHeat() {
        double c = 6.829;
        for (int i = 0; i < molecule.size(); i++) {
            int n = molecule.getAmount()[i];
            c += n * molecule.getGroupContributions()[i].getDipoleMomentH1i();
        }
        return c;
    }

    public double getMolarVolume() {
        double d = 0.01211;
        double c = 0;

        for (int i = 0; i < molecule.size(); i++) {
            int n = molecule.getAmount()[i];
            c += n * molecule.getGroupContributions()[i].getLiquidMolarVolume();
        }

        for (int j = 0; j < secondOrderCode.size(); j++) {
            int caseNum = secondOrderCode.get(j);
            c += CamdRunner.CONTRIBUTION_GROUPS.getSecondOrderGroupsContributions().get(caseNum).getLiquidMolarVolume();
        }
        //System.out.println("MVC2:" +c);
        return (c + d) * 1000;
    }

    public double getd() {
        double R = 8.314;
        double d = Math.pow((getVapHeat() - R * temperature / 1000) / getMolarVolume(), 0.5);
        return d;
    }

    public double getRefracIndex(boolean conditionCase) {
        double d = getd();
        if (conditionCase) {
            return 1 / 7.26 * (Math.pow(d, 0.36) + 8.15);
        } else {
            return 1 / 14.95 * (d + 13.47);
        }
    }

    public double getDipolarMoment(boolean conditionCase) {
        int sum = 0;
        for (int k = 0; k < molecule.size(); k++)
            for (int j = 0; j < conditionGHC.length; j++)
                if (molecule.getGroupCode(k) == conditionGHC[j])
                    sum += 1;

        if (conditionCase & sum == molecule.size())
            return 0;

        double c = 0;
        for (int i = 0; i < molecule.size(); i++) {
            int n = molecule.getAmount()[i];
            c += n * molecule.getGroupContributions()[i].getDipoleMoment();
        }
        return 0.11 * Math.pow(c, 0.29) * Math.pow(getMolarVolume(), -0.16);
    }

    private double getE1(boolean conditionCase, boolean otherCond) {
        int sum = 0;

        if (conditionCase && otherCond) {
            for (int i = 0; i < molecule.size(); i++) {
                for (int j = 0; j < condition1_9.length; j++) {
                    if (molecule.getGroupCode(i) == condition1_9[j]) {
                        sum += 1;
                    }
                }
            }
            return 70 / (sum + 4.5);
        } else return 0;
    }

    private double getE2(boolean conditionCase, boolean otherCond) {
        double sum = 0;
        if (conditionCase && otherCond) {
            for (int i = 0; i < molecule.size(); i++) {
                for (int j = 0; j < condition1_9.length; j++) {
                    if (molecule.getGroupCode(i) == condition1_9[j]) {
                        sum += 1;
                    }
                }
            }
            return -16 * 1 / (sum + 3);
        } else return 0;
    }

    private double getE3(boolean conditionCase) {
        if (conditionCase) {
            return 2.5;
        } else {
            return 0;
        }
    }

    public double getDielectricConstant() {
        double DM = getDipolarMoment(isConditionGHC);
        //System.out.println("DM:"+DM);
        double VM = getMolarVolume();
        //System.out.println("VM:"+VM);
        if (DM <= 0.5) {
            double r = (0.1 + Math.pow(getRefracIndex(isConditionGND), 2));
            //System.out.println("r: "+r);
            return r;
        } else {
            double r = 0.91 * (48 * DM * DM - 15.5 * DM * DM * DM) * Math.pow(VM, -0.5)
                    + getE1(isConditionG1, isCondition1_9) + getE2(isConditionG2, isCondition1_9) + getE3(isConditionG3);
            //System.out.println("E1: "+getE1(isConditionG1,isCondition1_9));
            //System.out.println("E2: "+getE2(isConditionG2,isCondition1_9));
            //System.out.println("E3: "+getE3(isConditionG3));
            //System.out.println("r: "+r);
            return r;
        }
    }

    public boolean isConditionCase(int[] conditionCase) {
        boolean b = false;
        int l = molecule.getGroups().length;
        for (int i = 0; i < l; i++) {
            int g = molecule.getGroups()[i];
            for (int j = 0; j < conditionCase.length; j++) {
                int cc = conditionCase[j];
                if (g == cc) {
                    b = true;
                }
            }
        }
        return b;
    }

    public double getMethodResult() {
        for (int i = 0; i < molecule.size(); i++) {

        }
        return 204.359;
    }


}
