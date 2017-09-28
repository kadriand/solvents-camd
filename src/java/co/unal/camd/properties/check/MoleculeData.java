package co.unal.camd.properties.check;

import lombok.Data;

@Data
public class MoleculeData {
    private int index;
    private int[] groups;
    private String name;
    private PropertiesSet experimental;
    private PropertiesSet computed;


    @Data
    public static class PropertiesSet {
        private double dielectricConst;
        private double fusionTemp;
        private double boilingTemp;
        private double density;
        private double deltaGibbs;
    }
}
