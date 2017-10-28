/**
 *
 */
package co.unal.camd.view;

import co.unal.camd.properties.ProblemParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author FAMILIA MORENO
 */
public class CamdSetupWindow extends CamdRunner implements ActionListener {

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
     * @return void
     */
    private void initialize() {
        userMolecules = new ArrayList<>();

        this.setSize(600, 600);
        this.setTitle("CAMD - Universidad Nacional");

        //TODO Cite icon
        //https://www.iconfinder.com/icons/2119347/molecule_scientific_icon#size=256
        // https://www.flaticon.com/free-icon/molecule_167735#term=molecule&page=1&position=1
        ImageIcon imageIcon = new ImageIcon(CamdSetupWindow.class.getResource("/molecule-2.png"));
        this.setIconImage(imageIcon.getImage());

        JLabel parentsPoolInputLabel = new JLabel("Número de padres para la primera generación");
        parentsPoolInput = new JTextField();
        parentsPoolInput.setEnabled(false);
        parentsPoolInput.setText(String.valueOf(ProblemParameters.DEFAULT_PARENTS_POOL));
        parentsPoolInput.addActionListener(evt -> {
            ProblemParameters.setParentsPoolSize(Integer.parseInt(parentsPoolInput.getText()));
            LOGGER.info("Parents pool size : {}", ProblemParameters.getParentsPoolSize());

            // HERE THE ALGORITHM STARTS
            designSuitableMolecules();

            parentsPoolInput.setEnabled(false);
            maxGroupsPerMoleculeInput.setEnabled(true);
        });

        JLabel maxGroupsPerMoleculeLabel = new JLabel("Número máximo de grupos por molécula");
        maxGroupsPerMoleculeInput = new JTextField();
        maxGroupsPerMoleculeInput.setText(String.valueOf(ProblemParameters.DEFAULT_MAX_GROUPS_PER_MOLECULE));
        maxGroupsPerMoleculeInput.addActionListener(evt -> {
            ProblemParameters.setMaxGroupsPerMolecule(Integer.parseInt(maxGroupsPerMoleculeInput.getText()));
            LOGGER.info("Max Groups Per Molecule size : {}", ProblemParameters.getParentsPoolSize());
            parentsPoolInput.setEnabled(true);
            maxGroupsPerMoleculeInput.setEnabled(false);
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

    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
    }
}
