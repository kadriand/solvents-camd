package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionParametersManager;

public final class MoleculeOperations {

    public static int findNewRefCode(int valence, ContributionParametersManager contributionParametersManager, boolean functional) {
        int codeOfRow = 0;
        int refCode = 0;
        if (functional) {
            double proba = Math.random();
            double p = 0;
            int n = contributionParametersManager.getTotalNumberOfGroupOfValence(valence);
            while (proba <= 1 - p) {
                codeOfRow = (int) (Math.random() * n) + 1;//random row to choose the group
                p = contributionParametersManager.getProbability(valence, codeOfRow);
                //	System.out.println("pruebaaa");
            }
//	/	System.out.println("pruebaa2");
            refCode = contributionParametersManager.getRefCode(valence, codeOfRow);
        } else {
            codeOfRow = 1;//the code of the firs group (Structural group)
            refCode = contributionParametersManager.getRefCode(valence, codeOfRow);
        }
        return refCode;
    }

    public static int findNewRefCode(int type, ContributionParametersManager aGC) {
        int codeOfRow = 0;
        int refCode = 0;

        return refCode;
    }

}
