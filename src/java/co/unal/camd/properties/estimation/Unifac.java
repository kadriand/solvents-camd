package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionParametersManager;

import java.util.ArrayList;

public class Unifac extends Methods {
    private double temperature;
    ContributionParametersManager parametersManager;

////////////////////////////////////////////////UNIFAC///////////////////////////////////////////////////////

    @Override
    public double getMethodResult(ArrayList<GroupArray> molecules, int principal, double temp, ContributionParametersManager aGC) {
        if (can_be_done) {
            // TODO Auto-generated method stub
            return solve(molecules, principal, temp, aGC);
        } else {
            return 100000.0;
        }
    }

    /**
     * solve unifac
     */
    private double solve(ArrayList<GroupArray> molecules, int principal, double temp, ContributionParametersManager aGC) {
        temperature = temp;
        this.parametersManager = aGC;
        double gamma = getGamma(principal, molecules);
        getTotalGroupsAndNumber(molecules);
        double GAMMA_RES = getGammaResidual(molecules, principal);

        if (!can_be_done)
            return 100000.0;

        gamma += GAMMA_RES;
        gamma = Math.exp(gamma);
        return gamma;
    }

    /**
     * The development of combinatorial part
     */

    //////////F////////
    private double getF(int principal, ArrayList<GroupArray> molecule) {
        double sum = 0;
        GroupArray g;
        for (GroupArray aMolecule : molecule) {
            g = aMolecule;
            sum += g.getComposition() * g.getq(parametersManager);
            //System.out.println("Qi"+g.getq(parametersManager));
        }
        g = molecule.get(principal);
        //System.out.println("Qiprin"+g.getq(parametersManager));
        // System.out.println("Fi"+g.getq(parametersManager)/sum);
        return g.getq(parametersManager) / sum;
    }

    ///////////V//////////
    private double getV(int principal, ArrayList<GroupArray> molecule) {
        double sum = 0;
        GroupArray g;
        for (GroupArray aMolecule : molecule) {
            g = aMolecule;
            sum += g.getComposition() * g.getr(parametersManager);
        }
        g = molecule.get(principal);
        // System.out.println("V"+g.getr(parametersManager)/sum);
        return g.getr(parametersManager) / sum;
    }

    ///////V'///////////////////
    private double getVprima(int principal, ArrayList<GroupArray> molecule) {
        double sum = 0;
        GroupArray g;
        for (GroupArray aMolecule : molecule) {
            g = aMolecule;
            sum += g.getComposition() * Math.pow(g.getr(parametersManager), 0.75);
            //System.out.println("comVp:"+g.getComposition());
            //System.out.println("r3vp:"+Math.pow(g.getr(parametersManager), 0.75));
            //	System.out.println("vpsum:"+sum);
        }
        g = molecule.get(principal);
        // System.out.println("comPrin:"+g.getComposition());
        //System.out.println("r3Prin:"+Math.pow(g.getr(parametersManager), 0.75));

        //System.out.println("Vp: "+Math.pow(g.getr(parametersManager),0.75)/sum);
        return Math.pow(g.getr(parametersManager), 0.75) / sum;
    }

    /////////gamma i combinat/////////
    private double getGamma(int principal, ArrayList<GroupArray> molecules) {
        GroupArray g = molecules.get(principal);
        // System.out.println("Gamma comb: "+(1-getVprima(principal, molecules)+2.30258509*Math.log10(getVprima(principal, molecules))
        //	-5*g.getq(parametersManager)*(1-(getV(principal,molecules)/getF(principal,molecules))
        //	+2.30258509*Math.log10(getV(principal,molecules)/getF(principal,molecules)))));
        return 1 - getVprima(principal, molecules) + 2.30258509 * Math.log10(getVprima(principal, molecules))
                - 5 * g.getq(parametersManager) * (1 - (getV(principal, molecules) / getF(principal, molecules))
                + 2.30258509 * Math.log10(getV(principal, molecules) / getF(principal, molecules)));
    }

    /**
     * The development of residual part
     *
     */

    /**
     * this method return an Array that indicate type of groups and amount in the molecule
     */
    private void getTotalGroupsAndNumber(ArrayList<GroupArray> molecule) {
        for (GroupArray aMolecule : molecule) {
            aMolecule.optimize();
        }
    }

    ////////////////////////X///////////////////////////////////////////
    private double getX(ArrayList<GroupArray> molecules, int principal, int group) {
        double sum = 0;
        double sum2 = 0;
        double x = 0;
        GroupArray g;
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
        for (GroupArray g2 : molecules) {
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

    ///////////////////TETA////////////////////////////////////
    public double getTeta(ArrayList<GroupArray> molecules, int principal, int group) {
        double sum = 0;
        GroupArray g;
        GroupArray g2 = molecules.get(principal);
        int aux = g2.getGroupCode(group);

        for (int i = 0; i < molecules.size(); i++) {
            g = molecules.get(i);
            for (int j = 0; j < g.size(); j++) {
                aux = g.getGroupCode(j);
                sum = sum + getX(molecules, i, j) * parametersManager.getQ(g.getGroupCode(j));
                //    System.out.println("esta es la suma de teta :"+sum);
            }
        }
        //System.out.println("esta es la suma de teta :"+sum);
        aux = g2.getGroupCode(group);
        //System.out.println("teta: "+getX(molecules,principal,group)*parametersManager.getQ(aux)/sum);
        return getX(molecules, principal, group) * parametersManager.getQ(aux) / sum;
    }

    ////////////Y///////////
    private double getY(int n, int m, double T) {
        n = n - 1;
        m = m - 1;
        double show2 = 10;
        for (int i = 0; can_be_done && i < 3; i++) {
            can_be_done = (parametersManager.getParamij()[i][n][m] != null &&
                    parametersManager.getParamij()[i][n][m].compareTo("**") != 0);
        }
        if (can_be_done) {
            double a = Double.parseDouble(parametersManager.getParamij()[0][n][m]);
            double b = Double.parseDouble(parametersManager.getParamij()[1][n][m]);
            double c = Double.parseDouble(parametersManager.getParamij()[2][n][m]);
            //System.out.println(" a: "+a+" b: "+b+" c: "+c);
            show2 = Math.exp(-(a + b * T + c * T * T) / T);
            //System.out.println(Math.exp(-(a+b*T+c*T*T)/T));
        }
        return show2;
    }

    ////////////Fi //////////////////
    private double getFi(ArrayList<GroupArray> molecules, int principal, int group) {
        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        GroupArray g;
        GroupArray g2 = molecules.get(principal);
        for (int i = 0; can_be_done && i < molecules.size(); i++) {// iterator by molecules
            g = molecules.get(i);
            /////no usar el molecules.get(i).getTotalGroups porq hay porblemas de limites
            for (int j = 0; can_be_done && j < g.size(); j++) {//iterator by each group of molec i
                int m = parametersManager.getPrincipalGroupCode(g.getGroupCode(j));
                int k = parametersManager.getPrincipalGroupCode(g2.getGroupCode(group));

                double y = getY(m, k, temperature);
                if (can_be_done) {
                    sum1 = sum1 + getTeta(molecules, i, j) * getY(m, k, temperature);
                }
                ////////////////suma3////////////////////7
                sum3 = 0;
                GroupArray g3;
                for (int l = 0; can_be_done && l < molecules.size(); l++) {
                    g3 = molecules.get(l);
                    for (int f = 0; f < molecules.get(l).size(); f++) {
                        int n = parametersManager.getPrincipalGroupCode(g3.getGroupCode(f));
                        if (getY(n, m, temperature) == 1000000) {
                            //	System.out.println("no estan todos los parametros de interacci�n");
                        }
                        sum3 = sum3 + getTeta(molecules, l, f) * getY(n, m, temperature);
                    }
                }
                /////////////////////////////////suma2///////////////////////////
                y = getY(k, m, temperature);
                if (can_be_done) {
                    sum2 = sum2 + (getTeta(molecules, i, j) * getY(k, m, temperature) / sum3);
                }
            }

        }
        //System.out.println(moleculesModif.get(principal).get(group).getQ()*(1-2.30258509*Math.log10(sum1)-sum2));
        //System.out.println("esta es la suma 1 :"+sum1);
        //	System.out.println("esta es la suma 2 :"+sum2);
        //	System.out.println("esta es la suma 3.. :"+sum3);
        if (can_be_done) {
            double p = parametersManager.getQ(g2.getGroupCode(group)) * (1 - 2.30258509 * Math.log10(sum1) - sum2);
            //System.out.println("F"+group+": "+p);
            return parametersManager.getQ(g2.getGroupCode(group)) * (1 - 2.30258509 * Math.log10(sum1) - sum2);
        } else {
            return 0.0001;
        }
    }

    /////////gamma i residual/////////
    private double getGammaResidual(ArrayList<GroupArray> molecules, int principal) {
        ArrayList<GroupArray> aMoleculeModif = new ArrayList<GroupArray>();
        GroupArray g = molecules.get(principal);
        aMoleculeModif.add(g); //create unitary array to place the principal molecule, and allow use getFi()

        double sum = 0;
        for (int i = 0; can_be_done && i < g.size(); i++) {
            //	System.out.println("amount"+g.getAmount(i));
            sum = sum + g.getAmount(i) * (getFi(molecules, principal, i) - (getFi(aMoleculeModif, 0, i)));
        }
        //System.out.println("Gamma residual: "+sum);
        if (can_be_done) {
            return sum;
        } else {
            return 0.00001;
        }
    }


    @Override
    public double getMethodResult(Molecules molecules) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getMethodResult(Molecules molecules, double temp) {
        // TODO Auto-generated method stub
        return 0;
    }

}
