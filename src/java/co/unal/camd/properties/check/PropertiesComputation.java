package co.unal.camd.properties.check;

import co.unal.camd.ga.haea.MoleculesFactory;
import co.unal.camd.properties.estimation.BoilingTemp;
import co.unal.camd.properties.estimation.Density;
import co.unal.camd.properties.estimation.DielectricConstant;
import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.GibbsEnergy;
import co.unal.camd.properties.estimation.MeltingTemp;
import co.unal.camd.properties.estimation.Molecule;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;

public class PropertiesComputation {

    public static void main(String[] args) throws IOException {
        String fileName = "molecules.json";
        ClassLoader classLoader = PropertiesComputation.class.getClassLoader();
        String moleculesPath = URLDecoder.decode(classLoader.getResource(fileName).getFile(), "UTF-8");
        File moleculesFile = new File(moleculesPath);
        System.out.println("File Found : " + moleculesFile.getAbsolutePath());
        //        String content = new String(Files.readAllBytes(moleculesFile.toPath()));
        ObjectMapper mapper = new ObjectMapper();
        // Convert JSON string from file to Object
        MoleculeData[] moleculeData = mapper.readValue(moleculesFile, MoleculeData[].class);

        int[] acetoneGroups = moleculeData[13].getGroups();
        System.out.println(Arrays.toString(acetoneGroups));


        ArrayList<FunctionalGroupNode> functionalGroupNodes = new ArrayList<>();

        for (int group : moleculeData[13].getGroups())
            functionalGroupNodes.add(new FunctionalGroupNode(group));

        MoleculesFactory moleculesFactory = new MoleculesFactory(8);
        Molecule solvent = moleculesFactory.buildMolecule(functionalGroupNodes);
        double temperature = 297.15;

        ArrayList<Integer> secOrderCodes = solvent.get2OrderGroupArray();
        GibbsEnergy GE = new GibbsEnergy(solvent, secOrderCodes);
        BoilingTemp BT = new BoilingTemp(solvent, secOrderCodes);
        Density D = new Density(solvent, temperature);
        MeltingTemp MT = new MeltingTemp(solvent, secOrderCodes);
        DielectricConstant DC = new DielectricConstant(solvent, secOrderCodes, temperature);

        double ge = GE.getMethodResult();
        double bt = BT.getMethodResult();
        double d = D.getMethodResult();
        double mt = MT.getMethodResult();
        double dc = DC.getDielectricConstant();

        System.out.println(ge);
        System.out.println(bt);
        System.out.println(d);
        System.out.println(mt);
        System.out.println(dc);
    }

}
