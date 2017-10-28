package co.unal.camd.methods.unifac.properties;

import co.unal.camd.methods.ProblemParameters;
import co.unal.camd.methods.unifac.UnifacSubGroup;
import co.unal.camd.methods.unifac.UnifacPairInteractions;
import co.unal.camd.methods.unifac.Unifacij;
import co.unal.camd.model.molecule.MoleculesMixture;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.model.molecule.UnifacGroupNode;
import co.unal.camd.view.CamdRunner;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class UnifacCalculator {

    private MoleculesMixture<Molecule> molecules;
    private Molecule headMolecule;
    @Getter
    private boolean success;

    public double computeActivity(Molecule... moleculesArray) throws InsufficientParametersException {
        success = true;
        molecules = new MoleculesMixture<>(moleculesArray);
        headMolecule = moleculesArray[0];

        double combinatorialPart = combinatorialActivity();
        double residualPart = computeGammaResidual();

        // The higher the activity coefficient of A in B, the less soluble A in B is
        return success ? Math.exp(combinatorialPart + residualPart) : 10000;
    }

    /**
     * Ln(gamma)^C : combinatorial (C) part
     */
    private double combinatorialActivity() {
        double volumeVprime = volumePrime();
        double volumeV = volumeV();
        double surfaceF = surfaceF();
        return 1 - volumeVprime + Math.log(volumeVprime) - 5 * headMolecule.getMixtureProperties().getSurfaceAreaQ() * (1 - volumeV / surfaceF + Math.log(volumeV / surfaceF));
    }

    /**
     * Vi' : volume/mol fraction ratio prime
     */
    private double volumePrime() {
        double sum = molecules.stream().mapToDouble(molecule -> molecule.getComposition() * Math.pow(molecule.getMixtureProperties().getVolumeR(), 0.75)).sum();
        return Math.pow(headMolecule.getMixtureProperties().getVolumeR(), 0.75) / sum;
    }

    /**
     * Vi : volume/mol fraction ratio
     */
    private double volumeV() {
        double sum = molecules.stream().mapToDouble(molecule -> molecule.getComposition() * molecule.getMixtureProperties().getVolumeR()).sum();
        return headMolecule.getMixtureProperties().getVolumeR() / sum;
    }

    /**
     * Fi : surface area/mol fraction ratio
     */
    private double surfaceF() {
        double sum = molecules.stream().mapToDouble(molecule -> molecule.getComposition() * molecule.getMixtureProperties().getSurfaceAreaQ()).sum();
        return headMolecule.getMixtureProperties().getSurfaceAreaQ() / sum;
    }

    /**
     * Ln(gamma)^R : residual (R) part
     */
    private double computeGammaResidual() throws InsufficientParametersException {
        ArrayList<UnifacGroupNode> headMoleculeGroups = headMolecule.pickAllGroups();
        double sum = 0;
        for (UnifacGroupNode group : headMoleculeGroups)
            sum += residualActivityGAMMA(group.getUnifacSubGroup(), molecules) - residualActivityGAMMA(group.getUnifacSubGroup(), new MoleculesMixture<>(headMolecule));
        return sum;
    }

    /**
     * Ln(GAMMA)k : residual activity coefficient of group k
     */
    private double residualActivityGAMMA(UnifacSubGroup kGroup, MoleculesMixture<Molecule> solutionMolecules) throws InsufficientParametersException {
        List<UnifacSubGroup> solutionGroups = solutionMolecules.getSolutionGroups();
        double firstSum = 0;

        for (UnifacSubGroup lGroup : solutionGroups)
            firstSum += thetaAreaFraction(lGroup, solutionMolecules) * psiGroupGroupInteraction(lGroup, kGroup);

        double secondSum = 0;
        for (UnifacSubGroup lGroup : solutionGroups) {
            double sum = 0;
            for (UnifacSubGroup mGroup : solutionGroups)
                sum += thetaAreaFraction(mGroup, solutionMolecules) * psiGroupGroupInteraction(mGroup, lGroup);
            secondSum += thetaAreaFraction(lGroup, solutionMolecules) * psiGroupGroupInteraction(kGroup, lGroup) / sum;
        }

        return kGroup.getQParam() * (1 - Math.log(firstSum) - secondSum);
    }

    /**
     * theta_i : area fraction of group l
     */
    private double thetaAreaFraction(UnifacSubGroup lGroup, MoleculesMixture<Molecule> solutionMolecules) {
        List<UnifacSubGroup> solutionGroups = solutionMolecules.getSolutionGroups();

        double sum = solutionGroups.stream().mapToDouble(
                mGroup -> mGroup.getQParam() * jiMoleFraction(mGroup, solutionMolecules)).sum();
        return lGroup.getQParam() * jiMoleFraction(lGroup, solutionMolecules) / sum;
    }

    /**
     * JI_m group mole fractions Xm
     */
    private double jiMoleFraction(UnifacSubGroup mGroup, MoleculesMixture<Molecule> solutionMolecules) {
        double firstSum = solutionMolecules.stream().mapToDouble(
                molecule -> molecule.pickAllGroups().stream().filter(
                        unifacGroup -> unifacGroup.getUnifacSubGroup() == mGroup)
                        .count() * molecule.getComposition())
                .sum();
        double secondSum = solutionMolecules.getTotalGroupsFractions();
        return firstSum / secondSum;
    }

    /**
     * PSI_mn group–group interaction between group m and n
     */
    private double psiGroupGroupInteraction(UnifacSubGroup nGroup, UnifacSubGroup mGroup) throws InsufficientParametersException {
        boolean ordered = nGroup.getMainGroup().getCode() < mGroup.getMainGroup().getCode();
        Unifacij unifacPair = ordered ? new Unifacij(nGroup, mGroup) : new Unifacij(mGroup, nGroup);
        UnifacPairInteractions unifacPairInteractions = CamdRunner.CONTRIBUTION_GROUPS.getUnifacInteractions().get(unifacPair);

        if (unifacPairInteractions == null) {
            success = false;
            throw new InsufficientParametersException(nGroup, mGroup);
        }

        double a = ordered ? unifacPairInteractions.getAij() : unifacPairInteractions.getAji();
        double b = ordered ? unifacPairInteractions.getBij() : unifacPairInteractions.getBji();
        double c = ordered ? unifacPairInteractions.getCij() : unifacPairInteractions.getCji();
        double temperature = ProblemParameters.getTemperature();

        return Math.exp(-(a + b * temperature + c * temperature * temperature) / temperature);
    }

}
