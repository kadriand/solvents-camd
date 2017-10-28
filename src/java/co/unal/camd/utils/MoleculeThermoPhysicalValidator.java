package co.unal.camd.utils;

import co.unal.camd.model.molecule.Molecule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MoleculeThermoPhysicalValidator {
    private int index;
    private String name;
    private String configuration;
    private String casNumber;
    private double molecularWeight;
    private ThermoPhysicalData experimental;
    private ThermoPhysicalData camdEstimation = new ThermoPhysicalData();

    @JsonIgnore
    private Molecule molecule;

    public MoleculeThermoPhysicalValidator buildRecomputed() {
        this.camdEstimation
                .setMolecularWeight(molecule.getMolecularWeight())
                .setGibbsEnergy(molecule.getThermoPhysicalProperties().getGibbsEnergy())
                .setDensity(molecule.getThermoPhysicalProperties().getDensity())
                .setBoilingPoint(molecule.getThermoPhysicalProperties().getBoilingPoint())
                .setMeltingPoint(molecule.getThermoPhysicalProperties().getMeltingPoint());
        return this;
    }

    public MoleculeThermoPhysicalValidator buildIdentifiers() {
        this.camdEstimation
                .setSmiles(molecule.getSmiles())
                .setInChIKey(molecule.getInChIKey());
        return this;
    }

    @Override
    public String toString() {
        return index + "\n" + name;
    }

    String asCsv() {
        return String.format("%s,\"%s\",%s,%s,%s,%s,%s,%s", index, name, casNumber, configuration, molecularWeight, camdEstimation.molecularWeight, experimental.asCsv(), camdEstimation.asCsv());
    }

    String identifiersCsv() {
        return String.format("%s,\"%s\",%s,%s,%s,%s", index, name, configuration, camdEstimation.molecularWeight, camdEstimation.smiles, camdEstimation.inChIKey);
    }

    @Data
    @Accessors(chain = true)
    public static class ThermoPhysicalData {
        private double molecularWeight;
        private double meltingPoint;
        private double boilingPoint;
        private double density;
        private double gibbsEnergy;

        private String smiles;
        private String inChIKey;

        @Override
        public String toString() {
            return "\nMW: " + molecularWeight +
                    "\nMP: " + meltingPoint +
                    "\nBP: " + boilingPoint +
                    "\nD : " + density +
                    "\nGE: " + gibbsEnergy;
        }

        String asCsv() {
            return String.format("%s,%s,%s,%s", meltingPoint, boilingPoint, density, gibbsEnergy);
        }

        public String completeTsv() {
            return String.format("%s%s%s%2$s%s%2$s%s%2$s%s%2$s%s%2$s%s", smiles, "\t", inChIKey, molecularWeight, meltingPoint, boilingPoint, density, gibbsEnergy);
        }

    }
}
