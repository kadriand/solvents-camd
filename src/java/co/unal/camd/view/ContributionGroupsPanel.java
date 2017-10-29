package co.unal.camd.view;

import co.unal.camd.properties.methods.UnifacMethod;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public abstract class ContributionGroupsPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JLabel labelTemperature = null;
    private JTextField textFieldTemperature = null;

    JComboBox<String> comboBoxMolecules = null;
    private JButton jButton = null;
    private JButton buttonFixMolecule = null;
    private JButton newMoleculeBtn = null;
    private JButton ButtonSolveUNIFAC = null;
    private JLabel labelValence = null;
    private JLabel labelGroups = null;

    private JButton loadMoleculeBtn = null;
    private JButton buttonSaveMolecule = null;
    private JComboBox<String> comboBoxValence = null;
    private JComboBox<String> comboBoxGroups = null;
    private JPanel jPanel = null;
    private JLabel labelIterations = null;
    private JTextField textFieldIterations = null;
    private JPanel jPanel1 = null;
    private JButton buttonLoadProblem = null;
    private JButton buttonSaveProblem = null;
    private JButton buttonRunEvolution = null;
    private JPanel scrollPanel = null;
    private ArrayList<JCheckBox> jcheck = new ArrayList<>();
    private JScrollPane jScrollPane = null;

    private int co;
    private int valence;
    CamdSetupWindow camdSetupWindow;
    String[][] allGroups;
    JList groupsList = null;
    DefaultListModel<String> groupsListModel = new DefaultListModel<>();
    ArrayList<String> userMoleculeGroups = new ArrayList<>();
    int principal;

    public abstract void addMolecule();

    public abstract void saveMolecule(String filePath);

    public abstract void loadMolecule();

    void initialize() {
        GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
        gridBagConstraints25.gridx = 5;
        gridBagConstraints25.fill = GridBagConstraints.NONE;
        gridBagConstraints25.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints25.gridy = 0;
        JLabel labelWorkGroups = new JLabel();
        labelWorkGroups.setText("Grupos a trabajar");
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
        gridBagConstraints20.ipadx = 0;
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
        this.add(getJButton1(), gridBagConstraints13);
        this.add(getButtonSaveMolecule(), gridBagConstraints14);
        this.add(getJPanel(), gridBagConstraints17);
        this.add(getJPanel1(), gridBagConstraints18);
        this.add(getButtonLoadProblem(), gridBagConstraints19);
        this.add(getButtonSaveProblem(), gridBagConstraints20);
        this.add(getButtonRunEvolution(), gridBagConstraints23);
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
                UnifacMethod unifac = new UnifacMethod();
                double Gamma = unifac.getMethodResult(camdSetupWindow.getUserMolecules(), principal, camdSetupWindow.getTemperature());
                System.out.println("El GAMMAi es: " + Gamma);
                //TODO implement as it should be
                //                camdSetupWindow.setGamma(Gamma);
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
     * This method initializes textFieldTemperature
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTextFieldTemperature() {
        if (textFieldTemperature == null) {
            textFieldTemperature = new JTextField("298.15");

            textFieldTemperature.addActionListener(evt -> camdSetupWindow.setTemperature(Double.parseDouble((textFieldTemperature.getText()))));
        }

        return textFieldTemperature;
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
            buttonFixMolecule.addActionListener(evt -> addMolecule());
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
            valence = comboBoxValence.getSelectedIndex();
            comboBoxGroups.removeAllItems();
            for (int i = 0; i < 28; i++)
                comboBoxGroups.addItem(allGroups[valence][i]);
        }
        for (int i = 0; i < jcheck.size(); i++)
            if (!jcheck.get(i).isSelected())
                CamdRunner.CONTRIBUTION_GROUPS.setProbability(i, 0);
        //	System.out.println("aa");
    }

    /**
     * This method initializes loadMoleculeBtn
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (loadMoleculeBtn == null) {
            loadMoleculeBtn = new JButton("Load");
            loadMoleculeBtn.setText("Load");
            loadMoleculeBtn.addActionListener(evt -> loadMolecule());
        }
        return loadMoleculeBtn;
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
                String fileName = camdSetupWindow.selectFile();
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
            String valences[] = {"1", "2", "3", "4", "Aromáticos", "Ciclos", "0"};
            comboBoxValence = new JComboBox<>(valences);
            comboBoxValence.setSelectedIndex(0);
            valence = comboBoxValence.getSelectedIndex();
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
            comboBoxGroups = new JComboBox<>(allGroups[valence]);
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
            labelIterations.setText("Iterations");
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
     * This method initializes textFieldIterations
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTextFieldIterations() {
        if (textFieldIterations == null) {
            textFieldIterations = new JTextField();
            textFieldIterations.setText("50");
            textFieldIterations.addActionListener(evt -> {
                int num = Integer.parseInt(textFieldIterations.getText());
                camdSetupWindow.setIterations(num);
            });
        }
        return textFieldIterations;
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
            jPanel1.add(getTextFieldIterations(), gridBagConstraints16);
            jPanel1.add(getTextFieldTemperature(), gridBagConstraints1);
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
            buttonLoadProblem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    String fileName = camdSetupWindow.selectFile();
                    //	loadProblem(fileName);
                }
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
            buttonSaveProblem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    String fileName = camdSetupWindow.selectFile();
                    //		saveProblem(fileName);
                }
            });
        }
        return buttonSaveProblem;
    }

    /**
     * This method initializes buttonRunEvolution
     *
     * @return javax.swing.JButton
     */
    private JButton getButtonRunEvolution() {
        if (buttonRunEvolution == null) {
            buttonRunEvolution = new JButton();
            buttonRunEvolution.setText("Run");
            buttonRunEvolution.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    String fileName = camdSetupWindow.selectFile();
                    //runEvolution(fileName);
                }
            });
        }
        return buttonRunEvolution;
    }

    /**
     * This method initializes scrollPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel2() {
        if (scrollPanel == null) {
            scrollPanel = new JPanel();
            scrollPanel.setLayout(new BoxLayout(getJPanel2(), BoxLayout.Y_AXIS));
            co = 0;
            for (int i = 0; i < 23; i++) {
                scrollPanel.add(getJCheck(), null);
                co = co + 1;
            }

        }
        return scrollPanel;
    }


    private JCheckBox getJCheck() {
        String name = CamdRunner.CONTRIBUTION_GROUPS.getGlobalGroupName(co + 1);
        JCheckBox j = new JCheckBox(name);
        j.setToolTipText(CamdRunner.CONTRIBUTION_GROUPS.getPrincipalGroupNames(co + 1));
        jcheck.add(j);
        jcheck.get(co).setSelected(true);
        return jcheck.get(co);
    }


    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getJPanel2());
        }
        return jScrollPane;
    }

}  //  @jve:decl-index=0:visual-constraint="27,-30"

