package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.properties.estimation.Molecule;
import unalcol.search.space.Space;

public class MoleculeSpace extends Space<Molecule> {

    private int maxGroups;
    private MoleculesFactory moleculesFactory;

    public MoleculeSpace(int maxGroups, ContributionParametersManager parametersManager) {
        this.maxGroups = maxGroups;
        moleculesFactory = new MoleculesFactory(maxGroups, parametersManager);
    }

    @Override
    public boolean feasible(Molecule molecule) {
        return molecule.getTotalGroups()<=maxGroups;
    }

    @Override
    public double feasibility(Molecule x) {
        return feasible(x) ? 1 : 0;
    }

    @Override
    public Molecule repair(Molecule molecule) {
//        TODO repair
        return molecule;
    }

    @Override
    public Molecule pick() {
        return moleculesFactory.buildNewMolecule();
    }
}