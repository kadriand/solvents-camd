package co.unal.camd.descriptors;

import co.unal.camd.ga.haea.MoleculeSpace;
import co.unal.camd.properties.model.EnvironmentalProperties;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.view.CamdRunner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class EnvironmentalConstants {

    public static void main(String... args) throws IOException {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();

        StringBuilder solventInfo = new StringBuilder();
        solventInfo.append("smiles");
        solventInfo.append(",waterLC50FM");
        solventInfo.append(",waterLC50DM");
        solventInfo.append(",ratLD50");
        solventInfo.append(",waterLogWS");
        solventInfo.append(",waterBFC");
        solventInfo.append(",airEUAc");
        solventInfo.append(",airEUAnc");
        solventInfo.append(",airERAc");
        solventInfo.append(",airERAnc");
        System.out.println(solventInfo.toString());

        File file = new File("environment-30k.csv");
        for (int i = 0; i < 30000; i++) {
            Molecule molecule = MoleculeSpace.randomMolecule();
            EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
            solventInfo.append("\n" + molecule.getSmiles());
            solventInfo.append("," + environmentalProperties.getWaterLC50FM());
            solventInfo.append("," + environmentalProperties.getWaterLC50DM());
            solventInfo.append("," + environmentalProperties.getRatLD50());
            solventInfo.append("," + environmentalProperties.getWaterLogWS());
            solventInfo.append("," + environmentalProperties.getWaterBFC());
            solventInfo.append("," + environmentalProperties.getAirEUAc());
            solventInfo.append("," + environmentalProperties.getAirEUAnc());
            solventInfo.append("," + environmentalProperties.getAirERAc());
            solventInfo.append("," + environmentalProperties.getAirERAnc());
        }

        FileUtils.write(file, solventInfo);
    }
}
