package co.unal.camd.ga.haea;

import co.unal.camd.properties.ProblemParameters;
import co.unal.camd.properties.methods.BoilingPoint;
import co.unal.camd.properties.methods.Density;
import co.unal.camd.properties.methods.GibbsEnergy;
import co.unal.camd.properties.methods.MeltingPoint;
import co.unal.camd.properties.methods.MolecularWeight;
import co.unal.camd.properties.methods.SolventLoss;
import co.unal.camd.properties.methods.UnifacEstimator;
import co.unal.camd.properties.model.MixtureProperties;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import com.co.evolution.model.ObjectiveFunction;

import java.util.ArrayList;

public class SolventPowerFitness extends ObjectiveFunction<Molecule> {

    private MoleculeGroups solventUser;
    private MoleculeGroups solute;
    private double ab;
    private double pm;
    private static final double _D = 0.001;
    private static final double _C = 0.999;

    private static double[] propertiesWeights = ProblemParameters.PROPERTIES_WEIGHTS;
    private static double[] beta = ProblemParameters.CONSTRAINTS_BOUNDARIES[0];
    private static double[] Po = ProblemParameters.CONSTRAINTS_BOUNDARIES[1];
    private static double[] uncertainty = ProblemParameters.CONSTRAINTS_BOUNDARIES[2];

    public SolventPowerFitness(MoleculeGroups soluteGroups, MoleculeGroups solventGroupsUser) {
        super(false);
        this.solute = soluteGroups;
        this.solventUser = solventGroupsUser;
        ArrayList<MoleculeGroups> AB = new ArrayList<>();

        MoleculeGroups a0 = soluteGroups;
        a0.setComposition(_D);
        AB.add(a0);

        MoleculeGroups b0 = solventGroupsUser;
        b0.setComposition(_C);
        AB.add(b0);

        UnifacEstimator unifac = new UnifacEstimator(AB);
        ab = unifac.solve();
        //	System.out.println("1___"+ab);

        double pmb = MolecularWeight.compute(solventGroupsUser);
        double pma = MolecularWeight.compute(soluteGroups);
        pm = pma / pmb;
    }

    /**
     * the method to calculate the objective function,
     * A:soluto
     * beta:User Solvent
     * S:objective Solvent
     */

    @Override
    public double apply(Molecule solvent) {
        ArrayList<MoleculeGroups> targetAndDesignedSolvents = new ArrayList<>();
        MoleculeGroups s3 = solvent.getGroupsArray();
        MoleculeGroups b3 = solventUser;
        s3.setComposition(_D);
        b3.setComposition(_C);
        targetAndDesignedSolvents.add(s3);
        targetAndDesignedSolvents.add(b3);

        double gibbsEnergy = GibbsEnergy.compute(solvent);
        double boilingPoint = BoilingPoint.compute(solvent);
        double meltingPoint = MeltingPoint.compute(solvent);
        double density = Density.compute(solvent);
        SolventLoss solventLoss = new SolventLoss(targetAndDesignedSolvents);
        double solventLossVal = solventLoss.compute();

        double r1 = normalizeRestriction(2, beta[0], gibbsEnergy, Po[0], uncertainty[0]);
        double r2 = normalizeRestriction(2, beta[1], boilingPoint, Po[1], uncertainty[1]);
        double r3 = normalizeRestriction(3, beta[2], density, Po[2], uncertainty[2]);
        double r4 = normalizeRestriction(1, beta[3], meltingPoint, Po[3], uncertainty[3]);
        double r5 = normalizeRestriction(2, beta[4], solventLossVal, Po[4], uncertainty[4]);
        double ks = computeKS(solvent);

        //        double fitness = ks * (propertiesWeights[0] * r1 + propertiesWeights[1] * r2 + propertiesWeights[2] * r3 + propertiesWeights[3] * r4);
        double fitness = ks * (propertiesWeights[0] * r1 + propertiesWeights[1] * r2 + propertiesWeights[2] * r3 + propertiesWeights[3] * r4 + propertiesWeights[4] * r5);
        solvent.setMixtureProperties(new MixtureProperties(ks, solventLossVal));

        return fitness;
    }

    private double computeKS(Molecule solvent) {
        double ks;
        ArrayList<MoleculeGroups> BS = new ArrayList<>();
        ArrayList<MoleculeGroups> AS = new ArrayList<>();

        MoleculeGroups b1 = solventUser;
        MoleculeGroups s1 = solvent.getGroupsArray();
        b1.setComposition(_D);
        s1.setComposition(_C);
        BS.add(b1);
        BS.add(s1);

        MoleculeGroups a2 = solute;
        MoleculeGroups s2 = solvent.getGroupsArray();
        a2.setComposition(_D);
        s2.setComposition(_C);
        AS.add(a2);
        AS.add(s2);

        //		UNIFAC aUNIFAC=(UNIFAC)unifacMethod;
        UnifacEstimator unifac = new UnifacEstimator(AS);
        double as = unifac.solve();
        boolean success = unifac.isSuccess();

        unifac = new UnifacEstimator(BS);
        double bs = unifac.solve();
        success = success && unifac.isSuccess();

        if (success)
            ks = (ab * bs) / (as * as) * (pm);
        else {
            ks = -1.0;
            solvent.setSuitable(false);
        }
        return ks;
    }

    private double normalizeRestriction(int type, double B, double Pi, double Po, double inc) {
        double v = 0;
        double Pmax = Po * (1 + inc);
        double Pmin = Po * (1 - inc);

        switch (type) {
            case 1:
                v = Math.pow((1 + Math.exp(-B * (Pi - 0.96 * Pmax) / (Pmax - Pmin))), -1);
                break;

            case 2:
                v = Math.pow((1 + Math.exp(-B * (1.04 * Pmax - Pi) / (Pmax - Pmin))), -1);
                break;

            case 3:
                if (Pi >= Pmax)
                    v = Math.pow((1 + Math.exp(-B * (Pi - 0.96 * Pmax) / (Pmax - Pmin))), -1);
                else
                    v = Math.pow((1 + Math.exp(-B * (1.04 * Pmin - Pi) / (Pmax - Pmin))), -1);
                break;
            default:
                break;
        }
        return v;
    }

}
