package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.parameters.unifac.ThermodynamicFirstOrderContribution;
import co.unal.camd.properties.parameters.unifac.ThermodynamicSecondOrderContribution;
import lombok.Setter;

import java.util.Map;
import java.util.stream.IntStream;

public class DielectricConstant {

    private Molecule molecule;

    @Setter
    private double temperature;

    /*TODO move to parameters worksheet*/
    private static final int[] conditionG1 = {81, 82, 14, 18, 19, 20, 41, 55, 56};
    private static final int[] conditionGHC = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 70};
    private static final int[] condition1_9 = {1, 2, 3, 4, 5, 6, 7, 8, 70};
    private static final int[] conditionG3 = {44, 45, 48};
    private static final int[] conditionGND = {24, 25, 26, 44, 45, 48, 53, 63, 64, 71};
    private static final int[] conditionG2 = {42};

    private boolean isConditionG1;
    private boolean isConditionGHC;
    private boolean isCondition1_9;
    private boolean isConditionG3;
    private boolean isConditionGND;
    private boolean isConditionG2;


    public DielectricConstant(Molecule molecule, double temperature) {
        this.temperature = temperature;
        this.molecule = molecule;

        isConditionG1 = isConditionCase(conditionG1);
        isConditionGHC = isConditionCase(conditionGHC);
        isCondition1_9 = isConditionCase(condition1_9);
        isConditionG3 = isConditionCase(conditionG3);
        isConditionGND = isConditionCase(conditionGND);
        isConditionG2 = isConditionCase(conditionG2);
    }

    private double computeVaporizationHeat() {
        double c = 6.829;
        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            c += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getDipoleMomentH1i();
        return c;
    }

    private double computeMolarVolume() {
        double d = 0.01211;
        double c = 0;

        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            c += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getLiquidMolarVolume();

        for (Map.Entry<ThermodynamicSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getSecondOrderContributions().entrySet())
            c += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getLiquidMolarVolume();

        return (c + d) * 1000;
    }

    private double computeD() {
        double R = 8.314;
        double d = Math.pow((computeVaporizationHeat() - R * temperature / 1000) / computeMolarVolume(), 0.5);
        return d;
    }

    private double computeRefracIndex(boolean conditionCase) {
        double d = computeD();
        if (conditionCase) {
            return 1 / 7.26 * (Math.pow(d, 0.36) + 8.15);
        } else {
            return 1 / 14.95 * (d + 13.47);
        }
    }

    private double computeDipolarMoment(boolean conditionCase) {
        int sum = 0;

        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            if (IntStream.of(conditionGHC).anyMatch(i -> i == firstOrderContributionEntry.getValue()))
                sum += firstOrderContributionEntry.getValue();

        if (conditionCase & sum == molecule.getSize())
            return 0;

        double c = 0;
        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            c += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getDipoleMoment();

        return 0.11 * Math.pow(c, 0.29) * Math.pow(computeMolarVolume(), -0.16);
    }

    private double computeE1(boolean conditionCase, boolean otherCond) {
        if (!conditionCase || !otherCond)
            return 0;

        int sum = 0;
        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            if (IntStream.of(condition1_9).anyMatch(i -> i == firstOrderContributionEntry.getValue()))
                sum += firstOrderContributionEntry.getValue();

        return 70 / (sum + 4.5);
    }

    private double computeE2(boolean conditionCase, boolean otherCond) {
        double sum = 0;
        if (!conditionCase || !otherCond)
            return 0;

        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            if (IntStream.of(condition1_9).anyMatch(i -> i == firstOrderContributionEntry.getValue()))
                sum += firstOrderContributionEntry.getValue();

        return -16 * 1.0 / (sum + 3);
    }

    private double computeE3(boolean conditionCase) {
        return conditionCase ? 2.5 : 0;
    }

    public double compute() {
        double DM = computeDipolarMoment(isConditionGHC);
        double VM = computeMolarVolume();
        double r;
        if (DM <= 0.5)
            r = (0.1 + Math.pow(computeRefracIndex(isConditionGND), 2));
        else
            r = 0.91 * (48 * DM * DM - 15.5 * DM * DM * DM) * Math.pow(VM, -0.5) + computeE1(isConditionG1, isCondition1_9) + computeE2(isConditionG2, isCondition1_9) + computeE3(isConditionG3);
        return r;
    }

    private boolean isConditionCase(int[] conditionCase) {
        for (Map.Entry<ThermodynamicFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getFirstOrderContributions().entrySet())
            if (IntStream.of(conditionCase).anyMatch(i -> i == firstOrderContributionEntry.getValue()))
                return true;
        return false;
    }

}
