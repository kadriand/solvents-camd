package co.unal.camd.core;

import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


public class MoleculesModel implements TreeModel {


    private Node rootGroup;
    private Vector<TreeModelListener> treeModelListeners =
            new Vector<TreeModelListener>();

    public MoleculesModel(Node aGroup) {
        rootGroup = aGroup;
    }

    /**
     * The only event raised by this model is TreeStructureChanged with the
     * root as path, i.e. the whole tree has changed.
     */
    protected void fireTreeStructureChanged(Node oldRoot) {

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
        Node g = (Node) parent;
        return g.getGroupAt(index);
    }

    /**
     * Returns the number of child of parent.
     */
    public int getChildCount(Object parent) {
        Node g = (Node) parent;
        return g.getGroupsCount();
    }

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        Node g = (Node) parent;
        return g.getIndexOfGroup((Node) child);
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
        Node g = (Node) node;
        return g.getGroupsCount() == 0;
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
