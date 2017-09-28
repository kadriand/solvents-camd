package co.unal.camd.ga.haea;

import co.unal.camd.properties.estimation.BoilingTemp;
import co.unal.camd.properties.estimation.Density;
import co.unal.camd.properties.estimation.DielectricConstant;
import co.unal.camd.properties.estimation.GibbsEnergy;
import co.unal.camd.properties.estimation.MeltingTemp;
import co.unal.camd.properties.estimation.Methods;
import co.unal.camd.properties.estimation.Molecule;
import co.unal.camd.properties.estimation.MoleculeGroups;
import co.unal.camd.properties.estimation.PM;
import co.unal.camd.properties.estimation.SolventLoss;
import co.unal.camd.properties.estimation.Unifac;
import unalcol.optimization.OptimizationFunction;

import java.util.ArrayList;

public class MoleculeFitness extends OptimizationFunction<Molecule> {

    private Methods allMethods;
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

    public MoleculeFitness(double temperature, MoleculeGroups soluteGroups, MoleculeGroups solventGroupsUser, double[] weight, double[][] limits) {
        super();
        this.solute = soluteGroups;
        this.solventUser = solventGroupsUser;
        this.temperature = temperature;
        allMethods = new Unifac();
        ArrayList<MoleculeGroups> AB = new ArrayList<>();

        MoleculeGroups a0 = soluteGroups;
        a0.setComposition(_D);
        AB.add(a0);

        MoleculeGroups b0 = solventGroupsUser;
        b0.setComposition(_C);
        AB.add(b0);

        allMethods.canBeDone = true;
        ab = allMethods.getMethodResult(AB, 0, temperature);
        //	System.out.println("1___"+ab);

        double pmb = PM.getMethodResult(solventGroupsUser);
        double pma = PM.getMethodResult(soluteGroups);
        pm = pma / pmb;

        w = weight;
        B = limits[0];
        Po = limits[1];
        unc = limits[2];
        //       		System.out.println("5___"+PM.getMethodResult(solute,CONTRIBUTION_GROUPS));
        //        		System.out.println("6___"+PM.getMethodResult(solventUser,CONTRIBUTION_GROUPS));
    }


    /**
     * the method to calculate the objective function,
     * A:soluto
     * B:User Solvent
     * S:objective Solvent
     */

    @Override
    public Double apply(Molecule solvent) {

        //System.out.println("solvent"+SB.get(0).getMoleculeByRootGroup());
        //System.out.println("solventuser"+SB.get(1).getMoleculeByRootGroup());

        ArrayList<MoleculeGroups> SB = new ArrayList<>();
        MoleculeGroups s3 = solvent.getGroupArray();
        MoleculeGroups b3 = solventUser;
        s3.setComposition(_D);
        b3.setComposition(_C);
        SB.add(s3);
        SB.add(b3);

        ArrayList<Integer> secOrderCodes = solvent.get2OrderGroupArray();
        GibbsEnergy GE = new GibbsEnergy(solvent, secOrderCodes);
        BoilingTemp BT = new BoilingTemp(solvent, secOrderCodes);
        Density D = new Density(solvent, temperature);
        MeltingTemp MT = new MeltingTemp(solvent, secOrderCodes);
        SolventLoss SL = new SolventLoss(temperature, SB);
        DielectricConstant DC = new DielectricConstant(solvent, secOrderCodes, temperature);

        double ge = GE.getMethodResult();
        double bt = BT.getMethodResult();
        double d = D.getMethodResult();
        double mt = MT.getMethodResult();
        double sl = SL.getMethodResult();
        double dc = DC.getDielectricConstant();

        double r1 = normalizeRestriction(2, B[0], ge, Po[0], unc[0]);
        double r2 = normalizeRestriction(2, B[1], bt, Po[1], unc[1]);
        double r3 = normalizeRestriction(3, B[2], d, Po[2], unc[2]);
        double r4 = normalizeRestriction(1, B[3], mt, Po[3], unc[3]);
        double r5 = normalizeRestriction(2, B[4], sl, Po[4], unc[4]);
        double ks = computeKS(solvent);

        //System.out.println("s___"+solvent.getArray().size());
        //System.out.println("a___"+solute.getArray().size());
        //System.out.println("b___"+solventUser.getArray().size());

        //	r6 = normalizeRestriction(1, B, Pi, Pm, Pm2, 999999999, Pmin);
        //System.out.println("ks: "+ks);
        double fitness = ks * (w[0] * r1 + w[1] * r2 + w[2] * r3 + w[3] * r4 + w[4] * r5);
        solvent.setFitness(fitness);
        solvent.setGe(ge)
                .setBt(bt)
                .setD(d)
                .setMt(mt)
                .setSl(sl)
                .setDc(dc)
                .setKs(ks);

        System.out.println("evalfit: " + fitness);
        return fitness;
    }

    private double computeKS(Molecule solvent) {
        double ks = 0;
        ArrayList<MoleculeGroups> BS = new ArrayList<>();
        ArrayList<MoleculeGroups> AS = new ArrayList<>();

        ///////////////////hacer composiciones 1 y 0 para cada una de las parejas dependiendo cual de los dos est� diluido
        //System.out.println("solut"+AB.get(0).);
        //System.out.println("solventuser"+AB.get(1).getMoleculeByRootGroup());

        MoleculeGroups b1 = solventUser;
        MoleculeGroups s1 = solvent.getGroupArray();
        b1.setComposition(_D);
        s1.setComposition(_C);
        BS.add(b1);
        BS.add(s1);
        //System.out.println("solvetuser: "+BS.get(0).getMoleculeByRootGroup());
        //System.out.println("solvent"+BS.get(1).getMoleculeByRootGroup());

        MoleculeGroups a2 = solute;
        MoleculeGroups s2 = solvent.getGroupArray();
        a2.setComposition(_D);
        s2.setComposition(_C);
        AS.add(a2);
        AS.add(s2);
        //System.out.println("solut"+AS.get(0).getMoleculeByRootGroup());
        //System.out.println("solvent: "+AS.get(1).getMoleculeByRootGroup());

        //		UNIFAC aUNIFAC=(UNIFAC)allMethods;
        allMethods.canBeDone = true;
        double as = allMethods.getMethodResult(AS, 0, temperature);
        double bs = allMethods.getMethodResult(BS, 0, temperature);
        ks = (ab * bs) / (as * as) * (pm);

        //	System.out.println("2___"+allMethods.getMethodResult(BS, 0,temperature,CONTRIBUTION_GROUPS));
        //	System.out.println("3___"+allMethods.getMethodResult(AS,0,temperature,CONTRIBUTION_GROUPS));

        if (!allMethods.canBeDone) {
            allMethods.canBeDone = true;
            ks = -100000.0;
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
