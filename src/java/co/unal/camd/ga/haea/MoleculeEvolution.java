package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.ga.haea.old.OldCross;
import co.unal.camd.ga.haea.old.OldMoleculeGenotype;
import co.unal.camd.ga.haea.old.OldMoleculeMutation;
import co.unal.camd.properties.estimation.ChangeByCH2;
import co.unal.camd.properties.estimation.CutAndReplace;
import co.unal.camd.properties.estimation.GroupArray;
import co.unal.camd.properties.estimation.Molecule;
import co.unal.camd.properties.estimation.old.OldMoleculesEnvironment;
import co.unal.camd.properties.estimation.CutAndClose;
import co.unal.camd.view.CamdRunner;
import unalcol.algorithm.iterative.ForLoopCondition;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.population.Population;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Tournament;
import unalcol.tracer.Tracer;

public class MoleculeEvolution {

    private ContributionParametersManager parametersManager;
    private HAEA algorithm;
    private OldMoleculesEnvironment environment;

    public MoleculeEvolution(CamdRunner camdRunner) {
        // TODO: Set the fitness parameters: double temperature, Molecules solute, Molecules solventUser, GenotypeChemistry parametersManager
        GroupArray solute = camdRunner.getMoleculesUser().get(0);
        GroupArray solvent = camdRunner.getMoleculesUser().get(1);

        // Optimization Function
        this.parametersManager = camdRunner.getParametersManager();

        OptimizationFunction<Molecule> moleculeFitness = new MoleculeFitness(camdRunner.getTemperature(), solute, solvent, camdRunner.getWeight(), camdRunner.getConstraintsLimits(), parametersManager);
        Goal<Molecule, Double> goal = new OptimizationGoal<>(moleculeFitness, false); // maximizing, remove the parameter false if minimizing

        HaeaOperators operators = buildOperators();


        Fitness fitness = new MoleculeFitness(camdRunner.getTemperature(), solute, solvent, camdRunner.getWeight(), camdRunner.getConstraintsLimits(), parametersManager);

        // TODO: Set the genotype parameters: int _maxNmGroups, GenotypeChemistry _aGC
        Genotype genotype = new OldMoleculeGenotype(camdRunner.getMaxGroups(), parametersManager);
        //Genotype g = new VariableLengthBinaryGenotype(10,100,10);
        Phenotype phenotype = new Phenotype();

        Elitism selection = new Elitism(environment, 1, false, 1.0, 0.0);

        algorithm = new HAEA(operators, selection);
    }

    public HaeaOperators buildOperators() {
        Operator[] opers;

        MoleculeMutation mutation = new MoleculeMutation(parametersManager);
        OldCross xover = new OldCross(environment, new Tournament(environment, 2, true, 4));
        CutAndClose cAndC = new CutAndClose(environment);
        CutAndReplace cAndR = new CutAndReplace(environment);
        ChangeByCH2 changeBy = new ChangeByCH2(environment);


        //@TODO Add more molecule operators opers[1] = xover;
        //opers[0] = transpose;
        HaeaOperators<Molecule> operators = new SimpleHaeaOperators<Molecule>(mutation
                , xover
//                ,cAndC
//                ,cAndR
//                ,changeBy
        );
        return operators;


//        opers = new Operator[5];
//        opers[0] = mutation;
//        opers[1] = xover;
//        opers[2] = cAndC;
//        opers[3] = cAndR;
//        opers[4] = changeBy;
//
//        return new SimpleHaeaOperators(opers);
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

}
