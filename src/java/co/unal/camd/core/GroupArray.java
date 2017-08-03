package co.unal.camd.core;

import co.unal.camd.control.parameters.ContributionParametersManager;

public class GroupArray {
    int[] groups;
    int[] amount;
    double composition = 0.0;

    public GroupArray(int[] _groups) {
        groups = _groups;
        //optimize();
    }

    public GroupArray(int[] _groups, double _composition) {
        groups = _groups;
        composition = _composition;
        //optimize();
    }

    public void optimize() {
        int N = 120;
        int[] pamount = new int[N];
        for (int i = 0; i < groups.length; i++) {
            pamount[groups[i]]++;
        }
        int c = 0;
        for (int i = 0; i < N; i++) {
            if (pamount[i] > 0) {
                c++;
            }
        }
        int[] new_groups = new int[c];
        amount = new int[c];
        c = 0;
        for (int i = 0; i < N; i++) {
            if (pamount[i] > 0) {
                new_groups[c] = i;
                amount[c] = pamount[i];
                c++;
            }
        }
        groups = new_groups;
    }

    public int size() {
        return groups.length;
    }

    public int getGroupCode(int group) {
        return groups[group];
    }

    public int getAmount(int group) {
        return amount[group];
    }

    ////q///
    public double getq(ContributionParametersManager aGC) {
        double qi = 0;
        for (int i = 0; i < groups.length; i++) {
            qi = qi + aGC.getQ(groups[i]);
        }
        //System.out.println("qi"+qi);
        return qi;
    }

    ////////r///////
    public double getr(ContributionParametersManager aGC) {
        double ri = 0;
        for (int i = 0; i < groups.length; i++) {
            ri = ri + aGC.getR(groups[i]);
        }
//	System.out.println("ri"+ri);
        return ri;
    }

    public double getComposition() {
        return composition;
    }

    public void setComposition(double _D) {
        composition = _D;
    }

    public String toString(ContributionParametersManager aGC) {
        //optimize();
        String show = "";
        for (int i = 0; i < groups.length; i++) {
            int n = groups[i];
            if (n > 0) {
                show += aGC.getName(n) + "-";
            }
        }
        return show;
    }


}
