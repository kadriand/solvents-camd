package co.unal.camd.ga.haea;

import co.unal.camd.ga.haea.operators.CH2Appending;
import co.unal.camd.ga.haea.operators.Cross;
import co.unal.camd.ga.haea.operators.CutAndClose;
import co.unal.camd.ga.haea.operators.CutAndReplace;
import co.unal.camd.ga.haea.operators.GroupRemoval;
import co.unal.camd.ga.haea.operators.MoleculeMutation;
import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import com.co.evolution.algorithm.MOHAEA;
import com.co.evolution.fitness.CrowdingDistanceFitnessCalculation;
import com.co.evolution.interceptor.ParetoPlotterImageInterceptor;
import com.co.evolution.model.FitnessCalculation;
import com.co.evolution.model.GeneticOperator;
import com.co.evolution.model.Population;
import com.co.evolution.model.PopulationInitialization;
import com.co.evolution.model.SelectionMethod;
import com.co.evolution.selection.TournamentSelection;
import com.co.evolution.termination.MaxIterationsTerminationCondition;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

public class MoleculeEvolution {

    private List<GeneticOperator<Molecule>> operators;
    private SolventPowerFitness solventPowerFitness;
    private EnvironmentFitness environmentFitness;
    private MoleculePenalization penalization;
    @Setter
    private String filesPrefix = "camd/";

    public MoleculeEvolution(CamdRunner camdRunner) {
        Molecule targetSolute = camdRunner.getSolute();
        Molecule problemSolvent = camdRunner.getSolvent();

        // Optimization Function
        this.solventPowerFitness = new SolventPowerFitness(targetSolute, problemSolvent);
        this.environmentFitness = new EnvironmentFitness();
        this.penalization = new MoleculePenalization(problemSolvent);
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
        MaxIterationsTerminationCondition<Molecule> terminationCondition = new MaxIterationsTerminationCondition<>(ProblemParameters.getMaxIterations());
        SelectionMethod<Molecule> selectionMethod = new TournamentSelection<>(4);
        PopulationInitialization<Molecule> initialization = new MoleculeSpace();

        FitnessCalculation<Molecule> fitnessCalculation = new CrowdingDistanceFitnessCalculation<>(this.penalization, this.solventPowerFitness, this.environmentFitness);
                ParetoPlotterImageInterceptor<Molecule> evolutionInterceptor = new ParetoPlotterImageInterceptor<>(5, filesPrefix, this.solventPowerFitness, this.environmentFitness);
                evolutionInterceptor.setTextExtension("tsv");
                evolutionInterceptor.setFieldSeparator("\t");
//        OperatorsRatesInterceptor<Molecule> evolutionInterceptor = new OperatorsRatesInterceptor<>(filesPrefix, operators, Arrays.asList(this.solventPowerFitness, this.environmentFitness), "tsv", "\t");

        //   FitnessCalculation<Molecule> fitnessCalculation = new StrengthParetoFitnessCalculation<Molecule>(objectiveFunctions);

        MOHAEA<Molecule> ga = new MOHAEA<>(operators, terminationCondition, selectionMethod, initialization, fitnessCalculation);
        ga.setEvolutionInterceptor(evolutionInterceptor);

        Population<Molecule> finalPop = ga.apply();

        Molecule bestSolution = finalPop.getBest();
        System.out.println("BEST : " + bestSolution.toString() + " Fitness: " + bestSolution.getFitness() + " Value: " + Arrays.toString(bestSolution.getObjectiveValues()));
        return finalPop;
    }

}
