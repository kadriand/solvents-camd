package co.unal.camd.utils;

import co.unal.camd.ga.haea.MoleculeSpace;
import co.unal.camd.model.EnvironmentalProperties;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class EnvironmentalConstants {

    public static void main(String... args) throws IOException {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();

        StringBuilder solventInfo = new StringBuilder();
        solventInfo.append("smiles");
        solventInfo.append(",waterLogLC50FM");
        solventInfo.append(",waterLogLC50DM");
        solventInfo.append(",ratLogLD50");
        solventInfo.append(",waterLogWS");
        solventInfo.append(",waterLogBFC");
        solventInfo.append(",airUrbanEUAc");
        solventInfo.append(",airUrbanEUAnc");
        solventInfo.append(",airRuralERAc");
        solventInfo.append(",airRuralERAnc");
        System.out.println(solventInfo.toString());

        File file = new File("validation/fitness/constant-50k-compounds.csv");
        for (int i = 0; i < 50000; i++) {
            Molecule molecule = MoleculeSpace.randomMolecule();
            EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
            solventInfo.append("\n" + molecule.getSmiles());
            solventInfo.append("," + environmentalProperties.getWaterLogLC50FM());
            solventInfo.append("," + environmentalProperties.getWaterLogLC50DM());
            solventInfo.append("," + environmentalProperties.getRatLogLD50());
            solventInfo.append("," + environmentalProperties.getWaterLogWS());
            solventInfo.append("," + environmentalProperties.getWaterLogBFC());
            solventInfo.append("," + environmentalProperties.getAirUrbanEUAc());
            solventInfo.append("," + environmentalProperties.getAirUrbanEUAnc());
            solventInfo.append("," + environmentalProperties.getAirRuralERAc());
            solventInfo.append("," + environmentalProperties.getAirRuralERAnc());
        }

        FileUtils.write(file, solventInfo);
    }
}
