package co.unal.camd.properties.model;

import co.unal.camd.view.CamdRunner;
import lombok.Data;

import java.util.Arrays;

@Data
public class MoleculeGroups {

    double composition;
    int[] amount;
    int[] groups;

    public MoleculeGroups(int[] _groups) {
        groups = _groups;
    }

    public MoleculeGroups(int[] _groups, double _composition) {
        groups = _groups;
        composition = _composition;
    }

    public void optimize() {
        int N = 120;
        int[] pamount = new int[N];
        for (int i = 0; i < groups.length; i++)
            pamount[groups[i]]++;

        int c = 0;
        for (int i = 0; i < N; i++)
            if (pamount[i] > 0)
                c++;

        int[] new_groups = new int[c];
        amount = new int[c];
        c = 0;
        for (int i = 0; i < N; i++)
            if (pamount[i] > 0) {
                new_groups[c] = i;
                amount[c] = pamount[i];
                c++;
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
    public double getq() {
        double qi = 0;
        for (int i = 0; i < groups.length; i++)
            qi = qi + CamdRunner.CONTRIBUTION_GROUPS.getQ(groups[i]);

        //System.out.println("qi"+qi);
        return qi;
    }

    ////////r///////
    public double getr() {
        double ri = 0;
        for (int i = 0; i < groups.length; i++)
            ri = ri + CamdRunner.CONTRIBUTION_GROUPS.getR(groups[i]);
        //	System.out.println("ri"+ri);
        return ri;
    }

    public String readableString() {
        //optimize();
        String show = "";
        for (int i = 0; i < groups.length; i++) {
            int n = groups[i];
            if (n > 0)
                show += CamdRunner.CONTRIBUTION_GROUPS.findGroupName(n) + "-";
        }
        return show;
    }

    @Override
    public MoleculeGroups clone() {
        int[] clonedGroups = Arrays.copyOf(groups, groups.length);
        return new MoleculeGroups(clonedGroups, composition);
    }
}
