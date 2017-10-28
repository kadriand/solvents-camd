package co.unal.camd.availability;

import co.unal.camd.ga.haea.MoleculeSpace;
import co.unal.camd.model.molecule.Molecule;
import co.unal.camd.view.CamdRunner;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AvailabilityFinder extends MongodbClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongodbClient.class);
    private MongoCollection<Document> compoundsCollection;

    public AvailabilityFinder() {
        super(COMPOUNDS_DATABASE_NAME);
        LOGGER.info("\n");
        LOGGER.info("AVAILABLE COLLECTIONS");
        database.listCollectionNames().forEach((Consumer<? super String>) LOGGER::info);

        String compoundsCollectionName = COMPOUNDS_COLLECTION_NAME;
        boolean existCollection = database.listCollectionNames().into(new ArrayList<>()).stream().anyMatch(compoundsCollectionName::equalsIgnoreCase);
        if (!existCollection)
            createCollection(compoundsCollectionName);

        compoundsCollection = getCollection(compoundsCollectionName);
        LOGGER.info("\n");
        LOGGER.info("Collection name: {}", compoundsCollectionName);
        LOGGER.info("Collection size: {}", compoundsCollection.count());
        LOGGER.info("AVAILABLE INDEXES ");
        for (Document index : compoundsCollection.listIndexes())
            LOGGER.info(index.toJson());
    }

    public List<CompoundEntry> findCompound(Molecule molecule) {
        ArrayList<CompoundEntry> compounds = new ArrayList<>();
        String smiles = molecule.getSmiles();
        try {
            List<Document> documents = filterBySmiles(smiles, compoundsCollection, false);
            LOGGER.debug("Search term: {} <> Results: {}", smiles, documents.size());
            for (Document filteredDocument : documents) {
                LOGGER.debug(filteredDocument.toJson());
                CompoundSource source = CompoundSource.valueOf(filteredDocument.get("source").toString());
                CompoundEntry compound;
                if (CompoundSource.EPA == source)
                    compound = deserializeDocument(filteredDocument, CompoundEntry.EPACompound.class);
                else
                    compound = deserializeDocument(filteredDocument, CompoundEntry.ZincCompound.class);
                LOGGER.debug("{} > {}", compound.getId(), compound.getSmiles());
                compounds.add(compound);
            }
        } catch (Exception e) {
            LOGGER.error("Problems collecting database entries for {}", smiles, e);
        }
        return compounds;
    }

    public static void main(String... args) {
        try {
            MongodbClient.IS_DB_ENABLE = true;
            CamdRunner.CONTRIBUTION_GROUPS.defaultFamilyProbabilities();
            for (int i = 0; i < 100; i++) {
                Molecule molecule = MoleculeSpace.randomMolecule();
                CamdRunner.AVAILABILITY_FINDER.findCompound(molecule);
            }
        } finally {
            CamdRunner.AVAILABILITY_FINDER.close();
        }
    }

}
