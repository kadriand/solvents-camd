package co.unal.camd.methods.hukkerikar;

import co.unal.camd.model.molecule.UnifacGroupNode;
import lombok.Data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class HukkerikarFirstOrderContribution {
    private int code;
    private int unifacCode;
    private String groupName;

    private ThermoPhysical thermoPhysical = new ThermoPhysical();
    private Environmental environmental = new Environmental();

    /**
     * <UNIFAC codes Contributions nodes , HukkerikarSecondOrderContribution containing this HukkerikarFirstOrderContribution >
     */
    private Map<UnifacGroupNode, HukkerikarSecondOrderContribution> secondOrderContributions = new HashMap<>();

    public HukkerikarFirstOrderContribution(int code) {
        this.code = code;
    }

    public HukkerikarFirstOrderContribution(int code, int unifacCode) {
        this.code = code;
        this.unifacCode = unifacCode;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "First Order Group Contributions of %s [%d]. \nEnvironmental : %s\nThermo-Physical  : %s",
                groupName, code, environmental, thermoPhysical);
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
