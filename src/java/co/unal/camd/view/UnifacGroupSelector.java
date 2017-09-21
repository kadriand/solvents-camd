package co.unal.camd.view;

import co.unal.camd.properties.estimation.GroupArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class UnifacGroupSelector extends ContributionGroupsPanel {

    private int count; //is the number of molecules creates by the user

    public UnifacGroupSelector(CamdSetupWindow setupWindow) {
        this.camdSetupWindow = setupWindow;
        allGroups = new String[8][28];
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 27; j++)
                allGroups[i][j] = (setupWindow.getContributionGroups().getAllGroups()[i][j + 1][2]);
        initialize();
    }

    @Override
    public void fixMolecule() {
        int n = aMolec.size();
        //System.out.println("n "+n);
        int[] aMolecule = new int[n];
        for (int i = 0; i < n; i++) {
            String gr = aMolec.get(i);
            //System.out.println(gr+camdSetupWindow.getContributionGroups().getRefCode(gr)+" bvb");
            aMolecule[i] = camdSetupWindow.getContributionGroups().getRefCode(gr);
        }

        GroupArray aGroupArray = new GroupArray(aMolecule, 0.01);//(Double.parseDouble(
        //JOptionPane.showInputDialog("Ingrese la Composici�n"))));

        camdSetupWindow.addMoleculesUser(aGroupArray);
        String show = count + ". " + aGroupArray.toString(camdSetupWindow.getContributionGroups());
        comboBoxMolecules.addItem(show);
        comboBoxMolecules.setSelectedIndex(0);
        principal = comboBoxMolecules.getSelectedIndex();
        count = count + 1;
        listG.clear();
        listGroups.removeAll();
        for (int i = 0; i < aMolec.size(); i++)
            aMolec = new ArrayList<>();
    }

    /**
     * Lee los datos del archivo
     *
     * @param filePath Ruta del archivo
     * @return Datos leidos del archivo
     */
    public void loadMoleculeGroupsFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String newLine = br.readLine();
            while (newLine != null) {
                int num = Integer.parseInt(newLine.trim());
                String line = camdSetupWindow.getContributionGroups().getGroupName(num);
                aMolec.add(line);
                //                System.out.println("linea" + line);
                //                System.out.println("num" + num);
                newLine = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            for (int i = 0; i < aMolec.size(); i++) {
                String groupName = Integer.toString(camdSetupWindow.getContributionGroups().getRefCode(aMolec.get(i)));
                out.println(groupName);
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadMolecule() {
        String fileName = camdSetupWindow.selectFile();
        aMolec = new ArrayList<>();
        loadMoleculeGroupsFile(fileName);
        fixMolecule();
    }

}

