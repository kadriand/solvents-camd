package co.unal.camd.view;

import co.unal.camd.properties.estimation.MoleculeGroups;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;


public class UnifacGroupSelector extends ContributionGroupsPanel {

    private int userMoleculesCount; //is the number of molecules creates by the user

    UnifacGroupSelector(CamdSetupWindow setupWindow) {
        this.camdSetupWindow = setupWindow;
        allGroups = new String[8][28];
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 27; j++)
                allGroups[i][j] = (CamdRunner.CONTRIBUTION_GROUPS.getGroupsData()[i][j + 1][2]);
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
                // System.out.println("num" + num);
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
        //System.out.println("n "+n);
        int[] moleculeGroupsIds = new int[n];
        for (int i = 0; i < n; i++) {
            String gr = userMoleculeGroups.get(i);
            moleculeGroupsIds[i] = CamdRunner.CONTRIBUTION_GROUPS.findGroupCode(gr);
        }

        System.out.println(Arrays.toString(moleculeGroupsIds));
        MoleculeGroups groupArray = new MoleculeGroups(moleculeGroupsIds, 0.01);
        //(Double.parseDouble(OptionPane.showInputDialog("Ingrese la Composición"))));

        camdSetupWindow.getUserMolecules().add(groupArray);

        String moleculeLabel = userMoleculesCount + ". " + groupArray.readableString();
        comboBoxMolecules.addItem(moleculeLabel);
        comboBoxMolecules.setSelectedIndex(0);
        principal = comboBoxMolecules.getSelectedIndex();
        userMoleculesCount++;
        groupsListModel.clear();
        groupsList.removeAll();
        for (int i = 0; i < userMoleculeGroups.size(); i++)
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

