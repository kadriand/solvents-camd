package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionGroupsManager;

public class PM {

    private static double sum;

    public static double getMethodResult(GroupArray aGroupArray, ContributionGroupsManager aGC) {
        sum = 0;
        for (int i = 0; i < aGroupArray.size(); i++) {
            sum += aGroupArray.getAmount(i) * aGC.getPM(aGroupArray.getGroupCode(i));
        }
        return sum;
    }
}
