package co.unal.camd.utils;

import co.unal.camd.model.molecule.Molecule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MoleculeUnifacValidator {

    @Setter
    @JsonIgnore
    private static Molecule waterMolecule;

    private int index;
    private String name;

    private String configuration;
    private String casNumber;

    private double molecularWeight;
    private double computedMolecularWeight;

    private double lnComputedActivity;
    private double lnExperimentalActivity;
    private double lnRecomputedActivity;

    private String smiles;
    private String inChIKey;

    @JsonIgnore
    private Molecule molecule;

    void recompute() {
        this.computedMolecularWeight = molecule.getMolecularWeight();
        this.lnRecomputedActivity = Math.log(molecule.getMixtureProperties().getInfiniteDilution(waterMolecule).getActivityCoefficient());
    }

    void buildIdentifiers() {
        this.smiles = molecule.getSmiles();
        this.inChIKey = molecule.getInChIKey();
        this.computedMolecularWeight = molecule.getMolecularWeight();
    }

    @Override
    public String toString() {
        return index + "\n" + name;
    }

    String asCsv() {
        return String.format("%s,\"%s\",%s,\"%s\",%s,%s,%s,%s,%s", index, name, casNumber, configuration, molecularWeight, computedMolecularWeight, lnComputedActivity, lnExperimentalActivity, lnRecomputedActivity);
    }

    String identifiersCsv() {
        return String.format("%s,\"%s\",%s,%s,%s,%s", index, name, configuration, this.computedMolecularWeight, this.smiles, this.inChIKey);
    }
}
