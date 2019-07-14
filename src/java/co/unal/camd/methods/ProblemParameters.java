package co.unal.camd.methods;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kadri on 15/03/2017.
 */
public class ProblemParameters {

    public static boolean MULTI_RUNS_MODE = false;
    public static int DEFAULT_RUNS = 100; // number of runs for Multi-run mode

    public static double DEFAULT_TEMPERATURE = 298.15; // K
    public static int DEFAULT_MAX_ITERATIONS = 50; // number of generations for the evolution
    public static int DEFAULT_PARENTS_POOL = 50; // number of generations for the evolution
    public static int DEFAULT_MAX_GROUPS_PER_MOLECULE = 10; // number of generations for the evolution
    public static int DEFAULT_MAX_FUNCTIONAL_ELEMENTS = 3;
    public static double DILUTION_FRACTION = 0.0001;

    public static final int[] DEFAULT_UNCHECKED_FAMILIES = new int[]{3, 13, 16, 17};

    public static ConstraintWeights CONSTRAINTS_WEIGHTS = new ConstraintWeights();
    public static ConstraintBoundaries CONSTRAINTS_BOUNDARIES = new ConstraintBoundaries();

    public static boolean EXCLUDE_NESTED_GROUPS = true;

    public static boolean IS_DB_ENABLE = false;

    @Getter
    @Setter
    private static double temperature = DEFAULT_TEMPERATURE;
    @Getter
    @Setter
    private static int maxIterations = DEFAULT_MAX_ITERATIONS;
    @Getter
    @Setter
    private static int maxGroupsPerMolecule = DEFAULT_MAX_GROUPS_PER_MOLECULE;
    @Getter
    @Setter
    private static int parentsPoolSize = DEFAULT_PARENTS_POOL;

    @Data
    public static class ConstraintWeights {
        double gibbsEnergy = 1.0;
        double boilingPoint = 1.0;
        double meltingPoint = 1.0;
        double solventLoss = 2.0;
        double marketAvailability = 1.0;
    }

    @Data
    public static class ConstraintBoundaries {
        double gibbsEnergy = 100.0;
        double meltingPoint = 293.15;
        double boilingPoint = 573.15;
        double solventLoss = 0.15;
    }
}
