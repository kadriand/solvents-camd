package co.unal.camd.descriptors;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.EnvironmentalProperties;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.view.CamdRunner;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

public class PropertiesComputation {

    public static void main(String[] args) throws IOException {
        //        evaluateSingleMolecule();
        //        evaluateSingleMolecule4Subs();
        evaluateJsonMoleculeSet();
        //        evaluateSingleMoleculeNewNaming();
    }

    private static void evaluateJsonMoleculeSet() throws IOException {
        MoleculeData[] moleculesData = parseMoleculeData();

        for (MoleculeData moleculeData : moleculesData) {

            if (moleculeData.getGroups().length == 0 && moleculeData.getConfiguration().trim().isEmpty())
                continue;

            ContributionGroupNode rootFunctionalGroupNode;
            if (!moleculeData.getConfiguration().trim().isEmpty())
                rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(moleculeData.getConfiguration());
            else {
                rootFunctionalGroupNode = new ContributionGroupNode(moleculeData.getGroups()[0]);
                ContributionGroupNode currentFunctionalNode = rootFunctionalGroupNode;
                for (int i = 1; i < moleculeData.getGroups().length; i++) {
                    ContributionGroupNode functionalGroupNode = new ContributionGroupNode(moleculeData.getGroups()[i]);
                    currentFunctionalNode.getSubGroups().add(functionalGroupNode);
                    currentFunctionalNode = functionalGroupNode;
                }
            }

            Molecule molecule = new Molecule(rootFunctionalGroupNode);
            moleculeData.buildRecomputed(molecule);
            System.out.println(moleculeData);
            System.out.println(molecule);
            System.out.println(moleculeData.getRecomputed().compared(moleculeData.getComputed()));

            EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
            molecule.getSmiles();
            System.out.println("DONE");
        }
    }

    private static void evaluateSingleMolecule() {
        // Sample: *Metil isobutil cetona
        ContributionGroupNode rootFunctionalGroupNode = new ContributionGroupNode(18);
        ContributionGroupNode functionalGroupNode1 = new ContributionGroupNode(2);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroupNode1);
        ContributionGroupNode functionalGroupNode2 = new ContributionGroupNode(3);
        functionalGroupNode1.getSubGroups().add(functionalGroupNode2);
        ContributionGroupNode functionalGroupNode3 = new ContributionGroupNode(1);
        functionalGroupNode2.getSubGroups().add(functionalGroupNode3);
        ContributionGroupNode functionalGroupNode4 = new ContributionGroupNode(1);
        functionalGroupNode2.getSubGroups().add(functionalGroupNode4);

        Molecule molecule = new Molecule(rootFunctionalGroupNode);
        MoleculeData moleculeData = new MoleculeData().setComputed(new MoleculeData.PropertiesSet());
        moleculeData.setName("Metil isobutil cetona");

        moleculeData.buildRecomputed(molecule);
        System.out.println(moleculeData);
        System.out.println(molecule);
        System.out.println(moleculeData.getRecomputed().compared(moleculeData.getComputed()));

        EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
        molecule.getSmiles();
        System.out.println("DONE");
    }

    private static void evaluateSingleMoleculeNewNaming() {
        // Sample: *Metil isobutil cetona'
        ContributionGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration("1.3(1).2.18");
        Molecule molecule = new Molecule(rootFunctionalGroupNode);
        MoleculeData moleculeData = new MoleculeData().setComputed(new MoleculeData.PropertiesSet());
        moleculeData.setName("Metil isobutil cetona");

        moleculeData.buildRecomputed(molecule);
        System.out.println(moleculeData);
        System.out.println(molecule);
        System.out.println(moleculeData.getRecomputed().compared(moleculeData.getComputed()));

        EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
        molecule.getSmiles();
        System.out.println("DONE");
    }

    private static void evaluateSingleMolecule4Subs() {
        // Sample: *Metil isobutil cetona
        ContributionGroupNode rootFunctionalGroupNode = new ContributionGroupNode(4);
        ContributionGroupNode functionalGroup1 = new ContributionGroupNode(82);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroup1);
        ContributionGroupNode functionalGroup2 = new ContributionGroupNode(49);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroup2);
        ContributionGroupNode functionalGroup3 = new ContributionGroupNode(2);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroup3);
        ContributionGroupNode functionalGroup4 = new ContributionGroupNode(1);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroup4);

        ContributionGroupNode functionalGroup21 = new ContributionGroupNode(1);
        functionalGroup2.getSubGroups().add(functionalGroup21);
        ContributionGroupNode functionalGroup31 = new ContributionGroupNode(1);
        functionalGroup3.getSubGroups().add(functionalGroup31);

        Molecule molecule = new Molecule(rootFunctionalGroupNode);
        MoleculeData moleculeData = new MoleculeData().setComputed(new MoleculeData.PropertiesSet());
        moleculeData.setName("That thing");
        moleculeData.buildRecomputed(molecule);
        System.out.println(moleculeData);
        System.out.println(molecule);
        System.out.println(moleculeData.getRecomputed().compared(moleculeData.getComputed()));
    }

    private static MoleculeData[] parseMoleculeData() throws IOException {
        String fileName = "molecules.json";
        ClassLoader classLoader = PropertiesComputation.class.getClassLoader();
        String moleculesPath = URLDecoder.decode(classLoader.getResource(fileName).getFile(), "UTF-8");
        File moleculesFile = new File(moleculesPath);
        System.out.println("\nFile Found : " + moleculesFile.getCanonicalPath());
        /*String content = new String(Files.readAllBytes(moleculesFile.toPath()));*/
        ObjectMapper mapper = new ObjectMapper();
        // Convert JSON string from file to Object
        return mapper.readValue(moleculesFile, MoleculeData[].class);
    }

}
