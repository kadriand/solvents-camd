package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class UnifacMethod {

    public boolean canBeDone = true;
    private double temperature;

    ////////////////////////////////////////////////UNIFAC///////////////////////////////////////////////////////

    public double getMethodResult(ArrayList<MoleculeGroups> molecules, int principal, double temp) {
        if (canBeDone)
            return solve(molecules, principal, temp);
        else
            return 100000.0;
    }

    /**
     * solve unifac
     */
    private double solve(ArrayList<MoleculeGroups> molecules, int principal, double temp) {
        this.temperature = temp;
        double gamma = getGamma(principal, molecules);
        getTotalGroupsAndNumber(molecules);
        double GAMMA_RES = getGammaResidual(molecules, principal);

        if (!canBeDone)
            return 100000.0;

        gamma += GAMMA_RES;
        gamma = Math.exp(gamma);
        return gamma;
    }

    /**
     * The development of combinatorial part
     */

    //////////F////////
    private double getF(int principal, ArrayList<MoleculeGroups> molecule) {
        double sum = 0;
        MoleculeGroups g;
        for (MoleculeGroups aMolecule : molecule) {
            g = aMolecule;
            sum += g.getComposition() * g.getQ();
            //System.out.println("Qi"+g.getQ(CONTRIBUTION_GROUPS));
        }
        g = molecule.get(principal);
        //System.out.println("Qiprin"+g.getQ(CONTRIBUTION_GROUPS));
        // System.out.println("Fi"+g.getQ(CONTRIBUTION_GROUPS)/sum);
        return g.getQ() / sum;
    }

    ///////////V//////////
    private double getV(int principal, ArrayList<MoleculeGroups> molecule) {
        double sum = 0;
        MoleculeGroups g;
        for (MoleculeGroups aMolecule : molecule) {
            g = aMolecule;
            sum += g.getComposition() * g.getR();
        }
        g = molecule.get(principal);
        // System.out.println("V"+g.getR(CONTRIBUTION_GROUPS)/sum);
        return g.getR() / sum;
    }

    ///////V'///////////////////
    private double getVprima(int principal, ArrayList<MoleculeGroups> molecule) {
        double sum = 0;
        MoleculeGroups g;
        for (MoleculeGroups aMolecule : molecule) {
            g = aMolecule;
            sum += g.getComposition() * Math.pow(g.getR(), 0.75);
            //System.out.println("comVp:"+g.getComposition());
            //System.out.println("r3vp:"+Math.pow(g.getR(CONTRIBUTION_GROUPS), 0.75));
            //	System.out.println("vpsum:"+sum);
        }
        g = molecule.get(principal);
        // System.out.println("comPrin:"+g.getComposition());
        //System.out.println("r3Prin:"+Math.pow(g.getR(CONTRIBUTION_GROUPS), 0.75));

        //System.out.println("Vp: "+Math.pow(g.getR(CONTRIBUTION_GROUPS),0.75)/sum);
        return Math.pow(g.getR(), 0.75) / sum;
    }

    /////////gamma i combinat/////////
    private double getGamma(int principal, ArrayList<MoleculeGroups> molecules) {
        MoleculeGroups g = molecules.get(principal);
        // System.out.println("Gamma comb: "+(1-getVprima(principal, molecules)+2.30258509*Math.log10(getVprima(principal, molecules))
        //	-5*g.getQ(CONTRIBUTION_GROUPS)*(1-(getV(principal,molecules)/getF(principal,molecules))
        //	+2.30258509*Math.log10(getV(principal,molecules)/getF(principal,molecules)))));
        return 1 - getVprima(principal, molecules) + 2.30258509 * Math.log10(getVprima(principal, molecules))
                - 5 * g.getQ() * (1 - (getV(principal, molecules) / getF(principal, molecules))
                + 2.30258509 * Math.log10(getV(principal, molecules) / getF(principal, molecules)));
    }

    /**
     * The development of residual part
     *
     */

    /**
     * this method return an Array that indicate type of groups and amount in the molecule
     */
    private void getTotalGroupsAndNumber(ArrayList<MoleculeGroups> molecule) {
        for (MoleculeGroups aMolecule : molecule)
            aMolecule.optimize();
    }

    ////////////////////////X///////////////////////////////////////////
    private double getX(ArrayList<MoleculeGroups> molecules, int principal, int group) {
        double sum = 0;
        double sum2 = 0;
        double x = 0;
        MoleculeGroups g;
        for (int i = 0; i < molecules.size(); i++) {
            g = molecules.get(i);
            if (molecules.size() == 1) {
                //	System.out.println("molecula principal");
                x = 1;
            } else {
                //	System.out.println("otras moleculas");
                x = g.getComposition();
            }
            for (int j = 0; j < g.size(); j++) {
                sum += x * g.getAmount(j);
                //System.out.println(sum);
            }
        }
        //System.out.println("X sum: "+sum);
        /**
         * Search in all the molecules the group m (group) to calculate the Xm
         */
        g = molecules.get(principal);
        int codeOfGroup = g.getGroupCode(group);
        for (MoleculeGroups g2 : molecules) {
            for (int m = 0; m < g2.size(); m++) {
                if (g2.getGroupCode(m) == codeOfGroup) {
                    sum2 += g2.getComposition() * g2.getAmount(m);
                }
            }
        }

        //System.out.println("X sum2: "+sum2);
        //  System.out.println("X de:"+group+".."+sum2/sum);
        return sum2 / sum;
    }

    ///////////////////THETA////////////////////////////////////
    public double getTheta(ArrayList<MoleculeGroups> molecules, int principal, int group) {
        double sum = 0;
        MoleculeGroups molecule;
        MoleculeGroups mainMolecule = molecules.get(principal);

        for (int i = 0; i < molecules.size(); i++) {
            molecule = molecules.get(i);
            for (int j = 0; j < molecule.size(); j++)
                sum += getX(molecules, i, j) * molecule.getGroupContributions()[j].getQParam();
        }

        return getX(molecules, principal, group) * mainMolecule.getGroupContributions()[group].getQParam() / sum;
    }

    ////////////Y///////////
    private double getY(int n, int m, double T) {
        n = n - 1;
        m = m - 1;
        double show2 = 10;
        for (int i = 0; canBeDone && i < 3; i++) {
            canBeDone = (CamdRunner.CONTRIBUTION_GROUPS.getIjParameters()[i][n][m] != null &&
                    CamdRunner.CONTRIBUTION_GROUPS.getIjParameters()[i][n][m].compareTo("**") != 0);
        }
        if (canBeDone) {
            double a = Double.parseDouble(CamdRunner.CONTRIBUTION_GROUPS.getIjParameters()[0][n][m]);
            double b = Double.parseDouble(CamdRunner.CONTRIBUTION_GROUPS.getIjParameters()[1][n][m]);
            double c = Double.parseDouble(CamdRunner.CONTRIBUTION_GROUPS.getIjParameters()[2][n][m]);
            //System.out.println(" a: "+a+" b: "+b+" c: "+c);
            show2 = Math.exp(-(a + b * T + c * T * T) / T);
            //System.out.println(Math.exp(-(a+b*T+c*T*T)/T));
        }
        return show2;
    }

    ////////////Fi //////////////////
    private double getFi(ArrayList<MoleculeGroups> molecules, int principal, int group) {
        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        MoleculeGroups g;
        MoleculeGroups g2 = molecules.get(principal);
        for (int i = 0; canBeDone && i < molecules.size(); i++) {// iterator by molecules
            g = molecules.get(i);
            /////no usar el molecules.get(i).getTotalGroups porq hay porblemas de limites
            for (int j = 0; canBeDone && j < g.size(); j++) {//iterator by each group of molec i
                int m = CamdRunner.CONTRIBUTION_GROUPS.getPrincipalGroupCode(g.getGroupCode(j));
                int k = CamdRunner.CONTRIBUTION_GROUPS.getPrincipalGroupCode(g2.getGroupCode(group));

                double y = getY(m, k, temperature);
                if (canBeDone)
                    sum1 = sum1 + getTheta(molecules, i, j) * y;

                ////////////////suma3////////////////////7
                sum3 = 0;
                MoleculeGroups g3;
                for (int l = 0; canBeDone && l < molecules.size(); l++) {
                    g3 = molecules.get(l);
                    for (int f = 0; f < molecules.get(l).size(); f++) {
                        int n = CamdRunner.CONTRIBUTION_GROUPS.getPrincipalGroupCode(g3.getGroupCode(f));
                        if (getY(n, m, temperature) == 1000000) {
                            //	System.out.println("no estan todos los parametros de interacci�n");
                        }
                        sum3 = sum3 + getTheta(molecules, l, f) * getY(n, m, temperature);
                    }
                }
                /////////////////////////////////suma2///////////////////////////
                if (canBeDone)
                    sum2 = sum2 + (getTheta(molecules, i, j) * getY(k, m, temperature) / sum3);
            }
        }
        //System.out.println(moleculesModif.get(principal).get(group).getQ()*(1-2.30258509*Math.log10(sum1)-sum2));
        //System.out.println("esta es la suma 1 :"+sum1);
        //	System.out.println("esta es la suma 2 :"+sum2);
        //	System.out.println("esta es la suma 3.. :"+sum3);
        if (canBeDone)
            return g2.getGroupContributions()[group].getQParam() * (1 - 2.30258509 * Math.log10(sum1) - sum2);
        else
            return 0.0001;
    }

    /////////gamma i residual/////////
    private double getGammaResidual(ArrayList<MoleculeGroups> molecules, int principal) {
        ArrayList<MoleculeGroups> aMoleculeModif = new ArrayList<MoleculeGroups>();
        MoleculeGroups g = molecules.get(principal);
        aMoleculeModif.add(g); //create unitary array to place the principal molecule, and allow use getFi()

        double sum = 0;
        for (int i = 0; canBeDone && i < g.size(); i++) {
            //	System.out.println("amount"+g.getAmount(i));
            sum = sum + g.getAmount(i) * (getFi(molecules, principal, i) - (getFi(aMoleculeModif, 0, i)));
        }
        //System.out.println("Gamma residual: "+sum);
        if (canBeDone) {
            return sum;
        } else {
            return 0.00001;
        }
    }

}
