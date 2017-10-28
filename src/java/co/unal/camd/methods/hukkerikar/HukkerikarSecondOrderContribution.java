package co.unal.camd.methods.hukkerikar;

import co.unal.camd.model.molecule.UnifacGroupNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
public class HukkerikarSecondOrderContribution {
    private int code;
    private String groupDescription;

    private ThermoPhysical thermoPhysical = new ThermoPhysical();
    private Environmental environmental = new Environmental();

    /**
     * Unifac configurations
     */
    private List<UnifacGroupNode> groupConfigurations = new ArrayList<>();

    public HukkerikarSecondOrderContribution(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "First Order Group Contributions of %s [%d]. \nEnvironmental : %s\nThermo-Physical  : %s",
                groupDescription, code, environmental, thermoPhysical);
    }

    @Data
    public static class ThermoPhysical {
        private Double meltingPoint;
        private Double boilingPoint;
        private Double gibbsFreeEnergy;
        private Double liquidMolarVolume;
    }

    @Data
    public static class Environmental {
        private Double waterLC50FM;
        private Double waterLC50DM;
        private Double oralLD50;
        private Double waterLogWS;
        private Double waterBFC;

        private Double airEUAc;
        private Double airEUAnc;
        private Double airERAc;
        private Double airERAnc;
    }

}
