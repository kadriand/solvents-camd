package co.unal.camd.properties.estimation;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;


public class MoleculesModel implements TreeModel {

    private FunctionalGroupNode rootGroup;
    private Vector<TreeModelListener> treeModelListeners =
            new Vector<>();

    public MoleculesModel(FunctionalGroupNode aGroup) {
        rootGroup = aGroup;
    }

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {

    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index) {
        FunctionalGroupNode g = (FunctionalGroupNode) parent;
        return g.getGroupAt(index);
    }

    /**
     * Returns the number of child of parent.
     */
    public int getChildCount(Object parent) {
        FunctionalGroupNode g = (FunctionalGroupNode) parent;
        return g.countSubgroups();
    }

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        FunctionalGroupNode g = (FunctionalGroupNode) parent;
        return g.getIndexOfGroup((FunctionalGroupNode) child);
    }

    /**
     * Returns the root of the tree.
     */
    public Object getRoot() {
        return rootGroup;
    }

    /**
     * Returns true if node is a leaf.
     */
    public boolean isLeaf(Object node) {
        FunctionalGroupNode g = (FunctionalGroupNode) node;
        return g.countSubgroups() == 0;
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged : "
                + path + " --> " + newValue);
    }

}
