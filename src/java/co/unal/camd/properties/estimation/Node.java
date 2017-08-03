package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionParametersManager;

import java.util.Vector;

public class Node {

    private int rootNode; //identification of each group by refCode
    private Vector<Node> subGroups;
    //private GenotypeChemistry aGC;

    public Node(Node aNode) {
        rootNode = aNode.getRootNode();
        subGroups = new Vector<Node>();
        int n = aNode.getGroupsCount();
        for (int i = 0; i < n; i++) {
            subGroups.add(aNode.subGroups.get(i).clone());
        }
    }

    public Node clone() {
        return new Node(this);
    }

    public Node(int refCode) {
        rootNode = refCode;
        subGroups = new Vector<Node>();
    }

    public Node(String name, ContributionParametersManager aGenotypeChemistry) {
        rootNode = aGenotypeChemistry.getRefCode(name);
        subGroups = new Vector<Node>();
    }

    /**
     * add a group to this group and count the valence to waranty the 0 valence and octete law in the molecule
     *
     * @param G
     */
    public void addGroup(Node subG) {
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

    public int getIndexOfGroup(Node aG) {
        return subGroups.indexOf(aG);
    }

    public int getRefCodeGroupAt(int i) {
        if (subGroups.size() < i + 1) {
            return 0;
        } else {
            return subGroups.elementAt(i).getRootNode();
        }
    }

    public Node getGroupAt(int i) {
        if (subGroups.size() < i + 1) {
            return null;
        } else {
            return subGroups.elementAt(i);
        }
    }

    public void setGroupAt(int i, Node aGr) {
        subGroups.set(i, aGr);
    }

    public void clearGroup(int i) {
        subGroups.removeElementAt(i);
    }

    public int getRootNode() {
        return rootNode;
    }

    public void setRootNode(int num) {
        rootNode = num;
    }

    public Vector<Node> getSubGroups() {
        return subGroups;
    }

    public String toString() {
        return Integer.toString(rootNode);
        //return aGC.getName(rootNode);
    }

}
