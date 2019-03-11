package co.unal.camd.availability;

import co.unal.camd.descriptors.CdkUtils;
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

public class ZincTranchesInsertAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZincTranchesInsertAgent.class);

    private static final String SPLIT_SMI_LINE_REGEX = "\\s";
    private static final String DATABASE_NAME = "availability-bank";
    private static final String COMPOUNDS_COLLECTION_NAME = "compounds";

    /*DISABLED BY DEFAULT*/
    private static final CompoundSource compoundSource = null;
    private static final ZincTranchesInsertAgent.TrancheBase tranchesBase = null;

    private File trancheFile;
    private File trancheReportFile;

    public ZincTranchesInsertAgent(File trancheFile, File baseDirectory) {
        this.trancheFile = trancheFile;

        try {
            String trancheLocation = this.trancheFile.getCanonicalPath().replace(baseDirectory.getParentFile().getCanonicalPath(), "").replaceAll("\\\\", "/");
            LOGGER.info("\n\n");
            LOGGER.info("NEW TRANCHE FILE {}", trancheFile.getName());
            LOGGER.info("Preparing to insert entries of tranche file: " + trancheLocation);
            trancheReportFile = new File("mongo/import-report" + trancheLocation + ".failed.smi");
            LOGGER.info("Tranche report file in: " + trancheLocation + ".failed.smi");
        } catch (Exception e) {
            LOGGER.error("NO REPORT FILE. BAD THING");
            e.printStackTrace();
        }
    }

    public static void main(String... args) throws Exception {
        MongodbClient.IS_DB_ENABLE = true;
        readTranchesDirectory();
    }

    private static void readTranchesDirectory() throws IOException {
        try (MongodbClient dbClient = new MongodbClient(DATABASE_NAME)) {
            LOGGER.info("\n");
            LOGGER.info("AVAILABLE COLLECTIONS");
            dbClient.getDatabase().listCollectionNames().forEach((Consumer<? super String>) LOGGER::info);

            boolean existCollection = dbClient.getDatabase().listCollectionNames().into(new ArrayList<>()).stream().anyMatch(COMPOUNDS_COLLECTION_NAME::equalsIgnoreCase);
            if (!existCollection)
                dbClient.createCollection(COMPOUNDS_COLLECTION_NAME);

            MongoCollection<Document> compoundsCollection = dbClient.getCollection(COMPOUNDS_COLLECTION_NAME);

            LOGGER.info("\n");
            LOGGER.info("AVAILABLE INDEXES ");
            for (Document index : compoundsCollection.listIndexes())
                LOGGER.info(index.toJson());

            File baseFile = new File(tranchesBase.targetBasePath);
            File overallReportFile = new File("mongo/import-report/" + compoundSource.toString().replace("_", "-") + ".report.info");
            FileUtils.touch(overallReportFile);

            Collection<File> tranchesFiles = FileUtils.listFiles(baseFile, new String[]{"smi"}, true);
            tranchesFiles.forEach(trancheFile -> {
                try {
                    Files.write(overallReportFile.toPath(), ("\n[" + Instant.now() + "] PROCESSING TRANCHE FILE : " + trancheFile.getCanonicalPath()).getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
                    ZincTranchesInsertAgent zincTranchesInsertAgent = new ZincTranchesInsertAgent(trancheFile, baseFile);
                    zincTranchesInsertAgent.insertCompounds(compoundsCollection);
                } catch (IOException e) {
                    LOGGER.error("Problems inserting compounds of {}", trancheFile, e);
                }
            });
        }
    }

    private void insertCompounds(MongoCollection<Document> compoundsCollection) {
        try {
            ArrayList<Document> compoundBulkDocuments = new ArrayList<>();
            LOGGER.info("compoundsCollection before insertion : " + compoundsCollection.count());
            try (Stream<String> stream = Files.lines(trancheFile.toPath())) {
                stream.forEach(entryLine -> storeCompound(compoundsCollection, compoundBulkDocuments, entryLine));
                LOGGER.info("compoundBulkDocuments to insert: " + compoundBulkDocuments.size());
                if (compoundBulkDocuments.size() > 0)
                    compoundsCollection.insertMany(compoundBulkDocuments, new InsertManyOptions().ordered(false));
            }
            LOGGER.info("compoundsCollection after insertion : " + compoundsCollection.count());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            try {
                FileUtils.touch(trancheReportFile);
                Files.write(trancheReportFile.toPath(), "Please check the whole file, something really bad happened\n".getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
            } catch (IOException e1) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void storeCompound(MongoCollection<Document> compoundsCollection, ArrayList<Document> compoundBulkDocuments, String entryLine) {
        try {
            String[] zincCompoundValues = entryLine.split(SPLIT_SMI_LINE_REGEX);
            if (zincCompoundValues.length != 2)
                throw new Exception();
            String smiles = CdkUtils.smilesToUniqueUnsafe(zincCompoundValues[0]);

            Document zincDocument = new Document()
                    .append("smiles", smiles)
                    .append("zincId", zincCompoundValues[1])
                    .append("source", compoundSource.toString());
            compoundBulkDocuments.add(zincDocument);

            if (compoundBulkDocuments.size() < 30000)
                return;

            LOGGER.info("compoundBulkDocuments for insertion: " + compoundBulkDocuments.size());
            if (compoundBulkDocuments.size() > 0)
                compoundsCollection.insertMany(compoundBulkDocuments, new InsertManyOptions().ordered(false));
            compoundBulkDocuments.clear();
        } catch (Exception e) {
            try {
                if (entryLine.matches(".*(\\W|^)(SMILES|smiles)(\\W|$).*"))
                    return;
                LOGGER.error(e.getMessage());
                if (!trancheReportFile.exists())
                    FileUtils.touch(trancheReportFile);
                if (e.getMessage() != null)
                    Files.write(trancheReportFile.toPath(), ("<MSG>" + e.getMessage().replaceAll("\\n", ". ") + "<MSG>\n").getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
                Files.write(trancheReportFile.toPath(), (entryLine + "\n").getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
            } catch (IOException e1) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    enum TrancheBase {
        TESTS("mongo/insert-tests/"),
        WAIT_OK("../data-bases/zinc-tranches/wait-ok/"),
        BOUTIQUE("../data-bases/zinc-tranches/boutique/"),
        ANNOTATED("../data-bases/zinc-tranches/annotated/");

        private final String targetBasePath;

        TrancheBase(String targetBasePath) {
            this.targetBasePath = targetBasePath;
        }
    }

}