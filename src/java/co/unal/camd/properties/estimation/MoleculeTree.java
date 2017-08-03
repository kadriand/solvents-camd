package co.unal.camd.properties.estimation;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

public class MoleculeTree extends JTree {
    MoleculesModel model;

    public MoleculeTree(Node aNode) {
        super(new MoleculesModel(aNode));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        Icon groupIcon = null;
        render.setLeafIcon(groupIcon);
        render.setClosedIcon(groupIcon);
        render.setOpenIcon(groupIcon);
        setCellRenderer(render);
    }

}
