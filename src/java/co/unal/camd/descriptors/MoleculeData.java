package co.unal.camd.descriptors;

import co.unal.camd.properties.model.Molecule;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;

@Data
@Accessors(chain = true)
public class MoleculeData {
    private int index;
    private int[] groups;
    private String name;
    private String configuration;
    private PropertiesSet experimental;
    private PropertiesSet computed;

    public void buildRecomputed(Molecule molecule) {
        this.recomputed
                .setMolecularWeight(molecule.getThermoPhysicalProperties().getMolecularWeight())
                .setGibbsEnergy(molecule.getThermoPhysicalProperties().getGibbsEnergy())
                .setDensity(molecule.getThermoPhysicalProperties().getDensity())
                .setBoilingPoint(molecule.getThermoPhysicalProperties().getBoilingPoint())
                .setMeltingPoint(molecule.getThermoPhysicalProperties().getMeltingPoint())
                .setDielectricConst(molecule.getThermoPhysicalProperties().getDielectricConstant());
    }

    private PropertiesSet recomputed = new PropertiesSet();

    @Override
    public String toString() {
        return "\n" + name + " " + Arrays.toString(groups);
    }

    @Data
    @Accessors(chain = true)
    public static class PropertiesSet {
        private double molecularWeight;
        private double dielectricConst;
        private double meltingPoint;
        private double boilingPoint;
        private double density;
        private double gibbsEnergy;

        @Override
        public String toString() {
            return "\nMW: " + molecularWeight +
                    "\nDC: " + dielectricConst +
                    "\nMP: " + meltingPoint +
                    "\nBP: " + boilingPoint +
                    "\nD : " + density +
                    "\nGE: " + gibbsEnergy;
        }

        public String compared(PropertiesSet other) {
            return "\nMW: " + molecularWeight + " - " + other.molecularWeight +
                    "\nDC: " + dielectricConst + " - " + other.dielectricConst +
                    "\nMP: " + meltingPoint + " - " + other.meltingPoint +
                    "\nBP: " + boilingPoint + " - " + other.boilingPoint +
                    "\nD : " + density + " - " + other.density +
                    "\nGE: " + gibbsEnergy + " - " + other.gibbsEnergy;
        }

    }
}
