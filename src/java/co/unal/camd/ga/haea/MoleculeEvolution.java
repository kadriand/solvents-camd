package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.view.CamdRunner;
import lombok.Getter;
import unalcol.Tagged;
import unalcol.clone.DefaultClone;
import unalcol.descriptors.WriteDescriptors;
import unalcol.evolution.EAFactory;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.HaeaStep;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperatorsDescriptor;
import unalcol.evolution.haea.WriteHaeaStep;
import unalcol.optimization.OptimizationFunction;
import unalcol.random.raw.JavaGenerator;
import unalcol.search.Search;
import unalcol.search.population.PopulationDescriptors;
import unalcol.search.population.PopulationSearch;
import unalcol.search.selection.Uniform;
import unalcol.search.solution.SolutionDescriptors;
import unalcol.search.solution.SolutionWrite;
import unalcol.search.space.Space;
import unalcol.services.Service;
import unalcol.services.ServicePool;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.Tracer;
import unalcol.types.real.array.DoubleArrayPlainWrite;

public class MoleculeEvolution {

    private HaeaOperators operators;
    private Space<Molecule> space;
    private OptimizationFunction<Molecule> moleculeFitness;

    @Getter
    private Tagged<Molecule> bestSolution;

    public MoleculeEvolution(CamdRunner camdRunner) {
        this.space = new MoleculeSpace(camdRunner.getMaxGroups());
        MoleculeGroups solute = camdRunner.getUserMolecules().get(0);
        MoleculeGroups solvent = camdRunner.getUserMolecules().get(1);

        // Optimization Function
        moleculeFitness = new MoleculeFitness(camdRunner.getTemperature(), solute, solvent, camdRunner.getWeight(), camdRunner.getConstraintsLimits());
        moleculeFitness.minimize(false);
        buildOperators();
    }

    public void buildOperators() {
        MoleculeMutation mutation = new MoleculeMutation();
        Cross xover = new Cross();
        CutAndClose cutAndClose = new CutAndClose();
        CutAndReplace cutAndReplace = new CutAndReplace();
        ChangeByCH2 changeByCH2 = new ChangeByCH2();

        HaeaOperators<Molecule> operators = new SimpleHaeaOperators<>(
                mutation,
                xover,
                cutAndClose,
                cutAndReplace,
                changeByCH2
        );

        this.operators = operators;
    }

    public Tagged<Molecule>[] evolve(int parentSize, int maxIterations) {

        //        Elitism<Molecule> elitism = new Elitism(1.0, 0.0);
        Uniform<Molecule> elitism = new Uniform<>();
        // Search method
        int POPSIZE = parentSize;
        int MAXITERS = maxIterations;
        EAFactory<Molecule> factory = new EAFactory<>();
        PopulationSearch<Molecule, Double> search = factory.HAEA(POPSIZE, operators, elitism, MAXITERS);
        search.setGoal(moleculeFitness);

        // Tracking the goal evaluations
        real_service(moleculeFitness, search);
        population_service(moleculeFitness);
        haea_service();

        // Apply the search method
        Tagged<Molecule>[] solutionPopulation = search.init(space);
        solutionPopulation = search.apply(solutionPopulation, space);

        bestSolution = search.pick(solutionPopulation);

        return solutionPopulation;
    }

    private void haea_service() {
        ServicePool service = (ServicePool) Service.get();
        service.register(new WriteHaeaStep(), HaeaStep.class);
        service.register(new SimpleHaeaOperatorsDescriptor<Molecule>(), HaeaOperators.class);
    }

    public static void population_service(OptimizationFunction function) {
        ServicePool service = (ServicePool) Service.get();
        PopulationDescriptors pd = new PopulationDescriptors();
        pd.setGoal(function);
        service.register(pd, Tagged[].class);
        service.register(new WriteDescriptors<Tagged[]>(), Tagged[].class);
    }

    public static void real_service(OptimizationFunction<Molecule> function, Search<Molecule, Double> search) {
        // Tracking the goal evaluations
        ServicePool service = new ServicePool();
        service.register(new JavaGenerator(), Object.class);
        service.register(new DefaultClone(), Object.class);
        Tracer<Object> t = new ConsoleTracer<Object>();
        t.start();
        service.register(t, search);
        service.register(new SolutionDescriptors<Molecule>(function), Tagged.class);
        service.register(new DoubleArrayPlainWrite(',', false), double[].class);
        service.register(new SolutionWrite<Molecule>(function, true), Tagged.class);
        Service.set(service);
    }

}
