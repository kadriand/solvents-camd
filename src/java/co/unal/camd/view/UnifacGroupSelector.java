package co.unal.camd.view;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.unifac.UnifacSubGroup;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.model.molecule.UnifacGroupNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class UnifacGroupSelector extends ContributionGroupsPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifacGroupSelector.class);

    UnifacGroupSelector(CamdSetupWindow setupWindow) {
        this.camdSetupWindow = setupWindow;
        for (int i = 0; i < 5; i++) {
            List<String> namesList = CamdRunner.CONTRIBUTION_GROUPS.getValenceContributionGroups().get(i).stream()
                    .map(UnifacSubGroup::getGroupName)
                    .collect(Collectors.toList());
            selectionBoxGroups.put(i, namesList);
        }
        //Aromatics
        List<Integer> mainCodes = new ArrayList<>();
        CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().get(3).getMainGroups().forEach(main -> mainCodes.add(main.getCode()));
        List<String> aromatics = CamdRunner.CONTRIBUTION_GROUPS.getUnifacContributions().values().stream()
                .filter(contributionGroupData -> mainCodes.contains(contributionGroupData.getMainGroup().getCode())).map(UnifacSubGroup::getGroupName)
                .collect(Collectors.toList());
        selectionBoxGroups.put(5, aromatics);

        //Cyclics
        mainCodes.clear();
        CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().get(16).getMainGroups().forEach(main -> mainCodes.add(main.getCode()));
        CamdRunner.CONTRIBUTION_GROUPS.getUnifacFamilyGroups().get(17).getMainGroups().forEach(main -> mainCodes.add(main.getCode()));
        List<String> cyclics = CamdRunner.CONTRIBUTION_GROUPS.getUnifacContributions().values().stream()
                .filter(contributionGroupData -> mainCodes.contains(contributionGroupData.getMainGroup().getCode())).map(UnifacSubGroup::getGroupName)
                .collect(Collectors.toList());
        selectionBoxGroups.put(6, cyclics);

        temperatureInput = new JTextField(String.valueOf(ProblemParameters.DEFAULT_TEMPERATURE));
        temperatureInput.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            if (NumberUtils.isParsable(temperatureInput.getText())) {
                ProblemParameters.setTemperature(Double.parseDouble((temperatureInput.getText())));
                LOGGER.info("Temperature : {}", ProblemParameters.getTemperature());
            }
            setupWindow.checkRunButtonAvailability();
        });

        maxIterationsInput = new JTextField();
        maxIterationsInput.setText(String.valueOf(ProblemParameters.DEFAULT_MAX_ITERATIONS));
        maxIterationsInput.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            if (StringUtils.isNumeric(maxIterationsInput.getText())) {
                ProblemParameters.setMaxIterations(Integer.parseInt((maxIterationsInput.getText())));
                LOGGER.info("Max Iterations : {}", ProblemParameters.getMaxIterations());
            }
            setupWindow.checkRunButtonAvailability();
        });

        runEvolutionButton = new JButton();
        runEvolutionButton.setText("RUN");
        runEvolutionButton.setEnabled(false);
        runEvolutionButton.addActionListener(evt -> camdSetupWindow.designSuitableMolecules());

        initialize();
    }

    @Override
    public void loadMolecule() {
        String filePath = camdSetupWindow.selectFile(camdSetupWindow.getSolute() == null ? "Select solute" : camdSetupWindow.getSolvent() == null ? "Select solvent" : "Select solute");
        userMoleculeGroups = new ArrayList<>();

        try {
            String moleculeConfiguration = FileUtils.readFileToString(new File(filePath));
            UnifacGroupNode rootGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(moleculeConfiguration);
            Molecule newMolecule = new Molecule(rootGroupNode);
            addMolecule(newMolecule);
        } catch (IOException e) {
            LOGGER.error("Unvalid file selected", e);
        }
    }

    @Override
    public void addMolecule(Molecule molecule) {
        if (camdSetupWindow.getSolute() == null)
            camdSetupWindow.setSolute(molecule);
        else if (camdSetupWindow.getSolvent() == null)
            camdSetupWindow.setSolvent(molecule);
        else {
            camdSetupWindow.setSolute(molecule).setSolvent(null);
            comboBoxMolecules.removeAllItems();
        }

        String moleculeLabel = molecule.getGroupsText() + " " + molecule.getSmiles();
        System.out.println("New molecule: " + moleculeLabel);
        comboBoxMolecules.addItem(moleculeLabel);
        comboBoxMolecules.setSelectedIndex(comboBoxMolecules.getItemCount() - 1);
        principal = comboBoxMolecules.getSelectedIndex();
        groupsListModel.clear();
        groupsList.removeAll();
        userMoleculeGroups = new ArrayList<>();

        camdSetupWindow.checkRunButtonAvailability();
    }

    /**
     * Guarda datos en el archivo
     *
     * @param filePath Ruta del archivo
     */
    @Override
    public void saveMolecule(String filePath) {
        LOGGER.error("Unimplemented method");
    }

}

