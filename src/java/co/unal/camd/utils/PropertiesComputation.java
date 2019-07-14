package co.unal.camd.utils;

import co.unal.camd.availability.MongodbClient;
import co.unal.camd.ga.haea.EnvironmentFitness;
import co.unal.camd.ga.haea.SolventPowerFitness;
import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.unifac.MixtureProperties;
import co.unal.camd.model.EnvironmentalProperties;
import co.unal.camd.model.ThermoPhysicalProperties;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.view.CamdRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.openscience.cdk.exception.CDKException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("Duplicates")
public class PropertiesComputation {

    public static void main(String[] args) throws IOException {
        //        evaluateSingleMolecule();
        //        evaluateSingleMolecule4Subs();
        //        evaluateSingleMoleculeNewNaming("1.35(2.2.1).2.2.1");
        //                evaluateThermoPhysicalJsonMoleculeSet();
        //        evaluateEnvironmentalWaterJsonMoleculeSet();
        //        evaluateEnvironmentalAirJsonMoleculeSet();
        //        generateIdentifiersJsonMoleculeSet();
        //                singleMoleculeForUnifac();
        //                generateIdentifiersJsonUnifacMoleculeSet();
        //                evaluateUnifacMoleculeSet();
        //        previousProgramResults();
        evaluateTernaryMixture();
        //        evaluateBinaryMixturePaper();

    }

    private static void previousProgramResults() {
        MongodbClient.IS_DB_ENABLE = true;
        String[] molecules = new String[]{
                "1.4(20)(20).20",
                "1.4(20)(20).2(20)",
                "20.2.20",
                "42.4(20)(1).2.2.20",
                "21.4(20)(20).1",
                "1.4(20)(20).2.21",
                "20.2.4(20)(1).2.2.4(1)(1).20",
                "1.4(20)(21).2.20",
                "20.2.2.2.4(20)(1).22.2.2.1",
                "1.4(20)(1).2.4(20)(1).2.21",
                "20.2.4(1)(21).2.2.4(1)(1).20",
                "20.3(1).20",
                "42.2.4(1)(20).21",
                "24.4(20)(1).2.2.20",
                "20.4(1)(20).42",
                "42.4(21)(1).2.2.20",
                "20.2.2.2.4(1)(21).25.2.2.1",
                "1.2.2.2.4(1)(21).2.25.2.20",
                "20.2.3(1).20",
                "20.2.4(1)(20).2.2.4(1)(1).24",
                "20.2.2.2.4(21)(1).22.2.2.1",
                "21.100.2.1",
                "21.2.4(1)(20).2.2.4(1)(1).24",
                "5.2.2.4(24)(20).3(1).2.20",
                "20.3(24).20",
                "20.4(1)(1).2.4(20)(1).2.20",
                "21.4(1)(1).2.2.22.20",
                "21.2.2.2.4(21)(1).25.2.2.1",
                "42.22.2.24(1)(1).20",
                "42.2.4(20)(1).2.2.4(1)(1).24",
                "1.2.2.22.4(1)(21).2.2.2.21",
                "20.22.2.25.4(1)(1).2.2.2.1",
                "1.100.2.21",
                "1.2.4(1)(20).2.22.4(1)(21).1",
                "24.2.25.2.4(1)(21).2.2.2.1",
                "20.2.24",
                "20.4(1)(1).20",
                "14.2.4(1)(20).2.2.4(1)(1).24",
                "20.4(1)(1).2.4(1)(20).2.42",
                "20.4(1)(20).82",
                "20.4(1)(20).24",
                "14.2.4(1)(20).2.2.4(1)(1).20",
                "42.2.2.100.4(1)(21).1",
                "24.4(1)(20).24",
                "1.100.2.2.2.21",
                "82.2.2.2.4(1)(21).25.2.2.1",
                "42.2.4(1)(21).2.4(1)(1).20",
                "82.4(1)(20).20",
                "1.2.2.100.21",
                "20.4(1)(1).2.4(1)(21).2.82",
                "1.2.4(1)(42).2.25.4(1)(1).20",
                "1.2.4(1)(21).2.100.4(1)(1).21",


        };
        String filesPrefix = "camd/prev-";
        File propertiesFile = initPropertiesFile(filesPrefix);
        Molecule waterMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration("16"));
        Molecule lacticAcidMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration("1.3(81).42"));
        EnvironmentFitness environmentFitness = new EnvironmentFitness();
        SolventPowerFitness solventPowerFitness = new SolventPowerFitness(lacticAcidMolecule, waterMolecule);

        for (String molecule : molecules) {
            // Sample: *Metil isobutil cetona' "1.3(1).2.18"
            UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(molecule);
            Molecule candidate = new Molecule(rootFunctionalGroupNode);
            double[] objectiveValues = new double[]{solventPowerFitness.compute(candidate), environmentFitness.compute(candidate)};
            candidate.setObjectiveValues(objectiveValues);
            candidate.getMixtureProperties().getInfiniteDilution(waterMolecule);
            appendCompoundProperties(filesPrefix, ArrayUtils.indexOf(molecules, molecule), propertiesFile, candidate, 0);
        }
    }

    static File initPropertiesFile(String filesPrefix) {
        File propertiesFile = new File("output/" + filesPrefix + "final-props.tsv");
        try {
            FileUtils.writeStringToFile(propertiesFile, "index\tconfiguration\toccurrences\tsmiles\tinChIKey\tmolecularWeight\t" +
                    "meltingPoint\tboilingPoint\tdensity\tgibbsEnergy\t" +
                    "waterLogLC50FM\twaterLogLC50DM\tratLogLD50\twaterLogWS\twaterLogBFC\t" +
                    "solventLoss\t" +
                    "KS\tenvironmentalIndex\tpenalties\tmarketAvailability\t\tavailability...", StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Problems creating properties file");
        }
        return propertiesFile;
    }

    static void appendCompoundProperties(String filesPrefix, int rank, File propertiesFile, Molecule candidate, int occurrences) {
        try {
            MoleculeThermoPhysicalValidator thermoPhysical = new MoleculeThermoPhysicalValidator();
            thermoPhysical.setMolecule(candidate).buildRecomputed().buildIdentifiers();
            MoleculeEnvironmentalValidator environmental = new MoleculeEnvironmentalValidator();
            environmental.setMolecule(candidate).buildRecomputed();
            StringBuilder candidateProperties = new StringBuilder(String.format("\n%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t",
                    rank,
                    candidate.getThermoPhysicalProperties().getGroupsSummary(),
                    occurrences,
                    thermoPhysical.getCamdEstimation().completeTsv(),
                    environmental.getCamdEstimation().waterAsTsv(),
                    candidate.getMixtureProperties().getLastBinary().getSolventLoss(),
                    candidate.getObjectiveValues()[0],
                    candidate.getObjectiveValues()[1],
                    candidate.getPenalization(),
                    candidate.getAvailabilityEntries().size()
            ));
            candidate.getAvailabilityEntries().forEach(compoundEntry -> candidateProperties.append(String.format("\t%s\t%s", compoundEntry.getSource().name(), compoundEntry.itemUrl())));
            FileUtils.writeStringToFile(propertiesFile, candidateProperties.toString(), StandardCharsets.UTF_8, true);

            BufferedImage moleculeImage = CdkUtils.moleculeImage(candidate.getSmiles(), rank + ". " + candidate.getSmiles());
            File imageFile = new File("output/" + filesPrefix + "molec-" + rank + ".png");
            imageFile.getParentFile().mkdirs(); // correct!
            if (!imageFile.exists())
                imageFile.createNewFile();
            ImageIO.write(moleculeImage, "png", imageFile);
        } catch (IOException | CDKException e) {
            System.out.println("Problems creating image file");
        }
    }

    private static void evaluateTernaryMixture() {
        Molecule waterMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration("16"));
        Molecule toSeparateMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration("1.42"));

        EnvironmentFitness environmentFitness = new EnvironmentFitness();
        SolventPowerFitness solventPowerFitness = new SolventPowerFitness(toSeparateMolecule, waterMolecule);

        String candidateConf = "1.2.2.77.82";
        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(candidateConf);
        Molecule candidate = new Molecule(rootFunctionalGroupNode);
        double[] objectiveValues = new double[]{solventPowerFitness.compute(candidate), environmentFitness.compute(candidate)};
        candidate.setObjectiveValues(objectiveValues);
        candidate.getMixtureProperties().getInfiniteDilution(waterMolecule);
    }

    private static void evaluateBinaryMixturePaper() {
        ProblemParameters.setTemperature(ProblemParameters.DEFAULT_TEMPERATURE + 10);
        Molecule solventMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(
                "14.1"
        ));
        Molecule soluteMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(
                "18.1"
        ));

        MixtureProperties.BinaryProperties infiniteDilution = soluteMolecule.getMixtureProperties().getInfiniteDilution(solventMolecule);
        System.out.println("Iinf dilution : " + infiniteDilution.getActivityCoefficient());
    }

    private static void evaluateThermoPhysicalJsonMoleculeSet() throws IOException {
        MoleculeThermoPhysicalValidator[] moleculesData = parseThermoPhysicalMoleculeData();

        File comparisonFile = new File("validation/thermo-phyisical/comparison.csv");
        FileUtils.writeStringToFile(comparisonFile, "index,name,CAS,configuration,molecularWeight,computed molecularWeight," +
                "meltingPoint,boilingPoint,density,gibbsEnergy, " +
                "meltingPoint,boilingPoint,density,gibbsEnergy", StandardCharsets.UTF_8);

        for (MoleculeThermoPhysicalValidator moleculeData : moleculesData) {
            if (moleculeData.getConfiguration().trim().isEmpty())
                continue;

            UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(moleculeData.getConfiguration());
            Molecule molecule = new Molecule(rootFunctionalGroupNode);
            moleculeData.setMolecule(molecule);
            moleculeData.buildRecomputed();

            FileUtils.writeStringToFile(comparisonFile, "\n" + moleculeData.asCsv(), StandardCharsets.UTF_8, true);
            ThermoPhysicalProperties thermoPhysicalProperties = molecule.getThermoPhysicalProperties();

            System.out.println("\n" + moleculeData);
            System.out.println(String.format("%s (MW = %s)", molecule, molecule.getMolecularWeight()));
            System.out.println(thermoPhysicalProperties);
        }
    }

    private static void evaluateEnvironmentalWaterJsonMoleculeSet() throws IOException {
        MoleculeEnvironmentalValidator[] moleculesData = parseEnvironmentalMoleculeData("water");

        File comparisonFile = new File("validation/environmental/comparison-water.csv");
        FileUtils.writeStringToFile(comparisonFile, "index, name, CAS, configuration, molecularWeight," +
                "waterLogLC50FM,waterLogLC50DM,ratLogLD50,waterLogWS,waterLogBFC," +
                "waterLogLC50FM,waterLogLC50DM,ratLogLD50,waterLogWS,waterLogBFC," +
                "waterLogLC50FM,waterLogLC50DM,ratLogLD50,waterLogWS,waterLogBFC", StandardCharsets.UTF_8);

        for (MoleculeEnvironmentalValidator moleculeData : moleculesData) {
            if (moleculeData.getConfiguration().trim().isEmpty())
                continue;

            UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(moleculeData.getConfiguration());
            Molecule molecule = new Molecule(rootFunctionalGroupNode);
            moleculeData.setMolecule(molecule);
            moleculeData.buildRecomputed();
            System.out.println("\n" + moleculeData);
            System.out.println(String.format("%s (MW = %s)", molecule, molecule.getMolecularWeight()));
            FileUtils.writeStringToFile(comparisonFile, "\n" + moleculeData.waterAsCsv(), StandardCharsets.UTF_8, true);
            EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
            System.out.println(environmentalProperties);
        }
    }

    private static void evaluateEnvironmentalAirJsonMoleculeSet() throws IOException {
        MoleculeEnvironmentalValidator[] moleculesData = parseEnvironmentalMoleculeData("air");

        File comparisonFile = new File("validation/environmental/comparison-air.csv");
        FileUtils.writeStringToFile(comparisonFile, "index, name, CAS, configuration, molecularWeight," +
                "airUrbanEUAc,airUrbanEUAnc,airRuralERAc,airRuralERAnc," +
                "airUrbanEUAc,airUrbanEUAnc,airRuralERAc,airRuralERAnc", StandardCharsets.UTF_8);

        for (MoleculeEnvironmentalValidator moleculeData : moleculesData) {
            if (moleculeData.getConfiguration().trim().isEmpty())
                continue;

            UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(moleculeData.getConfiguration());
            Molecule molecule = new Molecule(rootFunctionalGroupNode);
            moleculeData.setMolecule(molecule);
            moleculeData.buildRecomputed();
            System.out.println("\n" + moleculeData);
            System.out.println(String.format("%s (MW = %s)", molecule, molecule.getMolecularWeight()));
            FileUtils.writeStringToFile(comparisonFile, "\n" + moleculeData.airAsCsv(), StandardCharsets.UTF_8, true);
            EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
            System.out.println(environmentalProperties);
        }
    }

    private static void generateIdentifiersJsonMoleculeSet() throws IOException {
        MoleculeThermoPhysicalValidator[] moleculesData = parseThermoPhysicalMoleculeData();

        File comparisonFile = new File("validation/identifiers.csv");
        FileUtils.writeStringToFile(comparisonFile, "index, name, configuration, molecularWeight, Smiles, InChIKey", StandardCharsets.UTF_8);
        for (MoleculeThermoPhysicalValidator moleculeData : moleculesData) {
            if (moleculeData.getConfiguration().trim().isEmpty())
                continue;

            UnifacGroupNode rootFunctionalGroupNode;
            rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(moleculeData.getConfiguration());
            Molecule molecule = new Molecule(rootFunctionalGroupNode);
            moleculeData.setMolecule(molecule);
            moleculeData.buildIdentifiers();
            System.out.println("\n" + moleculeData);
            System.out.println(molecule);
            FileUtils.writeStringToFile(comparisonFile, "\n" + moleculeData.identifiersCsv(), StandardCharsets.UTF_8, true);
        }
    }

    private static void evaluateUnifacMoleculeSet() throws IOException {
        MoleculeUnifacValidator[] moleculesData = parseUnifacMoleculeData();

        File comparisonFile = new File("validation/unifac/comparison-check.csv");
        FileUtils.writeStringToFile(comparisonFile,
                "index,name,CAS,configuration,molecularWeight,computedMolecularWeight,lnComputedActivity,lnExperimentalActivity,lnRecomputedActivity", StandardCharsets.UTF_8);

        Molecule waterMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration("16"));
        MoleculeUnifacValidator.setWaterMolecule(waterMolecule);

        for (MoleculeUnifacValidator moleculeData : moleculesData) {
            if (moleculeData.getConfiguration().trim().isEmpty())
                continue;

            UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(moleculeData.getConfiguration());
            Molecule molecule = new Molecule(rootFunctionalGroupNode);
            moleculeData.setMolecule(molecule);
            moleculeData.recompute();

            FileUtils.writeStringToFile(comparisonFile, "\n" + moleculeData.asCsv(), StandardCharsets.UTF_8, true);
            System.out.println("\n" + moleculeData);
            System.out.println(String.format("%s (MW = %s)", molecule, molecule.getMolecularWeight()));
        }
    }

    private static void generateIdentifiersJsonUnifacMoleculeSet() throws IOException {
        MoleculeUnifacValidator[] moleculesData = parseUnifacMoleculeData();

        File comparisonFile = new File("validation/identifiers-unifac.csv");
        FileUtils.writeStringToFile(comparisonFile, "index, name, configuration, molecularWeight, Smiles, InChIKey", StandardCharsets.UTF_8);
        for (MoleculeUnifacValidator moleculeData : moleculesData) {
            if (moleculeData.getConfiguration().trim().isEmpty())
                continue;

            UnifacGroupNode rootFunctionalGroupNode;
            rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(moleculeData.getConfiguration());
            Molecule molecule = new Molecule(rootFunctionalGroupNode);
            moleculeData.setMolecule(molecule);
            moleculeData.buildIdentifiers();
            System.out.println("\n" + moleculeData);
            System.out.println(molecule);
            FileUtils.writeStringToFile(comparisonFile, "\n" + moleculeData.identifiersCsv(), StandardCharsets.UTF_8, true);
        }
    }

    public static void singleMoleculeForUnifac() {
        CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
        String originalUnifacMolecule = "74.75.75.75.75.75.74";
        originalUnifacMolecule = "1.2.14";
        //                originalUnifacMolecule = "1.42";
        //        originalUnifacMolecule = "1.3(81).42";

        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(originalUnifacMolecule);
        Molecule soluteMolecule = new Molecule(rootFunctionalGroupNode);
        soluteMolecule.setComposition(ProblemParameters.DILUTION_FRACTION);
        Molecule waterMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration("16"));
        waterMolecule.setComposition(1 - ProblemParameters.DILUTION_FRACTION);
        Molecule aceticAcidMolecule = new Molecule(CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration("1.42"));

        System.out.println("\nUnifac new");
        MixtureProperties.BinaryProperties binary = soluteMolecule.getMixtureProperties().getInfiniteDilution(waterMolecule);
        System.out.println(binary.getActivityCoefficient());
        System.out.println("Loss new");
        System.out.println(binary.getSolventLoss());
        MixtureProperties.TernaryProperties ternary = soluteMolecule.getMixtureProperties().getTernary(aceticAcidMolecule.getMixtureProperties().getInfiniteDilution(waterMolecule));
        System.out.println("KS new");
        System.out.println(ternary.getKs());

    }

    private static void evaluateSingleMolecule() {
        // Sample: *Metil isobutil cetona
        UnifacGroupNode rootFunctionalGroupNode = new UnifacGroupNode(18);
        UnifacGroupNode functionalGroupNode1 = new UnifacGroupNode(2);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroupNode1);
        UnifacGroupNode functionalGroupNode2 = new UnifacGroupNode(3);
        functionalGroupNode1.getSubGroups().add(functionalGroupNode2);
        UnifacGroupNode functionalGroupNode3 = new UnifacGroupNode(1);
        functionalGroupNode2.getSubGroups().add(functionalGroupNode3);
        UnifacGroupNode functionalGroupNode4 = new UnifacGroupNode(1);
        functionalGroupNode2.getSubGroups().add(functionalGroupNode4);

        Molecule molecule = new Molecule(rootFunctionalGroupNode);
        MoleculeThermoPhysicalValidator moleculeData = new MoleculeThermoPhysicalValidator().setExperimental(new MoleculeThermoPhysicalValidator.ThermoPhysicalData());
        moleculeData.setName("Metil isobutil cetona");

        moleculeData.setMolecule(molecule);
        System.out.println(moleculeData);
        System.out.println(molecule);
        System.out.println(moleculeData.getExperimental());
        System.out.println(moleculeData.getCamdEstimation());

        EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
        molecule.getSmiles();
    }

    private static void evaluateSingleMoleculeNewNaming(String config) {
        // Sample: *Metil isobutil cetona' "1.3(1).2.18"
        UnifacGroupNode rootFunctionalGroupNode = CamdRunner.CONTRIBUTION_GROUPS.parseGroupsConfiguration(config);
        Molecule molecule = new Molecule(rootFunctionalGroupNode);
        MoleculeThermoPhysicalValidator moleculeData = new MoleculeThermoPhysicalValidator().setExperimental(new MoleculeThermoPhysicalValidator.ThermoPhysicalData());
        moleculeData.setName("Metil isobutil cetona");

        moleculeData.setMolecule(molecule);
        System.out.println(moleculeData);
        System.out.println(molecule);
        System.out.println(moleculeData.getExperimental());
        System.out.println(moleculeData.getCamdEstimation());

        EnvironmentalProperties environmentalProperties = molecule.getEnvironmentalProperties();
        molecule.getSmiles();
    }

    private static void evaluateSingleMolecule4Subs() {
        // Sample: *Metil isobutil cetona
        UnifacGroupNode rootFunctionalGroupNode = new UnifacGroupNode(4);
        UnifacGroupNode functionalGroup1 = new UnifacGroupNode(82);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroup1);
        UnifacGroupNode functionalGroup2 = new UnifacGroupNode(49);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroup2);
        UnifacGroupNode functionalGroup3 = new UnifacGroupNode(2);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroup3);
        UnifacGroupNode functionalGroup4 = new UnifacGroupNode(1);
        rootFunctionalGroupNode.getSubGroups().add(functionalGroup4);

        UnifacGroupNode functionalGroup21 = new UnifacGroupNode(1);
        functionalGroup2.getSubGroups().add(functionalGroup21);
        UnifacGroupNode functionalGroup31 = new UnifacGroupNode(1);
        functionalGroup3.getSubGroups().add(functionalGroup31);

        Molecule molecule = new Molecule(rootFunctionalGroupNode);
        MoleculeThermoPhysicalValidator moleculeData = new MoleculeThermoPhysicalValidator().setExperimental(new MoleculeThermoPhysicalValidator.ThermoPhysicalData());
        moleculeData.setName("That thing");
        moleculeData.setMolecule(molecule);
        System.out.println(moleculeData);
        System.out.println(molecule);
        System.out.println(moleculeData.getExperimental());
        System.out.println(moleculeData.getCamdEstimation());
    }

    private static MoleculeThermoPhysicalValidator[] parseThermoPhysicalMoleculeData() throws IOException {
        String fileName = "validation/molecules-thermo.json";
        ClassLoader classLoader = PropertiesComputation.class.getClassLoader();
        String moleculesPath = URLDecoder.decode(classLoader.getResource(fileName).getFile(), "UTF-8");
        File moleculesFile = new File(moleculesPath);
        System.out.println("\nFile Found : " + moleculesFile.getCanonicalPath());
        /*String content = new String(Files.readAllBytes(moleculesFile.toPath()));*/
        ObjectMapper mapper = new ObjectMapper();
        // Convert JSON string from file to Object
        return mapper.readValue(moleculesFile, MoleculeThermoPhysicalValidator[].class);
    }

    private static MoleculeEnvironmentalValidator[] parseEnvironmentalMoleculeData(String type) throws IOException {
        String fileName = "validation/molecules-environmental-" + type + ".json";
        ClassLoader classLoader = PropertiesComputation.class.getClassLoader();
        String moleculesPath = URLDecoder.decode(classLoader.getResource(fileName).getFile(), "UTF-8");
        File moleculesFile = new File(moleculesPath);
        System.out.println("\nFile Found : " + moleculesFile.getCanonicalPath());
        /*String content = new String(Files.readAllBytes(moleculesFile.toPath()));*/
        ObjectMapper mapper = new ObjectMapper();
        // Convert JSON string from file to Object
        return mapper.readValue(moleculesFile, MoleculeEnvironmentalValidator[].class);
    }

    private static MoleculeUnifacValidator[] parseUnifacMoleculeData() throws IOException {
        String fileName = "validation/unifac.json";
        ClassLoader classLoader = PropertiesComputation.class.getClassLoader();
        String moleculesPath = URLDecoder.decode(classLoader.getResource(fileName).getFile(), "UTF-8");
        File moleculesFile = new File(moleculesPath);
        System.out.println("\nFile Found : " + moleculesFile.getCanonicalPath());
        /*String content = new String(Files.readAllBytes(moleculesFile.toPath()));*/
        ObjectMapper mapper = new ObjectMapper();
        // Convert JSON string from file to Object
        return mapper.readValue(moleculesFile, MoleculeUnifacValidator[].class);
    }

}
