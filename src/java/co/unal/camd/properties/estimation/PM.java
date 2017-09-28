package co.unal.camd.properties.estimation;

import co.unal.camd.view.CamdRunner;

public class PM {

    private static double sum;

    public static double getMethodResult(MoleculeGroups aGroupArray) {
        sum = 0;
        for (int i = 0; i < aGroupArray.size(); i++) {
            sum += aGroupArray.getAmount(i) * CamdRunner.CONTRIBUTION_GROUPS.getPM(aGroupArray.getGroupCode(i));
        }
        return sum;
    }
}
