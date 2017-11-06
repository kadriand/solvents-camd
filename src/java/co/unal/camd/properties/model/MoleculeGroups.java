package co.unal.camd.properties.model;

import co.unal.camd.properties.parameters.unifac.ContributionGroupData;
import co.unal.camd.view.CamdRunner;
import lombok.Data;

import java.util.Arrays;

@Data
public class MoleculeGroups {

    private double composition;
    private int[] amount;
    private int[] groups;
    private ContributionGroupData[] groupContributions;

    public MoleculeGroups(int[] _groups) {
        groups = _groups;
        findGroupContributions();
    }

    public MoleculeGroups(int[] _groups, double _composition) {
        groups = _groups;
        composition = _composition;
        findGroupContributions();
    }

    private void findGroupContributions() {
        groupContributions = new ContributionGroupData[groups.length];
        for (int i = 0; i < groups.length; i++) {
            ContributionGroupData groupContribution = CamdRunner.CONTRIBUTION_GROUPS.getContributionGroups().get(groups[i]);
            groupContributions[i] = groupContribution;
        }
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
        findGroupContributions();
    }

    public int size() {
        return groups.length;
    }

    public int getGroupCode(int groupIndex) {
        return groups[groupIndex];
    }

    public int getAmount(int group) {
        return amount[group];
    }

    ////q///
    public double getQ() {
        double q = 0;
        for (int i = 0; i < groups.length; i++)
            q += groupContributions[i].getQParam();
        return q;
    }

    ////////r///////
    public double getR() {
        double r = 0;
        for (int i = 0; i < groups.length; i++)
            r += groupContributions[i].getRParam();
        return r;
    }

    public String readableString() {
        //optimize();
        String show = "";
        for (int i = 0; i < groups.length; i++) {
            int n = groups[i];
            if (n > 0)
                show += (show.length() > 0 ? "-" : "") + CamdRunner.CONTRIBUTION_GROUPS.findGroupName(n);
        }
        return show;
    }

    @Override
    public MoleculeGroups clone() {
        int[] clonedGroups = Arrays.copyOf(groups, groups.length);
        return new MoleculeGroups(clonedGroups, composition);
    }
}
