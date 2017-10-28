package co.unal.camd.ga.haea;

import co.unal.camd.availability.CompoundEntry;
import co.unal.camd.availability.CompoundSource;
import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.model.molecule.Molecule;
import com.co.evolution.model.Penalization;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MoleculePenalization extends Penalization<Molecule> {

    private Molecule problemSolvent;

    @Override
    protected double compute(Molecule solvent) {
        return availabilityPenalization(solvent) + thermoPhysicalPenalization(solvent);
    }

    private double availabilityPenalization(Molecule molecule) {
        List<CompoundEntry> availabilityEntries = molecule.getAvailabilityEntries();

        boolean marketAvailable = availabilityEntries.stream().anyMatch(
                compoundEntry -> CompoundSource.EPA == compoundEntry.getSource()
                        || CompoundSource.ZINC_WAITOK == compoundEntry.getSource()
                        || CompoundSource.ZINC_BOUTIQUE == compoundEntry.getSource()
                        || CompoundSource.ZINC_ANNOTATED == compoundEntry.getSource());

        return marketAvailable ? 0.0 : ProblemParameters.CONSTRAINTS_WEIGHTS.getMarketAvailability();
    }

    private double thermoPhysicalPenalization(Molecule solvent) {
        double gibbsEnergyContribution = solvent.getThermoPhysicalProperties().getGibbsEnergy() < ProblemParameters.CONSTRAINTS_BOUNDARIES.getGibbsEnergy() ? 0.0 : ProblemParameters.CONSTRAINTS_WEIGHTS.getGibbsEnergy();
        double boilingPointContribution = solvent.getThermoPhysicalProperties().getBoilingPoint() < ProblemParameters.CONSTRAINTS_BOUNDARIES.getBoilingPoint() ? 0.0 : ProblemParameters.CONSTRAINTS_WEIGHTS.getBoilingPoint();
        double meltingPointContribution = solvent.getThermoPhysicalProperties().getMeltingPoint() < ProblemParameters.CONSTRAINTS_BOUNDARIES.getMeltingPoint() ? 0.0 : ProblemParameters.CONSTRAINTS_WEIGHTS.getMeltingPoint();
        double solventLossContribution = solvent.getMixtureProperties().getInfiniteDilution(problemSolvent).getSolventLoss() < ProblemParameters.CONSTRAINTS_BOUNDARIES.getSolventLoss() ? 0.0 : ProblemParameters.CONSTRAINTS_WEIGHTS.getSolventLoss();

        return gibbsEnergyContribution + boilingPointContribution + meltingPointContribution + solventLossContribution;
    }
}
