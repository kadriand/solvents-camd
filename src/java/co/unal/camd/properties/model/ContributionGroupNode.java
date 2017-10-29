package co.unal.camd.properties.model;

import co.unal.camd.view.CamdRunner;
import lombok.Data;

import java.util.Vector;

@Data
public class ContributionGroupNode {

    private int groupId; //identification of each group by refCode
    private Vector<ContributionGroupNode> subGroups;

    public ContributionGroupNode(ContributionGroupNode functionalGroupNode) {
        groupId = functionalGroupNode.getGroupId();
        subGroups = new Vector<>();
        int n = functionalGroupNode.countSubgroups();
        for (int i = 0; i < n; i++) {
            subGroups.add(functionalGroupNode.subGroups.get(i).clone());
        }
    }

    public ContributionGroupNode clone() {
        return new ContributionGroupNode(this);
    }

    public ContributionGroupNode(int refCode) {
        groupId = refCode;
        subGroups = new Vector<>();
    }

    public ContributionGroupNode(String name) {
        groupId = CamdRunner.CONTRIBUTION_GROUPS.findGroupCode(name);
        subGroups = new Vector<>();
    }

    /**
     * add a group to this group and count the valence to waranty the 0 valence and octete law in the molecule
     */
    public void addGroup(ContributionGroupNode subG) {
        subGroups.addElement(subG);
    }

    public void removeGroup(int i) {
        subGroups.remove(i);
    }

    public int countTotalGroups() {
        int s = 1;
        for (int i = 0; i < subGroups.size(); i++)
            s += getGroupAt(i).countTotalGroups();
        return s;
    }

    public int countSubgroups() {
        return subGroups.size();
    }

    public int getIndexOfGroup(ContributionGroupNode aG) {
        return subGroups.indexOf(aG);
    }

    public int getRefCodeGroupAt(int i) {
        if (subGroups.size() < i + 1)
            return 0;
        else
            return subGroups.elementAt(i).getGroupId();
    }

    public ContributionGroupNode getGroupAt(int i) {
        if (subGroups.size() < i + 1)
            return null;
        else
            return subGroups.elementAt(i);
    }

    public void setGroupAt(int i, ContributionGroupNode aGr) {
        subGroups.set(i, aGr);
    }

    public void clearGroup(int i) {
        subGroups.removeElementAt(i);
    }

    public Vector<ContributionGroupNode> getSubGroups() {
        return subGroups;
    }

    public String toString() {
        return Integer.toString(groupId);
        //        return CONTRIBUTION_GROUPS.findGroupName(groupId);
    }

}
