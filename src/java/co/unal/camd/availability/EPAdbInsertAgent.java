package co.unal.camd.availability;

import co.unal.camd.utils.CdkUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class EPAdbInsertAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(EPAdbInsertAgent.class);

    private static final String SPLIT_CSV_LINE_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String DATABASE_NAME = "availability-bank";
    private static final String COMPOUNDS_COLLECTION_NAME = "compounds";

    /*DISABLED BY DEFAULT*/
    private static final CompoundSource compoundSource = null;
    private static final EPABase epaBase = null;

    private File epaFile;

    /**
     * use ^[^F^<].*$ regex to filter not FAIL smails in reports
     */
    private File epaReportFile;

    public EPAdbInsertAgent(File epaEntriesFile, File baseDirectory) {
        this.epaFile = epaEntriesFile;

        try {
            String epaEntriesLocation = this.epaFile.getCanonicalPath().replace(baseDirectory.getParentFile().getCanonicalPath(), "").replaceAll("\\\\", "/");
            LOGGER.info("\n\n");
            LOGGER.info("NEW EPA FILE {}", epaFile.getName());
            LOGGER.info("Preparing to insert entries of epa file: " + epaEntriesLocation);
            epaReportFile = new File("mongo/import-report" + epaEntriesLocation + ".failed.csv");
            LOGGER.info("EPA file report file in: " + epaEntriesLocation + ".failed.csv");
        } catch (Exception e) {
            LOGGER.error("NO REPORT FILE. BAD THING");
            LOGGER.error(e.getMessage());
        }
    }

    public static void main(String... args) throws Exception {
        MongodbClient.IS_DB_ENABLE = true;
        readTEpaEntriesDirectory();
    }

    private static void readTEpaEntriesDirectory() throws IOException {
        try (MongodbClient dbClient = new MongodbClient(DATABASE_NAME)) {
            LOGGER.info("\n");
            LOGGER.info("AVAILABLE COLLECTIONS");
            dbClient.getDatabase().listCollectionNames().forEach((Consumer<? super String>) LOGGER::info);

            boolean existCollection = dbClient.getDatabase().listCollectionNames().into(new ArrayList<>()).stream().anyMatch(COMPOUNDS_COLLECTION_NAME::equalsIgnoreCase);
            if (!existCollection)
                dbClient.createCollection(COMPOUNDS_COLLECTION_NAME);

            MongoCollection<Document> compoundsCollection = dbClient.getCollection(COMPOUNDS_COLLECTION_NAME);

            LOGGER.info("\n");
            LOGGER.info("AVAILABLE INDEXES");
            for (Document index : compoundsCollection.listIndexes())
                LOGGER.info(index.toJson());

            File baseFile = new File(epaBase.targetBasePath);
            File overallReportFile = new File("mongo/import-report/" + compoundSource.toString().replace("_", "-") + ".report.info");
            FileUtils.touch(overallReportFile);

            Collection<File> epaFiles = FileUtils.listFiles(baseFile, new String[]{"csv"}, true);
            epaFiles.forEach(epaFile -> {
                try {
                    Files.write(overallReportFile.toPath(), ("\n[" + Instant.now() + "] PROCESSING EPA FILE : " + epaFile.getCanonicalPath()).getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
                    EPAdbInsertAgent epadbInsertAgent = new EPAdbInsertAgent(epaFile, baseFile);
                    epadbInsertAgent.insertCompounds(compoundsCollection);
                } catch (IOException e) {
                    LOGGER.error("Problems inserting compounds of {}", epaFile, e);
                }
            });
        }
    }

    private void insertCompounds(MongoCollection<Document> compoundsCollection) {
        try {
            ArrayList<Document> compoundBulkDocuments = new ArrayList<>();
            LOGGER.info("compoundsCollection before insertion : " + compoundsCollection.count());
            try (Stream<String> stream = Files.lines(epaFile.toPath())) {
                stream.forEach(entryLine -> storeCompound(compoundsCollection, compoundBulkDocuments, entryLine));
                LOGGER.info("compoundBulkDocuments : " + compoundBulkDocuments.size());
                if (compoundBulkDocuments.size() > 0)
                    compoundsCollection.insertMany(compoundBulkDocuments, new InsertManyOptions().ordered(false));
            }
            LOGGER.info("compoundsCollection after insertion : " + compoundsCollection.count());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            try {
                FileUtils.touch(epaReportFile);
                Files.write(epaReportFile.toPath(), "Please check the whole file, something really bad happened\n".getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
            } catch (IOException e1) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void storeCompound(MongoCollection<Document> compoundsCollection, ArrayList<Document> compoundBulkDocuments, String entryLine) {
        try {
            String[] epaCompoundValues = entryLine.split(SPLIT_CSV_LINE_REGEX);
            if (epaCompoundValues.length != 4)
                throw new Exception();
            String smiles = CdkUtils.smilesToUniqueUnsafe(epaCompoundValues[0]);

            Document epaDocument = new Document()
                    .append("smiles", smiles)
                    .append("commonName", epaCompoundValues[1])
                    .append("casRN", epaCompoundValues[2])
                    .append("dssToxId", epaCompoundValues[3])
                    .append("source", compoundSource.toString());
            compoundBulkDocuments.add(epaDocument);

            if (compoundBulkDocuments.size() < 30000)
                return;

            LOGGER.info("compoundBulkDocuments " + compoundBulkDocuments.size());
            if (compoundBulkDocuments.size() > 0)
                compoundsCollection.insertMany(compoundBulkDocuments, new InsertManyOptions().ordered(false));
            compoundBulkDocuments.clear();
        } catch (Exception e) {
            try {
                if (entryLine.matches(".*(\\W|^)(SMILES|smiles)(\\W|$).*"))
                    return;
                LOGGER.error(e.getMessage());
                if (!epaReportFile.exists())
                    FileUtils.touch(epaReportFile);
                if (e.getMessage() != null)
                    Files.write(epaReportFile.toPath(), ("<MSG>" + e.getMessage().replaceAll("\\n", ". ") + "<MSG>\n").getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
                Files.write(epaReportFile.toPath(), (entryLine + "\n").getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
            } catch (IOException e1) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    enum EPABase {
        TESTS("mongo/insert-tests/"),
        EPA("../data-bases/epa/DSSTOX_MS_Ready_Chemical_Structures/");

        private final String targetBasePath;

        EPABase(String targetBasePath) {
            this.targetBasePath = targetBasePath;
        }
    }

}