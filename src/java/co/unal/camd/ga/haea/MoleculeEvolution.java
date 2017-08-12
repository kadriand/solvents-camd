package co.unal.camd.ga.haea;

import co.unal.camd.properties.estimation.ChangeByCH2;
import co.unal.camd.properties.estimation.CutAndReplace;
import co.unal.camd.properties.estimation.GroupArray;
import co.unal.camd.properties.estimation.MoleculesEnvironment;
import co.unal.camd.properties.estimation.cutAndClose;
import co.unal.camd.view.CamdRunner;
import unalcol.core.Tracer;
import unalcol.evolution.EvolutionaryAlgorithm;
import unalcol.evolution.Fitness;
import unalcol.evolution.Genotype;
import unalcol.evolution.Operator;
import unalcol.evolution.Phenotype;
import unalcol.evolution.Population;
import unalcol.evolution.algorithms.haea.HAEA;
import unalcol.evolution.algorithms.haea.HaeaOperators;
import unalcol.evolution.algorithms.haea.SimpleHaeaOperators;
import unalcol.evolution.selections.Elitism;
import unalcol.evolution.selections.Tournament;
import unalcol.util.ForLoopCondition;

public class MoleculeEvolution {

    private HAEA algorithm;
    private MoleculesEnvironment environment;

    public MoleculeEvolution(CamdRunner camdRunner) {
        // TODO: Set the fitness parameters: double temperature, Molecules solute, Molecules solventUser, GenotypeChemistry parametersManager
        GroupArray solute = camdRunner.getMoleculesUser().get(0);
        GroupArray solvent = camdRunner.getMoleculesUser().get(1);

        Fitness fitness = new MoleculeFitness(camdRunner.getTemperature(), solute, solvent, camdRunner.getWeight(), camdRunner.getConstraintsLimits(), camdRunner.getParametersManager());

        // TODO: Set the genotype parameters: int _maxNmGroups, GenotypeChemistry _aGC
        Genotype genotype = new MoleculeGenotype(camdRunner.getMaxGroups(), camdRunner.getParametersManager());
        //Genotype g = new VariableLengthBinaryGenotype(10,100,10);
        Phenotype phenotype = new Phenotype();

        environment = new MoleculesEnvironment(genotype, phenotype, fitness, camdRunner.getParametersManager());
        HaeaOperators operators = buildOperators();
        Elitism selection = new Elitism(environment, 1, false, 1.0, 0.0);

        algorithm = new HAEA(operators, selection);
    }

    public HaeaOperators buildOperators() {
        Operator[] opers;
        MoleculeMutation mutation = new MoleculeMutation(environment);
        Cross xover = new Cross(environment, new Tournament(environment, 2, true, 4));
        cutAndClose cAndC = new cutAndClose(environment);
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

}
