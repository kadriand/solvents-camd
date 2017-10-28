package co.unal.camd.ga.haea;

import co.unal.camd.methods.unifac.MixtureProperties;
import co.unal.camd.model.molecule.Molecule;
import com.co.evolution.model.ObjectiveFunction;

public class SolventPowerFitness extends ObjectiveFunction<Molecule> {

    private Molecule problemSolvent;
    private Molecule problemSolute;

    public SolventPowerFitness(Molecule solute, Molecule solvent) {
        super(false);
        this.problemSolute = solute;
        this.problemSolvent = solvent;
    }

    /**
     * the method to calculate the objective function,
     * A:soluto
     * beta:User Solvent
     * S:objective Solvent
     */

    @Override
    public double compute(Molecule solvent) {
        MixtureProperties.BinaryProperties infiniteDilution = this.problemSolute.getMixtureProperties().getInfiniteDilution(this.problemSolvent);
        return solvent.getMixtureProperties()
                .getTernary(infiniteDilution)
                .getKs();
    }
}
