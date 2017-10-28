package co.unal.camd.view;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.unifac.FamilyGroup;
import co.unal.camd.model.molecule.Molecule;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


public abstract class ContributionGroupsPanel extends JPanel implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContributionGroupsPanel.class);

    private static final long serialVersionUID = 1L;

    private JLabel labelTemperature = null;
    private JButton jButton = null;
    private JButton buttonFixMolecule = null;
    private JButton newMoleculeBtn = null;
    private JButton ButtonSolveUNIFAC = null;
    private JLabel labelValence = null;
    private JLabel labelGroups = null;

    private JButton addMoleculeBtn = null;
    private JButton buttonSaveMolecule = null;
    private JComboBox<String> comboBoxValence = null;
    private JComboBox<String> comboBoxGroups = null;
    private JPanel jPanel = null;
    private JLabel labelIterations = null;
    private JPanel jPanel1 = null;
    private JButton buttonLoadProblem = null;
    private JButton buttonSaveProblem = null;
    private JPanel scrollPanel = null;
    private ArrayList<JCheckBox> jcheck = new ArrayList<>();
    private JScrollPane jScrollPane = null;


    CamdSetupWindow camdSetupWindow;
    JComboBox<String> comboBoxMolecules = null;
    Map<Integer, java.util.List<String>> selectionBoxGroups = new HashMap<>();

    @Getter
    JTextField temperatureInput = null;
    @Getter
    JTextField maxIterationsInput = null;
    @Getter
    JButton runEvolutionButton = null;

    JList groupsList = null;
    DefaultListModel<String> groupsListModel = new DefaultListModel<>();
    ArrayList<String> userMoleculeGroups = new ArrayList<>();
    int principal;

    public abstract void addMolecule(Molecule molecule);

    public abstract void saveMolecule(String filePath);

    public abstract void loadMolecule();

    void initialize() {
        GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
        gridBagConstraints25.gridx = 5;
        gridBagConstraints25.fill = GridBagConstraints.NONE;
        gridBagConstraints25.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints25.gridy = 0;
        JLabel labelWorkGroups = new JLabel();
        labelWorkGroups.setText("Groups to use in design");
        GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
        gridBagConstraints24.fill = GridBagConstraints.BOTH;
        gridBagConstraints24.gridy = 9;
        gridBagConstraints24.weightx = 1.0;
        gridBagConstraints24.weighty = 1.0;
        gridBagConstraints24.gridx = 5;
        GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
        gridBagConstraints23.gridx = 5;
        gridBagConstraints23.ipadx = 56;
        gridBagConstraints23.gridy = 11;
        GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
        gridBagConstraints20.gridx = 5;
        gridBagConstraints20.ipadx = 86;
        gridBagConstraints20.gridy = 13;
        GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
        gridBagConstraints19.gridx = 4;
        gridBagConstraints19.gridwidth = 1;
        gridBagConstraints19.ipadx = 23;
        gridBagConstraints19.gridy = 13;
        GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
        gridBagConstraints18.gridx = 4;
        gridBagConstraints18.gridy = 0;
        GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
        gridBagConstraints17.gridx = 1;
        gridBagConstraints17.gridy = 0;
        GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
        gridBagConstraints14.gridx = 2;
        gridBagConstraints14.anchor = GridBagConstraints.CENTER;
        gridBagConstraints14.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints14.fill = GridBagConstraints.BOTH;
        gridBagConstraints14.gridy = 11;
        GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
        gridBagConstraints13.gridx = 2;
        gridBagConstraints13.anchor = GridBagConstraints.WEST;
        gridBagConstraints13.fill = GridBagConstraints.BOTH;
        gridBagConstraints13.gridy = 13;
        GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
        gridBagConstraints22.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints22.gridy = 5;
        gridBagConstraints22.weightx = 1.0;
        gridBagConstraints22.gridx = 1;
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.fill = GridBagConstraints.BOTH;
        gridBagConstraints12.gridy = 3;
        gridBagConstraints12.weightx = 1.0;
        gridBagConstraints12.weighty = 1.0;
        gridBagConstraints12.gridx = 1;
        labelGroups = new JLabel();
        labelGroups.setText("Select Grupo");
        labelValence = new JLabel();
        labelValence.setText("Valence / type");
        GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
        gridBagConstraints51.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints51.gridy = 9;
        gridBagConstraints51.weightx = 0.0;
        gridBagConstraints51.ipadx = 0;
        gridBagConstraints51.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints51.gridwidth = 1;
        gridBagConstraints51.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints51.gridx = 4;
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 4;
        gridBagConstraints21.ipadx = 57;
        gridBagConstraints21.gridy = 11;
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.gridx = 1;
        gridBagConstraints7.anchor = GridBagConstraints.WEST;
        gridBagConstraints7.fill = GridBagConstraints.BOTH;
        gridBagConstraints7.gridy = 11;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.gridx = 2;
        gridBagConstraints6.anchor = GridBagConstraints.CENTER;
        gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints6.gridwidth = 1;
        gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints6.ipadx = 0;
        gridBagConstraints6.ipady = 0;
        gridBagConstraints6.weightx = -0.0;
        gridBagConstraints6.gridheight = 1;
        gridBagConstraints6.gridy = 9;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 1;
        gridBagConstraints5.gridwidth = 1;
        gridBagConstraints5.anchor = GridBagConstraints.WEST;
        gridBagConstraints5.fill = GridBagConstraints.BOTH;
        gridBagConstraints5.gridy = 13;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.BOTH;
        gridBagConstraints4.gridy = 9;
        gridBagConstraints4.weightx = 0.0;
        gridBagConstraints4.weighty = 1.0;
        gridBagConstraints4.gridwidth = 1;
        gridBagConstraints4.gridheight = 1;
        gridBagConstraints4.anchor = GridBagConstraints.CENTER;
        gridBagConstraints4.ipadx = 0;
        gridBagConstraints4.gridx = 1;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints3.gridy = 3;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints2.gridy = 3;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.gridx = 1;
        labelTemperature = new JLabel();
        labelTemperature.setText("Temperature (K)");
        this.setSize(498, 261);
        this.setLayout(new GridBagLayout());
        this.add(buildGroupsList(), gridBagConstraints4);
        this.add(buildJButton(), gridBagConstraints5);
        this.add(buildButtonFixMolecule(), gridBagConstraints6);
        this.add(buildNewMoleculeBtn(), gridBagConstraints7);
        this.add(getButtonSolveUnifac(), gridBagConstraints21);
        this.add(getComboBoxMolecules(), gridBagConstraints51);
        addMoleculeBtn = new JButton("Load Molecule");
        addMoleculeBtn.addActionListener(evt -> loadMolecule());
        this.add(addMoleculeBtn, gridBagConstraints13);
        this.add(getButtonSaveMolecule(), gridBagConstraints14);
        this.add(getJPanel(), gridBagConstraints17);
        this.add(getJPanel1(), gridBagConstraints18);
        this.add(getButtonLoadProblem(), gridBagConstraints19);
        this.add(runEvolutionButton, gridBagConstraints20);
        this.add(getButtonSaveProblem(), gridBagConstraints23);
        this.add(getJScrollPane(), gridBagConstraints24);
        this.add(labelWorkGroups, gridBagConstraints25);
    }

    /**
     * This method initializes ButtonSolveUNIFAC
     *
     * @return javax.swing.JButton
     */
    private JButton getButtonSolveUnifac() {
        if (ButtonSolveUNIFAC == null) {
            ButtonSolveUNIFAC = new JButton("Calcular GAMMAi");
            ButtonSolveUNIFAC.setText("gamma");
            ButtonSolveUNIFAC.addActionListener(evt -> {
                LOGGER.error("Please implement! class {}", this.getClass().getCanonicalName());
                //TODO implement as it should be: camdSetupWindow.setGamma(Gamma);
            });
        }
        return ButtonSolveUNIFAC;
    }

    /**
     * This method initializes comboBoxMolecules
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getComboBoxMolecules() {
        if (comboBoxMolecules == null) {
            comboBoxMolecules = new JComboBox<>();
            comboBoxMolecules.addActionListener(evt -> principal = comboBoxMolecules.getSelectedIndex());
        }
        comboBoxMolecules.addActionListener(this);
        return comboBoxMolecules;
    }


    /**
     * This method initializes groupsList
     *
     * @return javax.swing.JList
     */
    private JScrollPane buildGroupsList() {
        JScrollPane scroll = new JScrollPane();
        if (groupsList == null) {
            groupsList = new JList<>(groupsListModel);
            scroll = new JScrollPane(groupsList);
        }
        return scroll;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton buildJButton() {
        if (jButton == null) {
            jButton = new JButton("Remover Grupo");
            jButton.setText("Remove Group");
            jButton.addActionListener(evt -> {
                int group = groupsList.getSelectedIndex();
                groupsListModel.removeElementAt(group);
                userMoleculeGroups.remove(group);
            });

        }
        return jButton;
    }

    /**
     * This method initializes buttonFixMolecule
     *
     * @return javax.swing.JButton
     */
    private JButton buildButtonFixMolecule() {
        if (buttonFixMolecule == null) {
            buttonFixMolecule = new JButton(">>");
            buttonFixMolecule.setToolTipText("Establecer la molecula de usuario");
            buttonFixMolecule.setText(">>");
            LOGGER.error("Unimplemented function");
            //            buttonFixMolecule.addActionListener(evt -> addMolecule());
        }
        return buttonFixMolecule;
    }

    /**
     * This method initializes newMoleculeBtn
     *
     * @return javax.swing.JButton
     */
    private JButton buildNewMoleculeBtn() {
        if (newMoleculeBtn == null) {
            newMoleculeBtn = new JButton("Nueva Molecula");
            newMoleculeBtn.addActionListener(evt -> {
                for (int i = 0; i < userMoleculeGroups.size(); i++) {
                    userMoleculeGroups = new ArrayList<>();
                    groupsListModel.clear();
                    groupsList.removeAll();
                }
            });
        }
        return newMoleculeBtn;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("comboBoxChanged")) {
            comboBoxGroups.removeAllItems();
            selectionBoxGroups.get(comboBoxValence.getSelectedIndex()).forEach(groupName -> comboBoxGroups.addItem(groupName));
        }
    }

    /**
     * This method initializes buttonSaveMolecule
     *
     * @return javax.swing.JButton
     */
    private JButton getButtonSaveMolecule() {
        if (buttonSaveMolecule == null) {
            buttonSaveMolecule = new JButton();
            buttonSaveMolecule.setText("Save");
            buttonSaveMolecule.addActionListener(evt -> {
                String fileName = camdSetupWindow.selectFile("Save molecule");
                saveMolecule(fileName);
            });
        }
        return buttonSaveMolecule;
    }

    /**
     * This method initializes comboBoxValence
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getComboBoxValence() {
        if (comboBoxValence == null) {
            comboBoxValence = new JComboBox<>();
            String valences[] = {"0", "1", "2", "3", "4", "Aromatics", "Cyclics"};
            comboBoxValence = new JComboBox<>(valences);
            comboBoxValence.setSelectedIndex(0);
            comboBoxValence.addActionListener(this);
        }
        return comboBoxValence;
    }

    /**
     * This method initializes ComboBoxGroups
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox buildComboBoxGroups() {
        if (comboBoxGroups == null) {
            comboBoxGroups = new JComboBox<>();
            comboBoxGroups = new JComboBox<>(selectionBoxGroups.get(comboBoxValence.getSelectedIndex()).toArray(new String[0]));
            comboBoxGroups.addActionListener(evt -> {
                String group = (String) comboBoxGroups.getSelectedItem();
                if (group != null) {//se genera un null cada vez q hay cambio en valencia
                    groupsListModel.addElement(group);
                    userMoleculeGroups.add(group);
                }
            });

        }
        return comboBoxGroups;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            labelIterations = new JLabel();
            labelIterations.setText("Generations");
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.anchor = GridBagConstraints.CENTER;
            gridBagConstraints11.gridx = 1;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.ipadx = 16;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.fill = GridBagConstraints.VERTICAL;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.gridy = 3;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.anchor = GridBagConstraints.CENTER;
            gridBagConstraints10.gridx = 1;
            gridBagConstraints10.gridy = 2;
            gridBagConstraints10.ipadx = 16;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.fill = GridBagConstraints.VERTICAL;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 2;
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jPanel.add(labelValence, gridBagConstraints9);
            jPanel.add(getComboBoxValence(), gridBagConstraints10);
            jPanel.add(labelGroups, gridBagConstraints8);
            jPanel.add(buildComboBoxGroups(), gridBagConstraints11);
        }
        return jPanel;
    }

    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.anchor = GridBagConstraints.CENTER;
            gridBagConstraints1.gridwidth = 3;
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.weightx = 2.0;
            gridBagConstraints1.ipadx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints16.gridy = 0;
            gridBagConstraints16.weightx = 1.0;
            gridBagConstraints16.anchor = GridBagConstraints.CENTER;
            gridBagConstraints16.gridwidth = 1;
            gridBagConstraints16.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints16.ipadx = 40;
            gridBagConstraints16.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridheight = 0;
            gridBagConstraints.ipadx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.gridy = 1;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.ipadx = 9;
            gridBagConstraints15.anchor = GridBagConstraints.CENTER;
            gridBagConstraints15.gridy = 0;
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(labelIterations, gridBagConstraints15);
            jPanel1.add(labelTemperature, gridBagConstraints);
            jPanel1.add(maxIterationsInput, gridBagConstraints16);
            jPanel1.add(temperatureInput, gridBagConstraints1);
        }
        return jPanel1;
    }

    /**
     * This method initializes buttonLoadProblem
     *
     * @return javax.swing.JButton
     */
    private JButton getButtonLoadProblem() {
        if (buttonLoadProblem == null) {
            buttonLoadProblem = new JButton();
            buttonLoadProblem.setText("Load Problem");
            buttonLoadProblem.addActionListener(evt -> {
                String fileName = camdSetupWindow.selectFile("Load problem");
                //	loadProblem(fileName);
            });
        }
        return buttonLoadProblem;
    }

    /**
     * This method initializes buttonSaveProblem
     *
     * @return javax.swing.JButton
     */
    private JButton getButtonSaveProblem() {
        if (buttonSaveProblem == null) {
            buttonSaveProblem = new JButton();
            buttonSaveProblem.setText("Save Problem");
            buttonSaveProblem.addActionListener(evt -> {
                String fileName = camdSetupWindow.selectFile("Save problem");
                //		saveProblem(fileName);
            });
        }
        return buttonSaveProblem;
    }

    /**
     * This method initializes scrollPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel buildGroupFamiliesSelectionPanel() {
        if (scrollPanel == null) {
            scrollPanel = new JPanel();
            scrollPanel.setLayout(new BoxLayout(buildGroupFamiliesSelectionPanel(), BoxLayout.Y_AXIS));
            completeFamilyChecks();
        }
        return scrollPanel;
    }

    private void completeFamilyChecks() {
        for (Map.Entry<Integer, FamilyGroup> familyEntry : CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().entrySet()) {
            FamilyGroup familyGroup = familyEntry.getValue();
            JCheckBox familyCB = new JCheckBox(familyGroup.getName());
            familyCB.setToolTipText(familyGroup.readableMainGroups());
            if (IntStream.of(ProblemParameters.DEFAULT_UNCHECKED_FAMILIES).noneMatch(i -> i == familyEntry.getKey()))
                familyCB.setSelected(true);
            else {
                familyGroup.setProbability(0);
                familyCB.setEnabled(false);
            }

            familyCB.addItemListener(e -> {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                System.out.println((selected ? "SELECTED " : "DESELECTED ") + familyGroup);
                familyGroup.setProbability(selected ? 1.0 : 0.0);
            });
            jcheck.add(familyCB);
            scrollPanel.add(familyCB, null);
        }
    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(buildGroupFamiliesSelectionPanel());
        }
        return jScrollPane;
    }

}

