package co.unal.camd.properties.environmental;

import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.groups.contributions.EnvironmentalFirstOrderContribution;
import co.unal.camd.properties.groups.contributions.EnvironmentalSecondOrderContribution;

import java.util.Map;

public class AqueousSolubilityLogWs {

    private static final double A_LogWs = 4.5484;
    private static final double B_LogWs = 0.3411;

    public static final double compute(Molecule molecule) {
        double contributionsSum = 0;

        for (Map.Entry<EnvironmentalFirstOrderContribution, Integer> firstOrderContributionEntry : molecule.getEnvironmentalFirstOrderContributions().entrySet())
            if (firstOrderContributionEntry.getKey().getWaterLogWS() != null)
                contributionsSum += firstOrderContributionEntry.getValue() * firstOrderContributionEntry.getKey().getWaterLogWS();

        for (Map.Entry<EnvironmentalSecondOrderContribution, Integer> secondOrderContributionEntry : molecule.getEnvironmentalSecondOrderContributions().entrySet())
            if (secondOrderContributionEntry.getKey().getWaterLogWS() != null)
                contributionsSum += secondOrderContributionEntry.getValue() * secondOrderContributionEntry.getKey().getWaterLogWS();

        return A_LogWs + B_LogWs * molecule.getThermoPhysicalProperties().getMolecularWeight() + contributionsSum - Math.log10(molecule.getThermoPhysicalProperties().getMolecularWeight());
    }

}
