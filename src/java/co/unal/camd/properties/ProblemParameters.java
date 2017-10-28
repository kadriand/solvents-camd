package co.unal.camd.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kadri on 15/03/2017.
 */
public class ProblemParameters {

    public static double DEFAULT_TEMPERATURE = 298.15; // K
    public static int DEFAULT_MAX_ITERATIONS = 50; // number of generations for the evolution
    public static int DEFAULT_PARENTS_POOL = 50; // number of generations for the evolution
    public static int DEFAULT_MAX_GROUPS_PER_MOLECULE = 10; // number of generations for the evolution
    public static int DEFAULT_MAX_FUNCTIONAL_ELEMENTS = 3;

    public static final int[] DEFAULT_UNCHECKED_FAMILIES = new int[]{3, 13, 16, 17};
    public static final double[] PROPERTIES_WEIGHTS = {0.2, 0.2, 0.2, 0.2, 0.2};  // gibbsEnergy, boilingPoint, density, meltingPoint, sloss
    public static double[][] CONSTRAINTS_BOUNDARIES = defineBoundaries();

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

    private static double[][] defineBoundaries() {
        double[][] boundaries = new double[3][5];

        // Parameter beta See disseration page 72
        boundaries[0][0] = 15;
        boundaries[0][1] = 15;
        boundaries[0][2] = 15;
        boundaries[0][3] = 15;
        boundaries[0][4] = 15;

        // Boundaries of each property. See disseration page 73
        boundaries[1][0] = 5000;
        boundaries[1][1] = 573;
        boundaries[1][2] = 1;
        boundaries[1][3] = 323;
        boundaries[1][4] = 0.1;

        // Uncertainty. See disseration page 74
        boundaries[2][0] = 0.076;
        boundaries[2][1] = 0.0142;
        boundaries[2][2] = 0.1;
        boundaries[2][3] = 0.0723;
        boundaries[2][4] = 0.05;
        return boundaries;
    }

}
