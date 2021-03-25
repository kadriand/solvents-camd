package co.unal.camd.util;

import net.sf.jniinchi.INCHI_RET;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.depict.Depiction;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class CdkUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdkUtils.class);

    private static SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    private static SmilesGenerator smilesGenerator = new SmilesGenerator(SmiFlavor.Absolute); // Or SmiFlavor.Unique

    /**
     * {@link <a href="https://www.ncbi.nlm.nih.gov/pmc/articles/PMC1360656/">'ZINC – A Free Database of Commercially Available Compounds for Virtual Screening'</a>}
     * {@link <a href="https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5461230/">'The Chemistry Development Kit (CDK) v2.0'</a>}
     * {@link <a href="https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5461230/">'The Chemistry Development Kit (CDK) v2.0'</a>}
     *
     * @return
     */
    public static String smilesToUnique(String smiles) {
        try {
            IAtomContainer molecule = smilesParser.parseSmiles(smiles);
            String canonicalSmiles = smilesGenerator.create(molecule);
            return canonicalSmiles;
        } catch (CDKException e) {
            if (smiles.matches(".*(\\W|^)(SMILES|smiles)(\\W|$).*"))
                LOGGER.error("Bad smiles trace ommited : " + smiles);
            else
                LOGGER.error("Problerms parsing smiles : {}", smiles, e);
            return null;
        }
    }

    public static IAtomContainer moleculeFromInChI(String inchi) {
        try {
            InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
            // Get InChIToStructure
            InChIToStructure intostruct = factory.getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
            INCHI_RET ret = intostruct.getReturnStatus();
            if (ret == INCHI_RET.WARNING) {
                // Structure generated, but with warning message
                System.out.println("InChI warning: " + intostruct.getMessage());
            } else if (ret != INCHI_RET.OKAY) {
                // Structure generation failed
                throw new CDKException("Structure generation failed failed: " + ret.toString()
                        + " [" + intostruct.getMessage() + "]");
            }

            IAtomContainer container = intostruct.getAtomContainer();
            return container;
        } catch (CDKException e) {
            LOGGER.error("Problerms reading InChi : {}", inchi, e);
            return null;
        }
    }

    private static void inconsistentParsingMolecules() {
        IAtomContainer atomContainer = moleculeFromInChI("InChI=1S/C26H42N2O5/c1-17(2)20(16-21-25(31-7)28-24(18(3)4)26(27-21)32-8)14-19-10-11-22(30-6)23(15-19)33-13-9-12-29-5/h10-11,15,17-18,20-21,24H,9,12-14,16H2,1-8H3/t20-,21-,24+/m0/s1");
        String atomContainerString = smilesFromMolecule(atomContainer);
        System.out.println(atomContainerString);
        System.out.println(smilesToUnique("COCCCOc1cc(C[C@@H](C[C@@H]2N=C(OC)[C@@H](C(C)C)N=C2OC)C(C)C)ccc1OC"));
        System.out.println(smilesToUnique("COCCCOC1=C(OC)C=CC(C[C@@H](C[C@@H]2N=C(OC)[C@H](N=C2OC)C(C)C)C(C)C)=C1"));
    }

    private static String smilesFromMolecule(IAtomContainer molecule) {
        try {
            String canonicalSmiles = smilesGenerator.create(molecule);
            return canonicalSmiles;
        } catch (CDKException e) {
            LOGGER.error("Problerms parsing smiles", e);
            return "ERROR";
        }
    }

    public static byte[] moleculeImageBytes(String smiles, String name) {
        try (ByteArrayOutputStream iout = new ByteArrayOutputStream()) {
            IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
            SmilesParser smipar = new SmilesParser(bldr);
            IAtomContainer mol = smipar.parseSmiles(smiles);
            mol.setProperty(CDKConstants.TITLE, name); // title already set from input!
            new DepictionGenerator()
                    .withMolTitle()
                    /*.withOuterGlowHighlight()*/
                    .depict(mol)
                    .writeTo(Depiction.SVG_FMT, iout);
            return iout.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Problerms drawing smiles : {}", smiles, e);
            return null;
        }
    }

    public static BufferedImage moleculeImage(String smiles, String name) throws CDKException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles(smiles);
        mol.setProperty(CDKConstants.TITLE, name); // title already set from input!
        BufferedImage bufferedImage = new DepictionGenerator()
                .withMolTitle()
                //                .withOuterGlowHighlight()
                .depict(mol)
                .toImg();
        return bufferedImage;
    }

    public static void showMolecule(String smiles, String name) {
        try {
            BufferedImage bufferedImage = moleculeImage(smiles, name);

            JFrame frame = new JFrame();
            ImageIcon imageIcon = new ImageIcon(bufferedImage);
            frame.getContentPane().add(new JLabel(imageIcon));
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        } catch (Exception e) {
            LOGGER.error("Problerms drawing smiles : {}", smiles, e);
        }
    }

    public static void main(String[] args) {
        showMolecule("C/C1=C/[C@H](C)CC[C@@H](C)[C@@H](C)\\C=C/C1", "molec");
        //        inconsistentParsingMolecules();
    }
}
