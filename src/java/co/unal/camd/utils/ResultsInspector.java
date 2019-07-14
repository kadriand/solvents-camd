package co.unal.camd.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class ResultsInspector {

    private static Pattern PATTERN_RUN_NUMBER = Pattern.compile("(\\d+)-.*");
    private static Pattern PATTERN_COMPOUND = Pattern.compile("^[^\\t]+\\t[^\\t]+\\t[^\\t]+\\t([^\\t]+)");

    public static void main(String[] args) throws Exception {
        //        makeCandidatesPerformanceTtable();
        makeEvolutionPerformanceTtable();
    }

    private static void makeCandidatesPerformanceTtable() throws Exception {
        String dirPath = "output/100-camd-candidates-150";
        File dir = new File(dirPath);
        File[] files = dir.listFiles((dir1, name) -> name.matches("\\d+-run-final.*.tsv"));

        File paretoOptimalFile = new File("output/results/100-candidates-pareto-optimal-150.tsv");
        paretoOptimalFile.getParentFile().mkdirs();
        FileUtils.writeStringToFile(paretoOptimalFile, "run\tindex\tconfiguration\toccurrences\tsmiles\tinChIKey\tmolecularWeight\t" +
                "meltingPoint\tboilingPoint\tdensity\tgibbsEnergy\t" +
                "waterLogLC50FM\twaterLogLC50DM\tratLogLD50\twaterLogWS\twaterLogBFC\t" +
                "solventLoss\t" +
                "KS\tenvironmentalIndex\tpenalties\tmarketAvailability\t\tavailability...", StandardCharsets.UTF_8);

        for (File file : files) {
            //            System.out.println(file.getName());
            List<String> fileLines = Files.readAllLines(file.toPath());
            String runNumber = readRunNumber(file);
            String optimalCompounds = readOptimalCompounds(runNumber, dirPath);
            for (String line : fileLines) {
                String compound = readCompoundName(line);
                if (optimalCompounds.contains("_" + compound + "_"))
                    FileUtils.writeStringToFile(paretoOptimalFile, "\n" + runNumber + "\t" + line, StandardCharsets.UTF_8, true);
            }
        }
    }

    private static void makeEvolutionPerformanceTtable() throws Exception {
        File dir = new File("output/100-camd-evolution-feasible-150");
        writeOperatorRatesFile(dir);
        writeEnvironmentFitnessFile(dir);
        writeSolventPowerFitnessFile(dir);
    }

    private static void writeOperatorRatesFile(File dir) throws IOException {
        File operatorsRatesFile = new File("output/results/100-evolution-operator-rates-feasible-150.tsv");

        File[] files = dir.listFiles((dir1, name) -> name.matches("\\d+-run-rates.tsv"));
        operatorsRatesFile.getParentFile().mkdirs();
        FileUtils.writeStringToFile(operatorsRatesFile, "run\tGeneration\tMoleculeMutation\tCross\tCutAndClose\tCutAndReplace\tCH2Appending\tGroupRemoval", StandardCharsets.UTF_8);
        for (File file : files) {
            System.out.println(file.getName());
            String runNumber = readRunNumber(file);
            List<String> fileLines = Files.readAllLines(file.toPath());
            for (String line : fileLines)
                FileUtils.writeStringToFile(operatorsRatesFile, "\n" + runNumber + "\t" + line, StandardCharsets.UTF_8, true);
        }
    }

    private static void writeEnvironmentFitnessFile(File dir) throws IOException {
        File[] files = dir.listFiles((dir1, name) -> name.matches("\\d+-run-of-Environment.*.tsv"));
        File operatorsRatesFile = new File("output/results/100-evolution-environmental-fitness-150.tsv");
        operatorsRatesFile.getParentFile().mkdirs();
        for (File file : files) {
            System.out.println(file.getName());
            String runNumber = readRunNumber(file);
            List<String> fileLines = Files.readAllLines(file.toPath());
            for (String line : fileLines)
                FileUtils.writeStringToFile(operatorsRatesFile, "\n" + runNumber + "\t" + line, StandardCharsets.UTF_8, true);
        }
    }

    private static void writeSolventPowerFitnessFile(File dir) throws IOException {
        File[] files = dir.listFiles((dir1, name) -> name.matches("\\d+-run-of-SolventPower.*.tsv"));
        File operatorsRatesFile = new File("output/results/100-evolution-solventpower-fitness-150.tsv");
        operatorsRatesFile.getParentFile().mkdirs();
        for (File file : files) {
            System.out.println(file.getName());
            String runNumber = readRunNumber(file);
            List<String> fileLines = Files.readAllLines(file.toPath());
            for (String line : fileLines)
                FileUtils.writeStringToFile(operatorsRatesFile, "\n" + runNumber + "\t" + line, StandardCharsets.UTF_8, true);
        }
    }

    private static String readOptimalCompounds(String runNumber, String dirPath) throws IOException {
        File optimalCompoundsFile = new File(dirPath + "/" + runNumber + "-run-generation-0.tsv");
        String optimalCompounds = FileUtils.readFileToString(optimalCompoundsFile);
        optimalCompounds = optimalCompounds.replaceAll("\t[^\n]*\n", "_").replaceAll("\t.*", "_").replaceAll("^[^_]+_", "_");
        //            System.out.println(optimalCompounds);
        return optimalCompounds;
    }

    private static String readRunNumber(File file) {
        Matcher runMatcher = PATTERN_RUN_NUMBER.matcher(file.getName());
        runMatcher.matches();
        return runMatcher.group(1);
    }

    private static String readCompoundName(String line) {
        Matcher compoundMatcher = PATTERN_COMPOUND.matcher(line);
        compoundMatcher.find();
        return compoundMatcher.group(1);
    }


}
