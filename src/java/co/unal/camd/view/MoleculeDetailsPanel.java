package co.unal.camd.view;

import co.unal.camd.availability.CompoundEntry;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.utils.CdkUtils;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.svg.SVGDocument;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.List;

public class MoleculeDetailsPanel extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoleculeDetailsPanel.class);

    private Molecule molecule;

    MoleculeDetailsPanel(Molecule solvent) {

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));
        setBackground(Color.WHITE);
        setOpaque(true);

        this.molecule = solvent;
        buildMoleculeCanvas();
        buildMoleculePropertiesTable();
    }

    private void buildMoleculePropertiesTable() {
        String[] columnNames = {"ATTRIBUTE", "VALUE"};
        List<CompoundEntry> availabilities = molecule.getAvailabilityEntries();
        Object[][] data = {
                {"Availability", availabilities != null && availabilities.size() > 0 ? availabilities.get(0).getSource() : "- no -"},
                {"SMILES", molecule.getSmiles()},
                {"# Groups", molecule.getSize()},
                {"Functional groups", molecule.getThermoPhysicalProperties().getGroupsSummary()},
                {"Fitness", readableDecimals(molecule.getFitness())},
                {"KS", molecule.getObjectiveValues() != null ? readableDecimals(molecule.getObjectiveValues()[0]) : "-"},
                {"Environmental Index", molecule.getObjectiveValues() != null ? readableDecimals(molecule.getObjectiveValues()[1]) : "-"},
                {"Penalization", readableDecimals(molecule.getPenalization())},
                {"Molecular weight", readableDecimals(molecule.getMolecularWeight())}
        };
        MoleculePropertiesTable propertiesTable = new MoleculePropertiesTable(data, columnNames, availabilities);
        this.add(propertiesTable, BorderLayout.CENTER);
    }

    private String readableDecimals(Double number) {
        if (number == null)
            return "-";
        else
            return String.format("%.3f", number);
    }

    private void buildMoleculeCanvas() {
        try {
            final byte[] bytes = CdkUtils.moleculeImageBytes(this.molecule.getSmiles(), this.molecule.getSmiles());
            JSVGCanvas moleculeCanvas = new JSVGCanvas();
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                SVGDocument document = factory.createSVGDocument("", bais);
                document.getRootElement().setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "300");
                document.getRootElement().setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "300");
                moleculeCanvas.setSVGDocument(document);
            }

            this.add(moleculeCanvas, BorderLayout.CENTER);
        } catch (Exception e) {
            LOGGER.error("Problems creating molecule image", e);
            ImageIcon imageIcon = new ImageIcon(MoleculeDetailsPanel.class.getResource("/chat-default.png"));
            this.add(new JLabel(imageIcon));
        }
    }

    public static JFrame showMoleculeFrame(Molecule molecule, String frameTitle) {
        LOGGER.info("SMILES " + molecule.getSmiles());
        JFrame frame = new JFrame();
        frame.setTitle(frameTitle);
        frame.add(new MoleculeDetailsPanel(molecule));
        frame.setSize(new Dimension(400, 400));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    public static void main(String... args) {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        originalUnifacMolecule = "21.1";
        originalUnifacMolecule = "1.2.3(81).42";

        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule randomMolecule = new Molecule(rootFunctionalGroupNode);
        System.out.println("\nThermoPhysicalData");
        System.out.println(randomMolecule.getThermoPhysicalProperties());
        System.out.println(randomMolecule.getEnvironmentalProperties());
        //        Molecule randomMolecule = MoleculeSpace.randomMolecule();
        //        Molecule randomMolecule1 = MoleculeSpace.randomMolecule();
        //        Molecule randomMolecule2 = MoleculeSpace.randomMolecule();
        //        Molecule randomMolecule3 = MoleculeSpace.randomMolecule();

        JFrame firstFrame = MoleculeDetailsPanel.showMoleculeFrame(randomMolecule, "Random molecule");
        firstFrame.setLocation(firstFrame.getX() - firstFrame.getWidth() / 2, firstFrame.getY() - firstFrame.getHeight() / 2);
        //        CandidateSolventPanel.showMoleculeFrame(randomMolecule1, "Random molecule 0")
        //                .setLocation(firstFrame.getX(), firstFrame.getY() + firstFrame.getHeight());
        //        CandidateSolventPanel.showMoleculeFrame(randomMolecule2, "Random molecule 1")
        //                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY());
        //        CandidateSolventPanel.showMoleculeFrame(randomMolecule3, "Random molecule 2")
        //                .setLocation(firstFrame.getX() + firstFrame.getWidth(), firstFrame.getY() + firstFrame.getHeight());
    }


}
