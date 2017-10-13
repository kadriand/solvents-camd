package co.unal.camd.properties.check;

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

public class PropertiesComputation {

    public static void main(String[] args) throws IOException {
        evaluateSingleMolecule();
        evaluateJsonMoleculeSet();
    }

    private static void evaluateJsonMoleculeSet() throws IOException {
        MoleculeData[] moleculesData = parseMoleculeData();

        for (MoleculeData moleculeData : moleculesData) {
            if (moleculeData.getGroups().length == 0)
                continue;

            FunctionalGroupNode firstFunctionalGroupNode = new FunctionalGroupNode(moleculeData.getGroups()[0]);
            FunctionalGroupNode lastFunctionalNode = firstFunctionalGroupNode;
            for (int i = 1; i < moleculeData.getGroups().length; i++) {
                FunctionalGroupNode functionalGroupNode = new FunctionalGroupNode(moleculeData.getGroups()[i]);
                lastFunctionalNode.addGroup(functionalGroupNode);
                lastFunctionalNode = functionalGroupNode;
            }

            Molecule molecule = new Molecule(firstFunctionalGroupNode);
            double temperature = 298.15;

            computeProperties(moleculeData, molecule, temperature);
            System.out.println(moleculeData);
            System.out.println(molecule);
            System.out.println(moleculeData.getRecomputed().compared(moleculeData.getComputed()));
        }
    }

    private static void evaluateSingleMolecule() {
        // Sample: *Metil isobutil cetona
        FunctionalGroupNode rootFunctionalGroupNode = new FunctionalGroupNode(18);
        FunctionalGroupNode functionalGroupNode1 = new FunctionalGroupNode(2);
        rootFunctionalGroupNode.addGroup(functionalGroupNode1);
        FunctionalGroupNode functionalGroupNode2 = new FunctionalGroupNode(3);
        functionalGroupNode1.addGroup(functionalGroupNode2);
        FunctionalGroupNode functionalGroupNode3 = new FunctionalGroupNode(1);
        functionalGroupNode2.addGroup(functionalGroupNode3);
        FunctionalGroupNode functionalGroupNode4 = new FunctionalGroupNode(1);
        functionalGroupNode2.addGroup(functionalGroupNode4);

        Molecule molecule = new Molecule(rootFunctionalGroupNode);
        double temperature = 298.15;
        MoleculeData moleculeData = new MoleculeData().setComputed(new MoleculeData.PropertiesSet());
        moleculeData.setName("Metil isobutil cetona");
        computeProperties(moleculeData, molecule, temperature);
        System.out.println(moleculeData);
        System.out.println(molecule);
        System.out.println(moleculeData.getRecomputed().compared(moleculeData.getComputed()));
    }

    private static void computeProperties(MoleculeData moleculeData, Molecule molecule, double temperature) {
        ArrayList<Integer> secOrderCodes = molecule.find2OrderGroupArray();
        GibbsEnergy GE = new GibbsEnergy(molecule, secOrderCodes);
        BoilingTemp BT = new BoilingTemp(molecule, secOrderCodes);
        Density D = new Density(molecule, temperature);
        MeltingTemp MT = new MeltingTemp(molecule, secOrderCodes);
        DielectricConstant DC = new DielectricConstant(molecule, secOrderCodes, temperature);

        moleculeData.getRecomputed()
                .setDeltaGibbs(GE.getMethodResult())
                .setBoilingTemp(BT.getMethodResult())
                .setDensity(D.getMethodResult())
                .setFusionTemp(MT.getMethodResult())
                .setDielectricConst(DC.getDielectricConstant());
    }

    private static MoleculeData[] parseMoleculeData() throws IOException {
        String fileName = "molecules.json";
        ClassLoader classLoader = PropertiesComputation.class.getClassLoader();
        String moleculesPath = URLDecoder.decode(classLoader.getResource(fileName).getFile(), "UTF-8");
        File moleculesFile = new File(moleculesPath);
        System.out.println("File Found : " + moleculesFile.getAbsolutePath());
        /*String content = new String(Files.readAllBytes(moleculesFile.toPath()));*/
        ObjectMapper mapper = new ObjectMapper();
        // Convert JSON string from file to Object
        return mapper.readValue(moleculesFile, MoleculeData[].class);
    }

}
