package co.unal.camd.ga.haea.old;

import co.unal.camd.properties.estimation.ChangeByCH2;
import co.unal.camd.properties.estimation.CutAndClose;
import co.unal.camd.properties.estimation.CutAndReplace;
import co.unal.camd.properties.estimation.GroupArray;
import co.unal.camd.properties.estimation.old.OldMoleculesEnvironment;
import co.unal.camd.view.CamdRunner;
import evolution.Glovito;
import evolution.GlovitoFitness;
import unalcol.algorithm.iterative.ForLoopCondition;
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
import unalcol.optimization.binary.BinarySpace;
import unalcol.optimization.binary.BitMutation;
import unalcol.optimization.binary.Transposition;
import unalcol.optimization.binary.XOver;
import unalcol.search.Goal;
import unalcol.search.population.Population;
import unalcol.search.population.PopulationDescriptors;
import unalcol.search.population.PopulationSearch;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Tournament;
import unalcol.search.solution.Solution;
import unalcol.search.space.Space;
import unalcol.search.variation.Variation_1_1;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.Tracer;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.real.array.DoubleArrayPlainWrite;

public class OldMoleculeEvolution {

    private HAEA algorithm;
    private OldMoleculesEnvironment environment;

    public OldMoleculeEvolution(CamdRunner camdRunner) {
        // TODO: Set the fitness parameters: double temperature, Molecules solute, Molecules solventUser, GenotypeChemistry parametersManager
        GroupArray solute = camdRunner.getMoleculesUser().get(0);
        GroupArray solvent = camdRunner.getMoleculesUser().get(1);

        Fitness fitness = new OldMoleculeFitness(camdRunner.getTemperature(), solute, solvent, camdRunner.getWeight(), camdRunner.getConstraintsLimits(), camdRunner.getParametersManager());

        // TODO: Set the genotype parameters: int _maxNmGroups, GenotypeChemistry _aGC
        Genotype genotype = new OldMoleculeGenotype(camdRunner.getMaxGroups(), camdRunner.getParametersManager());
        //Genotype g = new VariableLengthBinaryGenotype(10,100,10);
        Phenotype phenotype = new Phenotype();

        environment = new OldMoleculesEnvironment(genotype, phenotype, fitness, camdRunner.getParametersManager());
        HaeaOperators operators = buildOperators();
        Elitism selection = new Elitism(environment, 1, false, 1.0, 0.0);

        algorithm = new HAEA(operators, selection);
    }

    public HaeaOperators buildOperators() {
        Operator[] opers;
        OldMoleculeMutation mutation = new OldMoleculeMutation(environment);
        OldCross xover = new OldCross(environment, new Tournament(environment, 2, true, 4));
        CutAndClose cAndC = new CutAndClose(environment);
        CutAndReplace cAndR = new CutAndReplace(environment);
        ChangeByCH2 changeBy = new ChangeByCH2(environment);
        opers = new Operator[5];
        opers[0] = mutation;
        opers[1] = xover;
        opers[2] = cAndC;
        opers[3] = cAndR;
        opers[4] = changeBy;
        //@TODO Add more molecule operators opers[1] = xover;
        //opers[0] = transpose;
        return new SimpleHaeaOperators(opers);
    }

    public Population evolve(int popSize, int maxIter, Tracer tracer) {
        Population p = null;
        Population population = new Population(environment, popSize);


        EvolutionaryAlgorithm ea = new EvolutionaryAlgorithm(population, algorithm, new ForLoopCondition(maxIter));
        ea.addTracer(tracer);
        ea.init();

        /**
         p = ea.getPopulation();
         p.sort();
         //double nnn=p.get(0).getFitness();
         //System.out.println("Numero"+nnn);
         Vector<Individual> popu = new Vector<Individual>();
         for(int i=0;i<POP_SIZE;i++){
         popu.add(p.get(i));
         }
         p = new Population(env,popu);
         ea = new EvolutionaryAlgorithm( p,
         getTransformation( operators, selection ),
         getCondition(MAX_ITER) );
         ea.addTracer( tracer );
         ea.init();
         */
        ea.run();
        p = ea.getPopulation();
        p.sort();
        return p;
    }

    public static void binary() {
        // Search Space definition
        int DIM = 24;
        Space<BitArray> space = new BinarySpace(DIM);

        // Optimization Function
        OptimizationFunction<BitArray> function = new GlovitoFitness();
        Goal<BitArray, Double> goal = new OptimizationGoal<BitArray>(function, false); // maximizing, remove the parameter false if minimizing

        // Variation definition
        Variation_1_1<BitArray> mutation = new BitMutation();
        Variation_1_1<BitArray> transposition = new Transposition();
        XOver xover = new XOver();
        HaeaOperators<BitArray> operators = new SimpleHaeaOperators<BitArray>(mutation, transposition, xover);

        // Search method
        int POPSIZE = 100;
        int MAXITERS = 10;
        EAFactory<BitArray> factory = new EAFactory<BitArray>();
        PopulationSearch<BitArray, Double> search =
                factory.HAEA(POPSIZE, operators, new Tournament<BitArray>(4), MAXITERS);

        // Tracking the goal evaluations
        WriteDescriptors write_desc = new WriteDescriptors();
        Write.set(double[].class, new DoubleArrayPlainWrite(false));
        Write.set(HaeaStep.class, new WriteHaeaStep<BitArray>());
        Descriptors.set(Population.class, new PopulationDescriptors<BitArray>());
        Descriptors.set(HaeaOperators.class, new SimpleHaeaOperatorsDescriptor<BitArray>());
        Write.set(HaeaOperators.class, write_desc);

        ConsoleTracer tracer = new ConsoleTracer();
//      Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm

        // Apply the search method
        Solution<BitArray> solution = search.solve(space, goal);

        System.out.println(solution.info(Goal.class.getName()));
        // Remove for general use
        Glovito g = new Glovito(solution.object());
        System.out.println(g.toString());
    }

}
