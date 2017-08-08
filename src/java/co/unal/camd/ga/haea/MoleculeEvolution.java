package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.properties.estimation.ChangeByCH2;
import co.unal.camd.properties.estimation.CutAndReplace;
import co.unal.camd.properties.estimation.GroupArray;
import co.unal.camd.properties.estimation.MoleculesEnviroment;
import co.unal.camd.properties.estimation.cutAndClose;
import unalcol.core.Tracer;
import unalcol.evolution.Environment;
import unalcol.evolution.EvolutionaryAlgorithm;
import unalcol.evolution.Fitness;
import unalcol.evolution.Genotype;
import unalcol.evolution.Operator;
import unalcol.evolution.Phenotype;
import unalcol.evolution.Population;
import unalcol.evolution.Selection;
import unalcol.evolution.Transformation;
import unalcol.evolution.algorithms.haea.HAEA;
import unalcol.evolution.algorithms.haea.HaeaOperators;
import unalcol.evolution.algorithms.haea.SimpleHaeaOperators;
import unalcol.evolution.selections.Tournament;
import unalcol.util.ForLoopCondition;

public class MoleculeEvolution {
    public static Environment buildEnvironment(double temperature, GroupArray solute, GroupArray solventUser, double[] weight, double[][] limits, ContributionParametersManager aGC, int maxNmGroups) {
        //@TODO: Set the fitness parameters: double temperature, Molecules solute, Molecules solventUser, GenotypeChemistry parametersManager
        Fitness f = new MoleculeFitness(temperature, solute, solventUser, weight, limits, aGC);
        //@TODO: Set the genotype parameters: int _maxNmGroups, GenotypeChemistry _aGC
        Genotype g = new MoleculeGenotype(maxNmGroups, aGC);

        //Genotype g = new VariableLengthBinaryGenotype(10,100,10);
        Phenotype p = new Phenotype();
        return new MoleculesEnviroment(g, p, f, aGC);
    }

    public static HaeaOperators buildOperators(Environment env) {
        Operator[] opers;
        MoleculeMutation mutation = new MoleculeMutation(env);
        Cross xover = new Cross(env, new Tournament(env, 2, true, 4));
        cutAndClose cAndC = new cutAndClose(env);
        CutAndReplace cAndR = new CutAndReplace(env);
        ChangeByCH2 changeBy = new ChangeByCH2(env);
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

    public static Transformation getTransformation(HaeaOperators operators,
                                                   Selection selection) {
        return new HAEA(operators, selection);
    }

    public static Population evolve(int POP_SIZE, Environment env,
                                    int MAX_ITER, HaeaOperators operators,
                                    Selection selection,
                                    Tracer tracer) {
        Population p = null;
        EvolutionaryAlgorithm ea = new EvolutionaryAlgorithm(new Population(env, POP_SIZE), getTransformation(operators, selection), new ForLoopCondition(MAX_ITER));

        ea.addTracer(tracer);
        ea.init();
//	    if( gui != null ) {
//	      ea.start();
//	    }else{
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
//	    }
        return p;
    }

    public static void main(String[] argv) {
//	    Environment env = buildEnvironment();
//	    HaeaOperators opers = buildOperators(env);
        // GenomeLimits limits;
//	    Population p = evolve( 50, env, 100, opers, new ConsoleTracer() );
    }

}
