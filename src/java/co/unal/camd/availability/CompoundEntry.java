package co.unal.camd.availability;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Data
@Accessors(chain = true)
public abstract class CompoundEntry {

    @Id
    protected ObjectId id;
    protected String smiles;
    protected CompoundSource source;

    public abstract String itemUrl();

    /***
     * {@link <a href="https://www.epa.gov/chemical-research/distributed-structure-searchable-toxicity-dsstox-database">'Learn more'</a>}
     *
     */
    @Data
    @Accessors(chain = true)
    @Entity
    public static class EPACompound extends CompoundEntry {
        private String commonName;
        private String casRN;
        private String dssToxId;

        @Override
        public String itemUrl() {
            return source.getUrlPrefix() + dssToxId;
        }
    }

    /***
     * {@link <a href="http://wiki.bkslab.org/index.php/Tranche_Browser#Purchasability">'Learn more'</a>}
     *
     */
    @Data
    @Accessors(chain = true)
    @Entity
    public static class ZincCompound extends CompoundEntry {
        private String zincId;

        @Override
        public String itemUrl() {
            return source.getUrlPrefix() + zincId;
        }
    }

}
