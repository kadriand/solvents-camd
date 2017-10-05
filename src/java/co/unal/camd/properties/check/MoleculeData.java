package co.unal.camd.properties.check;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;

@Data
@Accessors(chain = true)
public class MoleculeData {
    private int index;
    private int[] groups;
    private String name;
    private PropertiesSet experimental;
    private PropertiesSet computed;
    private PropertiesSet recomputed = new PropertiesSet();

    @Override
    public String toString() {
        return "\n" + name + Arrays.toString(groups);
    }

    @Data
    @Accessors(chain = true)
    public static class PropertiesSet {
        private double dielectricConst;
        private double fusionTemp;
        private double boilingTemp;
        private double density;
        private double deltaGibbs;

        @Override
        public String toString() {
            return "\nDC: " + dielectricConst +
                    "\nMT: " + fusionTemp +
                    "\nBT: " + boilingTemp +
                    "\nD : " + density +
                    "\nGE: " + deltaGibbs;
        }

        public String compared(PropertiesSet other) {
            return "\nDC: " + dielectricConst + " - " + other.dielectricConst +
                    "\nMT: " + fusionTemp + " - " + other.fusionTemp +
                    "\nBT: " + boilingTemp + " - " + other.boilingTemp +
                    "\nD : " + density + " - " + other.density +
                    "\nGE: " + deltaGibbs + " - " + other.deltaGibbs;
        }

    }
}
