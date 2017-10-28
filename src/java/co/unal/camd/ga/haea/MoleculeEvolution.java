package co.unal.camd.ga.haea;

import co.unal.camd.ga.haea.operators.CH2Appending;
import co.unal.camd.ga.haea.operators.Cross;
import co.unal.camd.ga.haea.operators.CutAndClose;
import co.unal.camd.ga.haea.operators.CutAndReplace;
import co.unal.camd.ga.haea.operators.GroupRemoval;
import co.unal.camd.ga.haea.operators.MoleculeMutation;
import co.unal.camd.properties.ProblemParameters;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.view.CamdRunner;
import com.co.evolution.algorithm.HAEA;
import com.co.evolution.fitness.NSGA2FitnessCalculation;
import com.co.evolution.interceptor.ParetoPlotterImageInterceptor;
import com.co.evolution.model.EvolutionInterceptor;
import com.co.evolution.model.FitnessCalculation;
import com.co.evolution.model.GeneticOperator;
import com.co.evolution.model.ObjectiveFunction;
import com.co.evolution.model.Population;
import com.co.evolution.model.PopulationInitialization;
import com.co.evolution.model.SelectionMethod;
import com.co.evolution.selection.TournamentSelection;
import com.co.evolution.termination.MaxIterationsTerminationCondition;

import java.util.Arrays;
import java.util.List;

public class MoleculeEvolution {

    private List<GeneticOperator<Molecule>> operators;
    private ObjectiveFunction[] objectiveFunctions;

    public MoleculeEvolution(CamdRunner camdRunner) {
        MoleculeGroups solute = camdRunner.getUserMolecules().get(0);
        MoleculeGroups solvent = camdRunner.getUserMolecules().get(1);

        // Optimization Function
        SolventPowerFitness solventPowerFitness = new SolventPowerFitness(solute, solvent);
        EnvironmentFitness environmentFitness = new EnvironmentFitness();
        objectiveFunctions = new ObjectiveFunction[]{solventPowerFitness, environmentFitness};
        buildOperators();
    }

    private void buildOperators() {
        MoleculeMutation mutation = new MoleculeMutation();
        Cross xover = new Cross();
        CutAndClose cutAndClose = new CutAndClose();
        CutAndReplace cutAndReplace = new CutAndReplace();
        CH2Appending appendingOfCH2 = new CH2Appending();
        GroupRemoval groupRemoval = new GroupRemoval();

        List<GeneticOperator<Molecule>> operators = Arrays.asList(
                mutation,
                xover,
                cutAndClose,
                cutAndReplace,
                appendingOfCH2,
                groupRemoval
        );

        this.operators = operators;
    }

    public Population<Molecule> evolve() {
        MaxIterationsTerminationCondition terminationCondition = new MaxIterationsTerminationCondition(ProblemParameters.getMaxIterations());
        SelectionMethod<Molecule> selectionMethod = new TournamentSelection(4);
        PopulationInitialization<Molecule> initialization = new MoleculeSpace();

        FitnessCalculation<Molecule> fitnessCalculation = new NSGA2FitnessCalculation<Molecule>(objectiveFunctions);
        EvolutionInterceptor<Molecule> evolutionInterceptor = new ParetoPlotterImageInterceptor<>(5, "nsga2/nsga2-", objectiveFunctions);

        //   FitnessCalculation<RealIndividual> fitnessCalculation = new SPEA2FitnessCalculation<RealIndividual>(objectiveFunctions);
        //   EvolutionInterceptor<RealIndividual> evolutionInterceptor = new ParetoPlotterImageInterceptor<>(5, "spea2/spea2-");

        HAEA<Molecule> ga = new HAEA<>(operators, terminationCondition, selectionMethod, initialization, fitnessCalculation);
        ga.setEvolutionInterceptor(evolutionInterceptor);

        Population<Molecule> finalPop = ga.apply();

        Molecule bestSolution = finalPop.getBest();
        System.out.println("BEST : " + bestSolution.toString() + " Fitness: " + bestSolution.getFitness() + " Value: " + Arrays.toString(bestSolution.getObjectiveValues()));
        return finalPop;
    }

}
