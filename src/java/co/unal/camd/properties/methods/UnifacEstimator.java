package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.properties.parameters.unifac.UnifacInteractionData;
import co.unal.camd.properties.parameters.unifac.UnifacParametersPair;
import co.unal.camd.view.CamdRunner;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

public class UnifacEstimator {

    @Getter
    private boolean success = true;
    private double temperature;
    @Setter
    private int principal = 0;

    private ArrayList<MoleculeGroups> evaluationMolecules;

    public UnifacEstimator(ArrayList<MoleculeGroups> molecules) {
        this.evaluationMolecules = molecules;
        optimizeMolecules();
    }
    ////////////////////////////////////////////////UNIFAC///////////////////////////////////////////////////////

    public double solve(double temp) {
        this.temperature = temp;
        double gamma = computeGamma();
        double GAMMA_RES = computeGammaResidual();

        if (!success)
            return 100000.0;

        gamma += GAMMA_RES;
        gamma = Math.exp(gamma);
        return gamma;
    }

    /**
     * The development of combinatorial part
     */

    //////////F////////
    private double getF() {
        double sum = 0;
        for (MoleculeGroups molecule : evaluationMolecules)
            sum += molecule.getComposition() * molecule.getQ();
        return evaluationMolecules.get(principal).getQ() / sum;
    }

    ///////////V//////////
    private double computeV() {
        double sum = 0;
        for (MoleculeGroups molecule : evaluationMolecules)
            sum += molecule.getComposition() * molecule.getR();
        return evaluationMolecules.get(principal).getR() / sum;
    }

    ///////V'///////////////////
    private double computeVprime() {
        double sum = 0;
        for (MoleculeGroups molecule : evaluationMolecules) {
            sum += molecule.getComposition() * Math.pow(molecule.getR(), 0.75);
        }
        MoleculeGroups mainMolecule = evaluationMolecules.get(principal);
        return Math.pow(mainMolecule.getR(), 0.75) / sum;
    }

    /////////computeGamma i combinat/////////
    private double computeGamma() {
        MoleculeGroups molecule = this.evaluationMolecules.get(principal);
        double vPrime = computeVprime();
        double v = computeV();
        double f = getF();
        return 1 - vPrime + 2.30258509 * Math.log10(vPrime) - 5 * molecule.getQ() * (1 - (v / f) + 2.30258509 * Math.log10(v / f));
    }

    /**
     * this method return an Array that indicate type of groups and amount in the molecule
     */
    private void optimizeMolecules() {
        for (MoleculeGroups molecule : evaluationMolecules)
            molecule.optimize();
    }

    ////////////////////////X///////////////////////////////////////////
    private double computeX(ArrayList<MoleculeGroups> molecules, int principal, int group) {
        double sum = 0;
        double sum2 = 0;
        double x = 0;

        for (MoleculeGroups molecule : molecules) {
            if (molecules.size() == 1)
                x = 1;
            else
                x = molecule.getComposition();
            for (int j = 0; j < molecule.size(); j++)
                sum += x * molecule.getAmount()[j];
        }

        /**
         * Search in all the evaluationMolecules the group m (group) to calculate the Xm
         */
        int codeOfGroup = molecules.get(principal).getGroups()[group];
        for (MoleculeGroups molecule : molecules)
            for (int m = 0; m < molecule.size(); m++)
                if (molecule.getGroups()[m] == codeOfGroup)
                    sum2 += molecule.getComposition() * molecule.getAmount()[m];

        return sum2 / sum;
    }

    ///////////////////THETA////////////////////////////////////
    private double computeTheta(ArrayList<MoleculeGroups> molecules, int principal, int group) {
        double sum = 0;
        MoleculeGroups molecule;
        MoleculeGroups principalMolecule = molecules.get(principal);

        for (int i = 0; i < molecules.size(); i++) {
            molecule = molecules.get(i);
            for (int j = 0; j < molecule.size(); j++)
                sum += computeX(molecules, i, j) * molecule.getGroupContributions()[j].getQParam();
        }

        return computeX(molecules, principal, group) * principalMolecule.getGroupContributions()[group].getQParam() / sum;
    }

    ////////////Y///////////
    private double computeY(int j, int i) {
        double y = 10;
        boolean switched = i > j;
        UnifacParametersPair unifacPair = switched ? new UnifacParametersPair(j, i) : new UnifacParametersPair(i, j);
        UnifacInteractionData unifacInteraction = CamdRunner.CONTRIBUTION_GROUPS.getUnifacInteractions().get(unifacPair);

        success = unifacInteraction != null;
        if (!success)
            return y;

        double a = switched ? unifacInteraction.getAji() : unifacInteraction.getAij();
        double b = switched ? unifacInteraction.getBji() : unifacInteraction.getBij();
        double c = switched ? unifacInteraction.getCji() : unifacInteraction.getCij();
        y = Math.exp(-(a + b * temperature + c * temperature * temperature) / temperature);

        return y;
    }

    ////////////Fi //////////////////
    private double computeFi(ArrayList<MoleculeGroups> molecules, int principal, int group) {
        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        MoleculeGroups molecule;
        MoleculeGroups principalMolecule = molecules.get(principal);
        for (int i = 0; success && i < molecules.size(); i++) {// iterator by evaluationMolecules
            molecule = molecules.get(i);
            /////no usar el evaluationMolecules.get(i).getSize porq hay porblemas de limites
            for (int j = 0; success && j < molecule.size(); j++) {//iterator by each group of molec i
                int m = molecule.getGroupContributions()[j].getMainGroup().getCode();
                int k = principalMolecule.getGroupContributions()[group].getMainGroup().getCode();
                double y = computeY(m, k);
                if (success)
                    sum1 = sum1 + computeTheta(molecules, i, j) * y;

                ////////////////suma3////////////////////7
                sum3 = 0;
                MoleculeGroups innerMoleculesGroups;
                for (int l = 0; success && l < molecules.size(); l++) {
                    innerMoleculesGroups = molecules.get(l);
                    for (int f = 0; f < molecules.get(l).size(); f++) {
                        int n = innerMoleculesGroups.getGroupContributions()[f].getMainGroup().getCode();
                        sum3 = sum3 + computeTheta(molecules, l, f) * computeY(n, m);
                    }
                }
                /////////////////////////////////suma2///////////////////////////
                if (success)
                    sum2 = sum2 + (computeTheta(molecules, i, j) * computeY(k, m) / sum3);
            }
        }
        //System.out.println(moleculesModif.get(principal).get(group).getQ()*(1-2.30258509*Math.log10(sum1)-sum2));
        //System.out.println("esta es la suma 1 :"+sum1);
        //	System.out.println("esta es la suma 2 :"+sum2);
        //	System.out.println("esta es la suma 3.. :"+sum3);
        if (success)
            return principalMolecule.getGroupContributions()[group].getQParam() * (1 - 2.30258509 * Math.log10(sum1) - sum2);
        else
            return 0.0001;
    }

    /////////computeGamma i residual/////////
    private double computeGammaResidual() {
        MoleculeGroups principalMolecule = evaluationMolecules.get(principal);
        ArrayList<MoleculeGroups> moleculesPrincipal = new ArrayList<>(Arrays.asList(principalMolecule)); //create unitary array to place the principal molecule, and allow use computeFi()

        double sum = 0;
        for (int i = 0; success && i < principalMolecule.size(); i++)
            sum += principalMolecule.getAmount()[i] * (computeFi(evaluationMolecules, principal, i) - (computeFi(moleculesPrincipal, 0, i)));

        return success ? sum : 0.00001;
    }

}
