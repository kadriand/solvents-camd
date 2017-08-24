package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.properties.estimation.GroupArray;
import co.unal.camd.properties.estimation.Molecule;
import co.unal.camd.view.CamdRunner;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.evolution.EAFactory;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.HaeaStep;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperatorsDescriptor;
import unalcol.evolution.haea.WriteHaeaStep;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.population.Population;
import unalcol.search.population.PopulationDescriptors;
import unalcol.search.population.PopulationSearch;
import unalcol.search.selection.Elitism;
import unalcol.search.solution.Solution;
import unalcol.search.space.Space;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.Tracer;
import unalcol.types.real.array.DoubleArrayPlainWrite;

public class MoleculeEvolution {

    private ContributionParametersManager parametersManager;
    private HaeaOperators operators;
    private Goal<Molecule, Double> goal;
    private Space<Molecule> space;

    private OptimizationFunction<Molecule> moleculeFitness;

    public MoleculeEvolution(CamdRunner camdRunner) {
        this.parametersManager = camdRunner.getParametersManager();
        this.space = new MoleculeSpace(camdRunner.getMaxGroups(), parametersManager);

        // TODO: Set the fitness parameters: double temperature, Molecules solute, Molecules solventUser, GenotypeChemistry parametersManager
        GroupArray solute = camdRunner.getMoleculesUser().get(0);
        GroupArray solvent = camdRunner.getMoleculesUser().get(1);

        // Optimization Function

        moleculeFitness = new MoleculeFitness(camdRunner.getTemperature(), solute, solvent, camdRunner.getWeight(), camdRunner.getConstraintsLimits(), parametersManager);
        goal = new OptimizationGoal<>(moleculeFitness, false); // maximizing, remove the parameter false if minimizing

        buildOperators();
    }

    public void buildOperators() {
        MoleculeMutation mutation = new MoleculeMutation(parametersManager);
        Cross xover = new Cross(parametersManager);
        CutAndClose cutAndClose = new CutAndClose(parametersManager);
        CutAndReplace cutAndReplace = new CutAndReplace(parametersManager);
        ChangeByCH2 changeByCH2 = new ChangeByCH2(parametersManager);

        HaeaOperators<Molecule> operators = new SimpleHaeaOperators<>(mutation
                , xover
                , cutAndClose
                , cutAndReplace
                , changeByCH2
        );

        this.operators = operators;
    }

    public Population evolve(int parentSize, int maxIterations) {

        Elitism<Molecule> elitism = new Elitism(1.0, 0.0);

        // Search method
        int POPSIZE = parentSize;
        int MAXITERS = maxIterations;
        EAFactory<Molecule> factory = new EAFactory<>();
        PopulationSearch<Molecule, Double> search = factory.HAEA(POPSIZE, operators, elitism, MAXITERS);

        // Tracking the goal evaluations
        WriteDescriptors write_desc = new WriteDescriptors();
        Write.set(double[].class, new DoubleArrayPlainWrite(false));
        Write.set(HaeaStep.class, new WriteHaeaStep<Molecule>());
        Descriptors.set(Population.class, new PopulationDescriptors<Molecule>());
        Descriptors.set(HaeaOperators.class, new SimpleHaeaOperatorsDescriptor<Molecule>());
        Write.set(HaeaOperators.class, write_desc);

        ConsoleTracer tracer = new ConsoleTracer();
        //      Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm

        // Apply the search method
//        Population<Molecule> solutionPopulation = search.init(space, goal);
        Solution<Molecule> solution = search.solve(space, goal);

        System.out.println("SIZE");
//        System.out.println(solutionPopulation.size());

        System.out.println("FITNESS");
        Double fitness = solution.object().getObjectiveFuntion();
//        Double fitness = moleculeFitness.apply(solutionPopulation.get(0).object());
        System.out.println(fitness);
        return null;
    }


    public OptimizationFunction<Molecule> getMoleculeFitness() {
        return moleculeFitness;
    }
}
