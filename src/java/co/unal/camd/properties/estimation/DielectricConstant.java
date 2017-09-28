package co.unal.camd.properties.estimation;

import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class DielectricConstant {

    private double sum = 0;
    private MoleculeGroups aMolecule;

    private double temperature;
    private double vapHeat;
    private double molarVolume;
    private double refracIndex;
    private double dipolarMoment;

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


    public DielectricConstant(Molecule solvent, ArrayList<Integer> secOrder, double Temperature) {
        temperature = Temperature;
        secondOrderCode = secOrder;
        aMolecule = solvent.getGroupArray();
        aMolecule.optimize();

        isConditionG1 = isConditionCase(conditionG1);
        isConditionGHC = isConditionCase(conditionGHC);
        isCondition1_9 = isConditionCase(condition1_9);
        isConditionG3 = isConditionCase(conditionG3);
        isConditionGND = isConditionCase(conditionGND);
        isConditionG2 = isConditionCase(conditionG2);

    }

    public double getVapHeat() {
        double c = 6.829;
        for (int i = 0; i < aMolecule.size(); i++) {
            int n = aMolecule.amount[i];
            double hi = CamdRunner.CONTRIBUTION_GROUPS.getHi(aMolecule.getGroupCode(i));
            c = c + n * hi;
        }
        //System.out.println("c:" +c);
        return c;
    }

    public double getMolarVolume() {
        double d = 0.01211;
        double c = 0;
        for (int i = 0; i < aMolecule.size(); i++) {
            int n = aMolecule.amount[i];
            //	System.out.println("c"+c);

            double mvi = CamdRunner.CONTRIBUTION_GROUPS.getMV(aMolecule.getGroupCode(i));
            //System.out.println("c"+mvi);
            //System.out.println("n"+n);
            c = c + n * mvi;
            //	System.out.println("ci:" +c);
        }
        //System.out.println("MVC1:" +c);
        for (int j = 0; j < secondOrderCode.size(); j++) {
            int caseNum = secondOrderCode.get(j);
            c += CamdRunner.CONTRIBUTION_GROUPS.getMVolumeSecondOrderParameter(caseNum);
        }
        //System.out.println("MVC2:" +c);
        return (c + d) * 1000;
    }

    public double getd() {
        double R = 8.314;
        //System.out.println("h:" +getVapHeat());
        double d = Math.pow((getVapHeat() - R * temperature / 1000) / getMolarVolume(), 0.5);
        //System.out.println("d:" + d);
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
        for (int k = 0; k < aMolecule.size(); k++) {
            for (int j = 0; j < conditionGHC.length; j++) {
                if (aMolecule.getGroupCode(k) == conditionGHC[j]) {
                    sum += 1;
                }
            }
        }
        //System.outln("N_GHC="+sum);

        if (conditionCase & sum == aMolecule.size()) {
            return 0;
        } else {
            double c = 0;
            for (int i = 0; i < aMolecule.size(); i++) {
                int n = aMolecule.amount[i];
                double dmi = CamdRunner.CONTRIBUTION_GROUPS.getDM(aMolecule.getGroupCode(i));
                //	System.out.println("DMi"+dmi);
                c = c + n * dmi;
            }
            return 0.11 * Math.pow(c, 0.29) * Math.pow(getMolarVolume(), -0.16);
        }
    }

    private double getE1(boolean conditionCase, boolean otherCond) {
        int sum = 0;

        if (conditionCase && otherCond) {
            for (int i = 0; i < aMolecule.size(); i++) {
                for (int j = 0; j < condition1_9.length; j++) {
                    if (aMolecule.getGroupCode(i) == condition1_9[j]) {
                        sum += 1;
                    }
                }
            }
            return 70 * 1 / (sum + 4.5);
        } else return 0;
    }

    private double getE2(boolean conditionCase, boolean otherCond) {
        double sum = 0;
        if (conditionCase && otherCond) {
            for (int i = 0; i < aMolecule.size(); i++) {
                for (int j = 0; j < condition1_9.length; j++) {
                    if (aMolecule.getGroupCode(i) == condition1_9[j]) {
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
        int l = aMolecule.groups.length;
        for (int i = 0; i < l; i++) {
            int g = aMolecule.groups[i];
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
        for (int i = 0; i < aMolecule.size(); i++) {

        }
        return 204.359;
    }


}
