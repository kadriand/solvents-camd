package co.unal.camd.core;

import co.unal.camd.control.parameters.ContributionParametersManager;
import unalcol.evolution.Environment;
import unalcol.evolution.Fitness;
import unalcol.evolution.Genotype;
import unalcol.evolution.Phenotype;

public class MoleculesEnviroment extends Environment<Molecules, Molecules> {

    /**
     * Creates a Enviroment with the given genotype, phenotype  and fitness
     *
     * @param genotype  The genotype of the Enviroment
     * @param phenotype The phenotype of the Enviroment
     * @param fitness   The fitness of the Enviroment
     */
    public MoleculesEnviroment(Genotype<Molecules> genotype,
                               Phenotype<Molecules, Molecules> phenotype,
                               Fitness<Molecules> fitness, ContributionParametersManager _aGC) {
        super(genotype, phenotype, fitness);
        aGC = _aGC;
    }

    protected ContributionParametersManager aGC;
}
