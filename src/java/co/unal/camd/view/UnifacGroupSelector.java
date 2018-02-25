package co.unal.camd.view;

import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.properties.parameters.unifac.ThermoPhysicalFirstOrderContribution;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class UnifacGroupSelector extends ContributionGroupsPanel {

    private int userMoleculesCount; //is the number of molecules creates by the user

    UnifacGroupSelector(CamdSetupWindow setupWindow) {
        this.camdSetupWindow = setupWindow;
        for (int i = 0; i < 5; i++) {
            List<String> namesList = CamdRunner.CONTRIBUTION_GROUPS.getValenceContributionGroups().get(i).stream()
                    .map(ThermoPhysicalFirstOrderContribution::getGroupName)
                    .collect(Collectors.toList());
            selectionBoxGroups.put(i, namesList);
        }
        //Aromatics
        List<Integer> mainCodes = new ArrayList<>();
        CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().get(3).getMainGroups().forEach(main -> mainCodes.add(main.getCode()));
        List<String> aromatics = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().values().stream()
                .filter(contributionGroupData -> mainCodes.contains(contributionGroupData.getMainGroup().getCode())).map(ThermoPhysicalFirstOrderContribution::getGroupName)
                .collect(Collectors.toList());
        selectionBoxGroups.put(5, aromatics);

        //Cyclics
        mainCodes.clear();
        CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().get(16).getMainGroups().forEach(main -> mainCodes.add(main.getCode()));
        CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().get(17).getMainGroups().forEach(main -> mainCodes.add(main.getCode()));
        List<String> cyclics = CamdRunner.CONTRIBUTION_GROUPS.getThermoPhysicalFirstOrderContributions().values().stream()
                .filter(contributionGroupData -> mainCodes.contains(contributionGroupData.getMainGroup().getCode())).map(ThermoPhysicalFirstOrderContribution::getGroupName)
                .collect(Collectors.toList());
        selectionBoxGroups.put(6, cyclics);

        textFieldTemperature = new JTextField(String.valueOf(setupWindow.getTemperature()));
        textFieldTemperature.addActionListener(evt -> camdSetupWindow.setTemperature(Double.parseDouble((textFieldTemperature.getText()))));

        initialize();
    }

    @Override
    public void loadMolecule() {
        String fileName = camdSetupWindow.selectFile();
        userMoleculeGroups = new ArrayList<>();
        loadMoleculeGroupsFile(fileName);
        addMolecule();
    }

    /**
     * Lee los datos del archivo
     *
     * @param filePath
     * @return Datos leidos del archivo
     */
    private void loadMoleculeGroupsFile(String filePath) {
        if (filePath == null)
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String newLine = br.readLine();
            while (newLine != null) {
                int num = Integer.parseInt(newLine.trim());
                String groupName = CamdRunner.CONTRIBUTION_GROUPS.findGroupName(num);
                userMoleculeGroups.add(groupName);
                // System.out.println("linea" + line);
                // System.out.print("*** group : " + num);
                newLine = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMolecule() {
        int n = userMoleculeGroups.size();
        int[] moleculeGroupsIds = new int[n];
        for (int i = 0; i < n; i++) {
            String gr = userMoleculeGroups.get(i);
            moleculeGroupsIds[i] = CamdRunner.CONTRIBUTION_GROUPS.findGroupCode(gr);
        }

        MoleculeGroups groupArray = new MoleculeGroups(moleculeGroupsIds, 0.01);
        //(Double.parseDouble(OptionPane.showInputDialog("Ingrese la Composición"))));

        camdSetupWindow.getUserMolecules().add(groupArray);
        String moleculeLabel = userMoleculesCount + ". " + groupArray.readableString();
        System.out.println("New molecule: " + moleculeLabel + " " + Arrays.toString(moleculeGroupsIds));
        comboBoxMolecules.addItem(moleculeLabel);
        comboBoxMolecules.setSelectedIndex(0);
        principal = comboBoxMolecules.getSelectedIndex();
        userMoleculesCount++;
        groupsListModel.clear();
        groupsList.removeAll();
        userMoleculeGroups = new ArrayList<>();
    }

    /**
     * Guarda datos en el archivo
     *
     * @param filePath Ruta del archivo
     */
    @Override
    public void saveMolecule(String filePath) {
        File file = new File(filePath);
        try (PrintWriter out = new PrintWriter(file)) {
            for (int i = 0; i < userMoleculeGroups.size(); i++) {
                String groupName = Integer.toString(CamdRunner.CONTRIBUTION_GROUPS.findGroupCode(userMoleculeGroups.get(i)));
                out.println(groupName);
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

