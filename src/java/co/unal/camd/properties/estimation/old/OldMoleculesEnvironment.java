package co.unal.camd.properties.estimation.old;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.properties.estimation.Molecule;
import unalcol.agents.simulate.Environment;

public class OldMoleculesEnvironment extends Environment<Molecule, Molecule> {

    /**
     * Creates a Enviroment with the given genotype, phenotype  and fitness
     *
     * @param genotype  The genotype of the Enviroment
     * @param phenotype The phenotype of the Enviroment
     * @param fitness   The fitness of the Enviroment
     */
    public OldMoleculesEnvironment(Genotype<Molecule> genotype,
                                   Phenotype<Molecule, Molecule> phenotype,
                                   Fitness<Molecule> fitness, ContributionParametersManager _aGC) {
        super(genotype, phenotype, fitness);
        aGC = _aGC;
    }

    public ContributionParametersManager aGC;
}
