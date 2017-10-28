package co.unal.camd.view;

import co.unal.camd.methods.ProblemParameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author FAMILIA MORENO
 */
public class CamdSetupWindow extends CamdRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamdSetupWindow.class);

    private JTextField parentsPoolInput;
    private JTextField maxGroupsPerMoleculeInput;
    private ContributionGroupsPanel unifacAndDataPanel;

    /**
     * This is the default constructor
     */
    public CamdSetupWindow() {
        super();
        this.setResizable(true);
        // Unifac groups manager
        initialize();
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setSize(800, 600);
        this.setTitle("CAMD - Universidad Nacional");

        //TODO Cite icon
        //https://www.iconfinder.com/icons/2119347/molecule_scientific_icon#size=256
        // https://www.flaticon.com/free-icon/molecule_167735#term=molecule&page=1&position=1
        ImageIcon imageIcon = new ImageIcon(CamdSetupWindow.class.getResource("/molecule-2.png"));
        this.setIconImage(imageIcon.getImage());

        JLabel parentsPoolInputLabel = new JLabel("Parents pool of first generation");
        parentsPoolInput = new JTextField();
        parentsPoolInput.setText(String.valueOf(ProblemParameters.DEFAULT_PARENTS_POOL));
        parentsPoolInput.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            if (StringUtils.isNumeric(parentsPoolInput.getText())) {
                ProblemParameters.setParentsPoolSize(Integer.parseInt(parentsPoolInput.getText()));
                LOGGER.info("Parents pool size : {}", ProblemParameters.getParentsPoolSize());
            }
            checkRunButtonAvailability();
        });

        JLabel maxGroupsPerMoleculeLabel = new JLabel("Number of groups per molecule");
        maxGroupsPerMoleculeInput = new JTextField();
        maxGroupsPerMoleculeInput.setText(String.valueOf(ProblemParameters.DEFAULT_MAX_GROUPS_PER_MOLECULE));
        maxGroupsPerMoleculeInput.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            if (StringUtils.isNumeric(maxGroupsPerMoleculeInput.getText())) {
                ProblemParameters.setMaxGroupsPerMolecule(Integer.parseInt(maxGroupsPerMoleculeInput.getText()));
                LOGGER.info("Max Groups Per Molecule : {}", ProblemParameters.getMaxGroupsPerMolecule());
            }
            checkRunButtonAvailability();
        });
        unifacAndDataPanel = new UnifacGroupSelector(this);

        JPanel options = new JPanel();
        options.setLayout(new GridLayout(2, 2));
        options.add(parentsPoolInputLabel);
        options.add(parentsPoolInput);
        options.add(maxGroupsPerMoleculeLabel);
        options.add(maxGroupsPerMoleculeInput);
        add(options, BorderLayout.SOUTH);
        add(unifacAndDataPanel, BorderLayout.EAST);
        candidateSolventsTabs = new JTabbedPane();
        JScrollPane scroll = new JScrollPane(candidateSolventsTabs);
        add(scroll);
    }

    void checkRunButtonAvailability() {
        boolean enabled = StringUtils.isNumeric(parentsPoolInput.getText()) && StringUtils.isNumeric(maxGroupsPerMoleculeInput.getText());
        enabled = enabled && NumberUtils.isParsable(unifacAndDataPanel.temperatureInput.getText()) && StringUtils.isNumeric(unifacAndDataPanel.maxIterationsInput.getText());
        enabled = enabled && this.solute != null && this.solvent != null;
        unifacAndDataPanel.getRunEvolutionButton().setEnabled(enabled);
    }

    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
    }
}
