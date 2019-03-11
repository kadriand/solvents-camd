package co.unal.camd.view;

import co.unal.camd.availability.AvailabilityFinder;
import co.unal.camd.availability.MongodbClient;
import co.unal.camd.ga.haea.MoleculeEvolution;
import co.unal.camd.properties.ProblemParameters;
import co.unal.camd.properties.groups.EstimationParameters;
import co.unal.camd.properties.model.EnvironmentalProperties;
import co.unal.camd.properties.model.MixtureProperties;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.properties.model.ThermoPhysicalProperties;
import com.co.evolution.model.Population;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Kevin Adrián Rodríguez Ruiz
 */
@Data
public class CamdRunner extends JFrame {

    static {
        System.setProperty(SimpleLogger.SHOW_SHORT_LOG_NAME_KEY, "true");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CamdRunner.class);

    private static final long serialVersionUID = 1L;
    public static final AvailabilityFinder AVAILABILITY_FINDER = MongodbClient.IS_DB_ENABLE ? new AvailabilityFinder() : null;
    public static final EstimationParameters CONTRIBUTION_GROUPS = new EstimationParameters();

    @Setter(AccessLevel.NONE)
    protected JTabbedPane candidateSolventsTabs;
    protected ArrayList<MoleculeGroups> userMolecules;

    /**
     * Despliega un JFileChooser y retorna la ruta absoluta del archivo
     * seleccionado
     *
     * @return Ruta absoluta del archivo seleccionado
     */
    public String selectFile() {
        JFileChooser fileChooser = new JFileChooser("./data/Molecules");
        fileChooser.setDialogTitle("Seleccione una molécula");
        int result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION)
            return null;

        String selected = null;
        try {
            selected = fileChooser.getSelectedFile().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return selected;
    }

    void designSuitableMolecules() {
        candidateSolventsTabs.removeAll();
        LOGGER.info("ITERATIONS: {}", ProblemParameters.getMaxIterations());
        LOGGER.info("pesos (gibbsEnergy, boilingPoint, density, meltingPoint, solventLoss) {}", ProblemParameters.PROPERTIES_WEIGHTS);

        MoleculeEvolution moleculeEvolution = new MoleculeEvolution(this);
        LOGGER.info("Parents pool size {}", ProblemParameters.getParentsPoolSize());
        LOGGER.info("Maximum of iterations {}", ProblemParameters.getMaxIterations());

        // EVOLUTION TIME
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
                .sorted(Comparator.comparingDouble(molecule -> molecule.getFitness()))
                .collect(Collectors.toList());

        for (Molecule candidate : sortedSolutions) {
            ThermoPhysicalProperties thermoPhysicalProperties = candidate.getThermoPhysicalProperties();
            EnvironmentalProperties environmentalProperties = candidate.getEnvironmentalProperties();
            int occurrences = groupedMolecules.get(candidate);
            MixtureProperties mixtureProperties = candidate.getMixtureProperties();
            Double fitness = candidate.getFitness();

            StringBuilder solventInfo = new StringBuilder("/////////////////////////// " + candidate.getSmiles() + " [" + occurrences + "] /////////////////////////////////////");
            solventInfo.append("\nMW: " + thermoPhysicalProperties.getMolecularWeight());
            solventInfo.append("\nGe: " + thermoPhysicalProperties.getGibbsEnergy());
            solventInfo.append("\nBT: " + thermoPhysicalProperties.getBoilingPoint());
            solventInfo.append("\nDen: " + thermoPhysicalProperties.getDensity());
            solventInfo.append("\nMT: " + thermoPhysicalProperties.getMeltingPoint());
            solventInfo.append("\nDC: " + thermoPhysicalProperties.getDielectricConstant());
            solventInfo.append("\nKS: " + mixtureProperties.getKs());
            solventInfo.append("\nSMILES: " + candidate.getSmiles());
            solventInfo.append("\nGROUPS: " + candidate.toString());

            solventInfo.append("\n///////////////////////// ENVIRONMENT ///////////////////////////////////");
            solventInfo.append("\nwaterLC50FM : " + environmentalProperties.getWaterLC50FM());
            solventInfo.append("\nwaterLC50DM : " + environmentalProperties.getWaterLC50DM());
            solventInfo.append("\nratLD50 : " + environmentalProperties.getRatLD50());
            solventInfo.append("\nwaterLogWS : " + environmentalProperties.getWaterLogWS());
            solventInfo.append("\nwaterBFC : " + environmentalProperties.getWaterBFC());
            solventInfo.append("\nairEUAc : " + environmentalProperties.getAirEUAc());
            solventInfo.append("\nairEUAnc : " + environmentalProperties.getAirEUAnc());
            solventInfo.append("\nairERAc : " + environmentalProperties.getAirERAc());
            solventInfo.append("\nairERAnc : " + environmentalProperties.getAirERAnc());
            solventInfo.append("\n//////////////////////////////////////////////////////////////\n");

            LOGGER.info("Solvent fitness {}\n{}", fitness, solventInfo);

            candidateSolventsTabs.addTab(String.format("%d. %s [%d]", sortedSolutions.indexOf(candidate) + 1, candidate.getSmiles(), occurrences), null, new CandidateSolventPanel(candidate), candidate.getSmiles());
        }
    }

}