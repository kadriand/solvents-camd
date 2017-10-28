package co.unal.camd.methods.unifac;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.unifac.properties.InsufficientParametersException;
import co.unal.camd.methods.unifac.properties.UnifacCalculator;
import co.unal.camd.model.molecule.Molecule;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Accessors(chain = true)
public class MixtureProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(MixtureProperties.class);
    private static final boolean SHOW_PROPERTIES_ERRORS = false;

    private Molecule headMolecule;

    @Getter(AccessLevel.NONE)
    private BinaryProperties binary;

    @Getter(AccessLevel.NONE)
    private BinaryProperties infiniteDilution;

    @Getter(AccessLevel.NONE)
    private TernaryProperties ternary;

    private double surfaceAreaQ;
    private double volumeR;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private UnifacCalculator unifacCalculator = new UnifacCalculator();

    public MixtureProperties(Molecule molecule) {
        this.headMolecule = molecule;
        this.volumeR = molecule.pickAllGroups().stream().mapToDouble(group -> group.getUnifacSubGroup().getRParam()).sum();
        this.surfaceAreaQ = molecule.pickAllGroups().stream().mapToDouble(group -> group.getUnifacSubGroup().getQParam()).sum();
    }

    public BinaryProperties getInfiniteDilution(Molecule solvent) {
        if (infiniteDilution != null && infiniteDilution.solvent == solvent)
            return this.infiniteDilution;

        double diluted = ProblemParameters.DILUTION_FRACTION;
        headMolecule.setComposition(diluted);
        solvent.setComposition(1 - diluted);
        this.infiniteDilution = new BinaryProperties(solvent);
        return this.infiniteDilution;
    }

    public BinaryProperties getBinary(Molecule otherMolecule) {
        if (binary == null || binary.solvent != otherMolecule)
            this.binary = new BinaryProperties(otherMolecule);
        return this.binary;
    }

    public BinaryProperties getLastBinary() {
        return this.binary != null ? this.binary : this.infiniteDilution;
    }

    public TernaryProperties getTernary(BinaryProperties targetInMedia) {
        if (ternary == null || ternary.targetInMedia != targetInMedia)
            this.ternary = new TernaryProperties(targetInMedia);
        return ternary;
    }

    public TernaryProperties getLastTernary() {
        return this.ternary;
    }

    @Data
    @Accessors(chain = true)
    public class BinaryProperties {
        @Getter(AccessLevel.NONE)
        private Molecule solute;
        @Getter(AccessLevel.NONE)
        private Molecule solvent;
        private double activityCoefficient;
        private double solventLoss;

        BinaryProperties(Molecule solvent) {
            this.solute = headMolecule;
            this.solvent = solvent;
            try {
                this.activityCoefficient = unifacCalculator.computeActivity(solute, solvent);
                this.solventLoss = 1 / activityCoefficient * solute.getMolecularWeight() / solvent.getMolecularWeight();
            } catch (InsufficientParametersException e) {
                if (SHOW_PROPERTIES_ERRORS)
                    LOGGER.error(e.getMessage());
                headMolecule.setSuitable(false);
                this.solventLoss = 1000;
            }
        }
    }

    @Data
    @Accessors(chain = true)
    public class TernaryProperties {
        @Getter(AccessLevel.NONE)
        private BinaryProperties targetInMedia;
        private double ks;

        public TernaryProperties(BinaryProperties targetInMedia) {
            this.targetInMedia = targetInMedia;
            double dilution = ProblemParameters.DILUTION_FRACTION;
            try {
                double activityTargetInMedia = targetInMedia.activityCoefficient;
                double activityMediaInHead = unifacCalculator.computeActivity(targetInMedia.solvent.setComposition(dilution), headMolecule.setComposition(1 - dilution));
                double activityTargetInHead = unifacCalculator.computeActivity(targetInMedia.solute.setComposition(dilution), headMolecule.setComposition(1 - dilution));

                this.ks = activityTargetInMedia * activityMediaInHead / activityTargetInHead / activityTargetInHead * targetInMedia.solute.getMolecularWeight() / headMolecule.getMolecularWeight();
            } catch (InsufficientParametersException e) {
                if (SHOW_PROPERTIES_ERRORS)
                    LOGGER.error(e.getMessage());
                headMolecule.setSuitable(false);
                this.ks = -1;
            }
        }

    }
}
