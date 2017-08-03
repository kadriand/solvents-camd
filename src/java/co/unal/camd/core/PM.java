package co.unal.camd.core;

import co.unal.camd.control.parameters.ContributionParametersManager;

public class PM {

    private static double sum;

    public static double getMethodResult(GroupArray aGroupArray, ContributionParametersManager aGC) {
        sum = 0;
        for (int i = 0; i < aGroupArray.size(); i++) {
            sum += aGroupArray.getAmount(i) * aGC.getPM(aGroupArray.getGroupCode(i));
        }
        return sum;
    }
}
