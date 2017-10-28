package co.unal.camd.view;

import co.unal.camd.availability.AvailabilityFinder;
import co.unal.camd.availability.MongodbClient;
import co.unal.camd.ga.haea.MoleculeEvolution;
import co.unal.camd.methods.EstimationConstants;
import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.model.EnvironmentalProperties;
import co.unal.camd.model.ThermoPhysicalProperties;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.utils.CdkUtils;
import co.unal.camd.utils.MoleculeEnvironmentalValidator;
import co.unal.camd.utils.MoleculeThermoPhysicalValidator;
import com.co.evolution.model.Population;
import com.co.evolution.model.individual.IndividualImpl;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.io.FileUtils;
import org.openscience.cdk.exception.CDKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Kevin Adrián Rodríguez Ruiz
 */
@Data
@Accessors(chain = true)
public class CamdRunner extends JFrame {

    static {
        System.setProperty(SimpleLogger.SHOW_SHORT_LOG_NAME_KEY, "true");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CamdRunner.class);

    private static final long serialVersionUID = 1L;
    public static final AvailabilityFinder AVAILABILITY_FINDER = MongodbClient.IS_DB_ENABLE ? new AvailabilityFinder() : null;
    public static final EstimationConstants CONTRIBUTION_GROUPS = new EstimationConstants();

    @Setter(AccessLevel.NONE)
    protected JTabbedPane candidateSolventsTabs;
    protected Molecule solute;
    protected Molecule solvent;

    /**
     * Despliega un JFileChooser y retorna la ruta absoluta del archivo
     * seleccionado
     *
     * @return Ruta absoluta del archivo seleccionado
     */
    String selectFile(String titleLabel) {
        JFileChooser fileChooser = new JFileChooser("./data/Molecules");
        fileChooser.setDialogTitle(titleLabel);
        int result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION)
            return null;

        String selectedFile = null;
        try {
            selectedFile = fileChooser.getSelectedFile().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return selectedFile;
    }

    void designSuitableMolecules() {
        if (!ProblemParameters.MULTI_RUNS_MODE) {
            designOptimalMolecule("run");
            return;
        }

        int[] range = IntStream.rangeClosed(1, ProblemParameters.DEFAULT_RUNS).toArray();
        for (int run : range)
            designOptimalMolecule(run + "-run");
    }

    private void designOptimalMolecule(String identifier) {
        candidateSolventsTabs.removeAll();
        LOGGER.info("\n");
        LOGGER.info("NEW RUN FOR SOLVENT DESIGN USING CAMD");
        LOGGER.info("RUN ID : {}", identifier);
        LOGGER.info("ITERATIONS: {}", ProblemParameters.getMaxIterations());
        LOGGER.info("Weights (gibbsEnergy, boilingPoint, density, meltingPoint, solventLoss) {}", ProblemParameters.CONSTRAINTS_WEIGHTS);

        MoleculeEvolution moleculeEvolution = new MoleculeEvolution(this);
        LOGGER.info("PARENTS POOL SIZE: {}", ProblemParameters.getParentsPoolSize());
        LOGGER.info("MAXIMUM OF ITERATIONS: {}", ProblemParameters.getMaxIterations());

        // EVOLUTION TIME
        String filesPrefix = "camd/" + identifier + "-";
        moleculeEvolution.setFilesPrefix(filesPrefix);
        Population<Molecule> population = moleculeEvolution.evolve();

        Molecule bestSolution = population.getBest();
        Double bestFitness = bestSolution.getFitness();
        LOGGER.info("BEST FITNESS : {}", bestFitness);

        Map<Molecule, Integer> groupedMolecules = new HashMap<>();
        population.forEach(individual -> {
            Optional<Map.Entry<Molecule, Integer>> filteredMoleculeEntry = groupedMolecules.entrySet().stream()
                    .filter(molecule -> molecule.getKey().getSmiles().equals(individual.getSmiles()))
                    .findFirst();
            if (filteredMoleculeEntry.isPresent())
                filteredMoleculeEntry.get().setValue(filteredMoleculeEntry.get().getValue() + 1);
            else
                groupedMolecules.put(individual, 1);
        });

        List<Molecule> sortedSolutions = groupedMolecules.keySet().stream()
                .sorted(Comparator.comparingDouble(IndividualImpl::getFitness))
                .collect(Collectors.toList());

        int rank = 0;
        File propertiesFile = initPropertiesFile(filesPrefix);

        for (Molecule candidate : sortedSolutions) {
            rank++;
            int occurrences = groupedMolecules.get(candidate);
            printMoleculeData(rank, candidate, occurrences);

            if (!candidate.isSuitable())
                continue;
            MoleculeDetailsPanel moleculePanel = new MoleculeDetailsPanel(candidate);
            candidateSolventsTabs.addTab(String.format("%d. %s [%d]", sortedSolutions.indexOf(candidate) + 1, candidate.getSmiles(), occurrences), null, moleculePanel, candidate.getSmiles());
            appendCompoundProperties(filesPrefix, rank, propertiesFile, candidate, occurrences);
        }
    }

    private File initPropertiesFile(String filesPrefix) {
        File propertiesFile = new File("output/" + filesPrefix + "final-props.tsv");
        try {
            FileUtils.writeStringToFile(propertiesFile, "index\tconfiguration\toccurrences\tsmiles\tinChIKey\tmolecularWeight\t" +
                    "meltingPoint\tboilingPoint\tdensity\tgibbsEnergy\t" +
                    "waterLogLC50FM\twaterLogLC50DM\tratLogLD50\twaterLogWS\twaterLogBFC\t" +
                    "solventLoss\t" +
                    "KS\tenvironmentalIndex\tpenalties\tmarketAvailability\t\tavailability...", StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Problems creating properties file", e);
        }
        return propertiesFile;
    }

    private void appendCompoundProperties(String filesPrefix, int rank, File propertiesFile, Molecule candidate, int occurrences) {
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
            ImageIO.write(moleculeImage, "png", imageFile);
        } catch (IOException | CDKException e) {
            LOGGER.error("Problems creating image file", e);
        }
    }

    private void printMoleculeData(int rank, Molecule candidate, int occurrences) {
        ThermoPhysicalProperties thermoPhysicalProps = candidate.getThermoPhysicalProperties();
        EnvironmentalProperties environmentalProps = candidate.getEnvironmentalProperties();
        Double fitness = candidate.getFitness();

        StringBuilder solventInfo = new StringBuilder(String.format("/////////////////////////// %s. %s [%s] /////////////////////////////////////", rank, candidate.getSmiles(), occurrences));
        solventInfo.append(String.format("\nMW: %s\nGibbs: %s\nBT: %s\nDen: %s\nMT: %s\nSmiles: %s\nGroups: %s\nSLoss: %s\nKS: %s",
                candidate.getMolecularWeight(),
                thermoPhysicalProps.getGibbsEnergy(),
                thermoPhysicalProps.getBoilingPoint(),
                thermoPhysicalProps.getDensity(),
                thermoPhysicalProps.getMeltingPoint(),
                candidate.getSmiles(),
                candidate.toString(),
                candidate.getMixtureProperties().getLastBinary().getSolventLoss(),
                candidate.getMixtureProperties().getLastTernary().getKs()));

        solventInfo.append("\n///////////////////////// ENVIRONMENT ///////////////////////////////////");
        solventInfo
                .append(String.format("\nwaterLogLC50FM : %s\nwaterLogLC50DM : %s\nratLogLD50 : %s\nwaterLogWS : %s\nwaterLogBFC : %s\nairUrbanEUAc : %s\nairUrbanEUAnc : %s\nairRuralERAc : %s\nairRuralERAnc : %s\n//////////////////////////////////////////////////////////////",
                        environmentalProps.getWaterLogLC50FM(),
                        environmentalProps.getWaterLogLC50DM(),
                        environmentalProps.getRatLogLD50(),
                        environmentalProps.getWaterLogWS(),
                        environmentalProps.getWaterLogBFC(),
                        environmentalProps.getAirUrbanEUAc(),
                        environmentalProps.getAirUrbanEUAnc(),
                        environmentalProps.getAirRuralERAc(),
                        environmentalProps.getAirRuralERAnc()
                ));

        solventInfo.append(String.format("\nAvailability : %s\nPenalization : %s\nSUITABLE : %s", candidate.getAvailabilityEntries().size(), candidate.getPenalization(), candidate.isSuitable()));

        LOGGER.info("Solvent fitness {}\n{}", fitness, solventInfo);
    }

}