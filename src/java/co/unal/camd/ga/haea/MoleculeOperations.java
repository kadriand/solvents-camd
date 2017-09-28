package co.unal.camd.ga.haea;

import co.unal.camd.view.CamdRunner;

public final class MoleculeOperations {

    public static int findNewRefCode(int valence, boolean functional) {
        int codeOfRow = 0;
        int refCode = 0;
        if (functional) {
            double proba = Math.random();
            double p = 0;
            int n = CamdRunner.CONTRIBUTION_GROUPS.getTotalNumberOfGroupOfValence(valence);
            while (proba <= 1 - p) {
                codeOfRow = (int) (Math.random() * n) + 1;//random row to choose the group
                p = CamdRunner.CONTRIBUTION_GROUPS.getProbability(valence, codeOfRow);
                //	System.out.println("pruebaaa");
            }
            //	/	System.out.println("pruebaa2");
            refCode = CamdRunner.CONTRIBUTION_GROUPS.findGroupCode(valence, codeOfRow);
        } else {
            codeOfRow = 1;//the code of the firs group (Structural group)
            refCode = CamdRunner.CONTRIBUTION_GROUPS.findGroupCode(valence, codeOfRow);
        }
        return refCode;
    }

    public static int findNewRefCode(int type) {
        int codeOfRow = 0;
        int refCode = 0;

        return refCode;
    }

}
