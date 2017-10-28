package co.unal.camd.ga.haea;

import co.unal.camd.model.EnvironmentalProperties;
import co.unal.camd.model.molecule.Molecule;
import com.co.evolution.model.ObjectiveFunction;

public class EnvironmentFitness extends ObjectiveFunction<Molecule> {

    public EnvironmentFitness() {
        super(true);
    }

    private static final double K_RAT_LD50 = 5.2351663;
    private static final double K_WS = 1.271982315;
    private static final double K_BCF = 2.771484851;

    /**
     * the method to calculate the objective function,
     */
    @Override
    public double compute(Molecule solvent) {
        if (!solvent.isSuitable())
            return 1000.0;

        EnvironmentalProperties environmental = solvent.getEnvironmentalProperties();
        double waterToxicity = K_WS * environmental.getWaterLogWS() + K_BCF * environmental.getWaterLogBFC() - K_RAT_LD50 * environmental.getRatLogLD50() - environmental.getWaterLogLC50DM() - environmental.getWaterLogLC50FM();
        // double airToxicity = Math.log(environmental.getAirRuralERAc() + environmental.getAirRuralERAnc() + environmental.getAirUrbanEUAc() + environmental.getAirUrbanEUAnc());
        return waterToxicity;
    }

}
