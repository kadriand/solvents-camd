/**
 *
 */
package co.unal.camd.view;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.core.GroupArray;
import co.unal.camd.core.Node;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author FAMILIA MORENO
 */
public class CamdSetupWindow extends CamdRun implements ActionListener {

    private JTextField textFieldParents;
    private JTextField textFieldMaxMolecules;
    private PanelDataAndUnifac aPanel;

    /**
     * This is the default constructor
     */
    public CamdSetupWindow() {
        super();
        this.setResizable(true);

        // Unifac parameters manager
        parametersManager = new ContributionParametersManager();
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        moleculesUser = new ArrayList<>();
        temperature = 298.15;
        iterations = 50;
        this.setSize(600, 600);
        this.setTitle("CAMD");
        JLabel labelParents = new JLabel("Número de padres para la primera generación");
        textFieldParents = new JTextField();
        textFieldParents.setEnabled(false);
        parents = 1;

        textFieldParents.addActionListener(evt -> {
            parents = Integer.parseInt(textFieldParents.getText());
            System.out.println("Molecules init :" + parents);

            //				HERE THE ALGORITHM STARTS
            createMolec();
            textFieldParents.setEnabled(false);
            textFieldMaxMolecules.setEnabled(true);
        });

        JLabel labelMaxMolecules = new JLabel("Número máximo de grupos por molécula");
        textFieldMaxMolecules = new JTextField();
        maxGroups = 0;
        textFieldMaxMolecules.addActionListener(evt -> {
            maxGroups = Integer.parseInt(textFieldMaxMolecules.getText());
            System.out.println("Num Max Molecs :" + maxGroups);
            textFieldParents.setEnabled(true);
            textFieldMaxMolecules.setEnabled(false);
        });
        aPanel = new PanelDataAndUnifac(this);

        JPanel options = new JPanel();
        options.setLayout(new GridLayout(2, 2));
        options.add(labelParents);
        options.add(textFieldParents);
        options.add(labelMaxMolecules);
        options.add(textFieldMaxMolecules);
        add(options, BorderLayout.SOUTH);
        add(aPanel, BorderLayout.EAST);
        tab = new JTabbedPane();
        JScrollPane scroll = new JScrollPane(tab);
        add(scroll);
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public DefaultMutableTreeNode moleculeToJtree(Node molec, DefaultMutableTreeNode node) {
        for (int i = 0; i < molec.getGroupsCount(); i++) {
            String n = parametersManager.getName(molec.getGroupAt(i).getRootNode());
            DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(n);

            moleculeToJtree(molec.getGroupAt(i), aNode);
            node.add(aNode);
        }
        return node;
    }

    public ContributionParametersManager getGC() {
        return parametersManager;
    }

    public void addMoleculesUser(GroupArray aMolecUser) {
        moleculesUser.add(aMolecUser);
    }

    public String getMoleculesUser(int i) {
        String show = moleculesUser.get(i).toString();
        return show;
    }

    public ArrayList<GroupArray> getMolecUser() {
        return moleculesUser;
    }

    public ContributionParametersManager getGenChemistry() {
        return parametersManager;
    }

    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub

    }
}
