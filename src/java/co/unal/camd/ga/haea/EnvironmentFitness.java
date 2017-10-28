package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.EnvironmentalProperties;
import co.unal.camd.properties.model.Molecule;
import com.co.evolution.model.ObjectiveFunction;

public class EnvironmentFitness extends ObjectiveFunction<Molecule> {


    private static final double K_WAT_1 = 0.282756924;
    private static final double K_WAT_2 = 1.788079538;
    private static final double K_WAT_3 = 0.093659445;
    private static final double K_AIR_1 = 1.05649E-05;

    public EnvironmentFitness() {
        super(true);
    }

    /**
     * the method to calculate the objective function,
     *
     */
    @Override
    public double apply(Molecule solvent) {
        if (!solvent.isSuitable())
            return 10.0;

        EnvironmentalProperties environmental = solvent.getEnvironmentalProperties();
        double waterToxicity = K_WAT_1 * environmental.getWaterLogWS();
        waterToxicity += K_WAT_2 * environmental.getRatLD50();
        waterToxicity += K_WAT_3 * waterToxicity / environmental.getWaterLC50DM() / environmental.getWaterLC50FM();
        double airToxicity = K_AIR_1 * (environmental.getAirERAc() + environmental.getAirERAnc() + environmental.getAirEUAc() + environmental.getAirEUAnc());
        return waterToxicity + airToxicity;
    }


}
