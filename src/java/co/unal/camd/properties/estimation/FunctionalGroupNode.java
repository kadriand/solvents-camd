package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionGroupsManager;
import lombok.Data;

import java.util.Vector;

@Data
public class FunctionalGroupNode {

    private int rootNode; //identification of each group by refCode
    private Vector<FunctionalGroupNode> subGroups;
    //    private ContributionParametersManager contributionGroups;

    public FunctionalGroupNode(FunctionalGroupNode functionalGroupNode) {
        rootNode = functionalGroupNode.getRootNode();
        subGroups = new Vector<>();
        int n = functionalGroupNode.getGroupsCount();
        for (int i = 0; i < n; i++) {
            subGroups.add(functionalGroupNode.subGroups.get(i).clone());
        }
    }

    public FunctionalGroupNode clone() {
        return new FunctionalGroupNode(this);
    }

    public FunctionalGroupNode(int refCode) {
        rootNode = refCode;
        subGroups = new Vector<>();
    }

    public FunctionalGroupNode(String name, ContributionGroupsManager parametersManager) {
        rootNode = parametersManager.getRefCode(name);
        subGroups = new Vector<>();
    }

    /**
     * add a group to this group and count the valence to waranty the 0 valence and octete law in the molecule
     *
     */
    public void addGroup(FunctionalGroupNode subG) {
        subGroups.addElement(subG);
    }

    public void removeGroup(int i) {
        subGroups.remove(i);
    }

    public int getTotalGroupsCount() {
        int s = 1;

        for (int i = 0; i < getGroupsCount(); i++) {
            s += getGroupAt(i).getTotalGroupsCount();
        }
        return s;
    }

    public int getGroupsCount() {
        return subGroups.size();
    }

    public int getIndexOfGroup(FunctionalGroupNode aG) {
        return subGroups.indexOf(aG);
    }

    public int getRefCodeGroupAt(int i) {
        if (subGroups.size() < i + 1) {
            return 0;
        } else {
            return subGroups.elementAt(i).getRootNode();
        }
    }

    public FunctionalGroupNode getGroupAt(int i) {
        if (subGroups.size() < i + 1) {
            return null;
        } else {
            return subGroups.elementAt(i);
        }
    }

    public void setGroupAt(int i, FunctionalGroupNode aGr) {
        subGroups.set(i, aGr);
    }

    public void clearGroup(int i) {
        subGroups.removeElementAt(i);
    }

    public Vector<FunctionalGroupNode> getSubGroups() {
        return subGroups;
    }

    public String toString() {
        return Integer.toString(rootNode);
        //        return contributionGroups.getName(rootNode);
    }

}
