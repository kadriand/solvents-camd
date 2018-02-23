package co.unal.camd.ga.haea;

import co.unal.camd.properties.methods.BoilingPoint;
import co.unal.camd.properties.methods.Density;
import co.unal.camd.properties.methods.GibbsEnergy;
import co.unal.camd.properties.methods.MeltingPoint;
import co.unal.camd.properties.methods.MolecularWeight;
import co.unal.camd.properties.methods.SolventLoss;
import co.unal.camd.properties.methods.UnifacEstimator;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import unalcol.optimization.OptimizationFunction;

import java.util.ArrayList;

public class MoleculeFitness extends OptimizationFunction<Molecule> {

    private double temperature;
    private MoleculeGroups solventUser;
    private MoleculeGroups solute;
    private double ab;
    private double pm;
    private static final double _D = 0.001;
    private static final double _C = 0.999;

    private double[] w;
    private double[] B;
    private double[] unc;
    private double[] Po;

    // USE FOR DEBUGGING
    //     private static int eval = 0;

    public MoleculeFitness(double temperature, MoleculeGroups soluteGroups, MoleculeGroups solventGroupsUser, double[] weight, double[][] limits) {
        super();
        this.solute = soluteGroups;
        this.solventUser = solventGroupsUser;
        this.temperature = temperature;
        ArrayList<MoleculeGroups> AB = new ArrayList<>();

        MoleculeGroups a0 = soluteGroups;
        a0.setComposition(_D);
        AB.add(a0);

        MoleculeGroups b0 = solventGroupsUser;
        b0.setComposition(_C);
        AB.add(b0);

        UnifacEstimator unifac = new UnifacEstimator(AB);
        ab = unifac.solve(temperature);
        //	System.out.println("1___"+ab);

        double pmb = MolecularWeight.compute(solventGroupsUser);
        double pma = MolecularWeight.compute(soluteGroups);
        pm = pma / pmb;

        w = weight;
        B = limits[0];
        Po = limits[1];
        unc = limits[2];
        //       		System.out.println("5___"+PM.solve(solute,CONTRIBUTION_GROUPS));
        //        		System.out.println("6___"+PM.solve(solventUser,CONTRIBUTION_GROUPS));
    }

    /**
     * the method to calculate the objective function,
     * A:soluto
     * B:User Solvent
     * S:objective Solvent
     */

    @Override
    public Double apply(Molecule solvent) {
        //System.out.println("solvent"+SB.get(0).getRootContributionGroup());
        //System.out.println("solventuser"+SB.get(1).getRootContributionGroup());

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
        double density = Density.compute(solvent, temperature);
        SolventLoss solventLoss = new SolventLoss(temperature, targetAndDesignedSolvents);
        double solventLossVal = solventLoss.compute();

        double r1 = normalizeRestriction(2, B[0], gibbsEnergy, Po[0], unc[0]);
        double r2 = normalizeRestriction(2, B[1], boilingPoint, Po[1], unc[1]);
        double r3 = normalizeRestriction(3, B[2], density, Po[2], unc[2]);
        double r4 = normalizeRestriction(1, B[3], meltingPoint, Po[3], unc[3]);
        double r5 = normalizeRestriction(2, B[4], solventLossVal, Po[4], unc[4]);
        double ks = computeKS(solvent);

        //	r6 = normalizeRestriction(1, B, Pi, Pm, Pm2, 999999999, Pmin);
        //System.out.println("ks: "+ks);
        double fitness = ks * (w[0] * r1 + w[1] * r2 + w[2] * r3 + w[3] * r4 + w[4] * r5);
        solvent.setFitness(fitness);
        solvent.setTemperature(temperature);
        solvent.getThermodynamicProperties()
                .setSolventLoss(solventLossVal)
                .setKs(ks);

        //        System.out.println("Fitness evaluation " + (++eval) + ": " + fitness);
        return fitness;
    }


    private double computeKS(Molecule solvent) {
        double ks;
        ArrayList<MoleculeGroups> BS = new ArrayList<>();
        ArrayList<MoleculeGroups> AS = new ArrayList<>();

        ///////////////////hacer composiciones 1 y 0 para cada una de las parejas dependiendo cual de los dos est� diluido
        //System.out.println("solut"+AB.get(0).);
        //System.out.println("solventuser"+AB.get(1).getRootContributionGroup());

        MoleculeGroups b1 = solventUser;
        MoleculeGroups s1 = solvent.getGroupsArray();
        b1.setComposition(_D);
        s1.setComposition(_C);
        BS.add(b1);
        BS.add(s1);
        //System.out.println("solvetuser: "+BS.get(0).getRootContributionGroup());
        //System.out.println("solvent"+BS.get(1).getRootContributionGroup());

        MoleculeGroups a2 = solute;
        MoleculeGroups s2 = solvent.getGroupsArray();
        a2.setComposition(_D);
        s2.setComposition(_C);
        AS.add(a2);
        AS.add(s2);
        //System.out.println("solut"+AS.get(0).getRootContributionGroup());
        //System.out.println("solvent: "+AS.get(1).getRootContributionGroup());

        //		UNIFAC aUNIFAC=(UNIFAC)unifacMethod;
        UnifacEstimator unifac = new UnifacEstimator(AS);
        double as = unifac.solve(temperature);
        boolean success = unifac.isSuccess();

        unifac = new UnifacEstimator(BS);
        double bs = unifac.solve(temperature);
        success = success && unifac.isSuccess();

        if (success)
            ks = (ab * bs) / (as * as) * (pm);
        else
            ks = -100000.0;
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
