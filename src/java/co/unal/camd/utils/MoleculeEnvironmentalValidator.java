package co.unal.camd.utils;

import co.unal.camd.model.molecule.Molecule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MoleculeEnvironmentalValidator {
    private int index;
    private String name;
    private String configuration;
    private String casNumber;
    private double molecularWeight;
    private EnvironmentalData experimental;
    private EnvironmentalData rigorousEstimation;
    private EnvironmentalData camdEstimation = new EnvironmentalData();

    @JsonIgnore
    private Molecule molecule;

    public MoleculeEnvironmentalValidator buildRecomputed() {
        this.camdEstimation
                .setWaterLogLC50FM(-molecule.getEnvironmentalProperties().getWaterLogLC50FM())
                .setWaterLogLC50DM(-molecule.getEnvironmentalProperties().getWaterLogLC50DM())
                .setRatLogLD50(-molecule.getEnvironmentalProperties().getRatLogLD50())
                .setWaterLogWS(-molecule.getEnvironmentalProperties().getWaterLogWS())
                .setWaterLogBFC(molecule.getEnvironmentalProperties().getWaterLogBFC())
                .setAirEUAc(molecule.getEnvironmentalProperties().getAirUrbanEUAc())
                .setAirEUAnc(molecule.getEnvironmentalProperties().getAirUrbanEUAnc())
                .setAirERAc(molecule.getEnvironmentalProperties().getAirRuralERAc())
                .setAirERAnc(molecule.getEnvironmentalProperties().getAirRuralERAnc());
        return this;
    }

    public MoleculeEnvironmentalValidator buildIdentifiers() {
        this.camdEstimation
                .setSmiles(molecule.getSmiles())
                .setInChIKey(molecule.getInChIKey());
        return this;
    }

    @Override
    public String toString() {
        return index + "\n" + name;
    }

    String waterAsCsv() {
        return String.format("%s,\"%s\",%s,%s,%s,%s,%s,%s", index, name, casNumber, configuration, molecularWeight, experimental.waterAsCsv(), rigorousEstimation.waterAsCsv(), camdEstimation.waterAsCsv());
    }

    String airAsCsv() {
        return String.format("%s,\"%s\",%s,%s,%s,%s,%s", index, name, casNumber, configuration, molecularWeight, experimental.airAsCsv(), camdEstimation.airAsCsv());
    }

    String identifiersCsv() {
        return String.format("%s,\"%s\",%s,%s,%s,%s", index, name, configuration, molecularWeight, camdEstimation.smiles, camdEstimation.inChIKey);
    }

    @Data
    @Accessors(chain = true)
    public static class EnvironmentalData {
        private String smiles;
        private String inChIKey;
        /**
         * in -Log(mol/lit)
         */
        private double waterLogLC50FM;
        /**
         * in -Log(mol/lit)
         */
        private double waterLogLC50DM;
        /**
         * in -Log(mol/kg)
         */
        private double ratLogLD50;
        /**
         * in -Log(mol/lit)
         */
        private double waterLogWS;
        /**
         * in Log(no units)
         */
        private double waterLogBFC;

        private double airEUAc;
        private double airEUAnc;
        private double airERAc;
        private double airERAnc;

        @Override
        public String toString() {
            return "\nwaterLogLC50FM : " + waterLogLC50FM +
                    "\nwaterLogLC50DM : " + waterLogLC50DM +
                    "\nratLogLD50 : " + ratLogLD50 +
                    "\nwaterLogWS : " + waterLogWS +
                    "\nwaterLogBFC : " + waterLogBFC +
                    "\nairUrbanEUAc : " + airEUAc +
                    "\nairUrbanEUAnc : " + airEUAnc +
                    "\nairRuralERAc : " + airERAc +
                    "\nairRuralERAnc : " + airERAnc;
        }

        String waterAsCsv() {
            return String.format("%s,%s,%s,%s,%s", waterLogLC50FM, waterLogLC50DM, ratLogLD50, waterLogWS, waterLogBFC);
        }

        public String waterAsTsv() {
            return String.format("%s%s%s%2$s%s%2$s%s%2$s%s", waterLogLC50FM, "\t", waterLogLC50DM, ratLogLD50, waterLogWS, waterLogBFC);
        }

        String airAsCsv() {
            return String.format("%s,%s,%s,%s", airEUAc, airEUAnc, airERAc, airERAnc);
        }

    }
}
