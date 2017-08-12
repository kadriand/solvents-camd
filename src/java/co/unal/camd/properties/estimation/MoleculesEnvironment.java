package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionParametersManager;
import unalcol.evolution.Environment;
import unalcol.evolution.Fitness;
import unalcol.evolution.Genotype;
import unalcol.evolution.Phenotype;

public class MoleculesEnvironment extends Environment<Molecules, Molecules> {

    /**
     * Creates a Enviroment with the given genotype, phenotype  and fitness
     *
     * @param genotype  The genotype of the Enviroment
     * @param phenotype The phenotype of the Enviroment
     * @param fitness   The fitness of the Enviroment
     */
    public MoleculesEnvironment(Genotype<Molecules> genotype,
                                Phenotype<Molecules, Molecules> phenotype,
                                Fitness<Molecules> fitness, ContributionParametersManager _aGC) {
        super(genotype, phenotype, fitness);
        aGC = _aGC;
    }

    public ContributionParametersManager aGC;
}
