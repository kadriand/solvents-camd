package co.unal.camd.util;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

public class CdkUtils {

    static SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());

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
            SmilesGenerator generator = new SmilesGenerator(SmiFlavor.Unique); // Or SmiFlavor.Absolute
            String canonicalSmiles = generator.create(molecule);
            return canonicalSmiles;
        } catch (InvalidSmilesException e) {
            e.printStackTrace();
            return "ERROR";
        } catch (CDKException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
