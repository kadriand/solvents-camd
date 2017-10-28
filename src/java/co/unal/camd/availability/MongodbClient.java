package co.unal.camd.availability;

import co.unal.camd.utils.CdkUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static co.unal.camd.availability.CompoundSource.EPA;
import static co.unal.camd.availability.CompoundSource.ZINC_WAITOK;

public class MongodbClient implements Closeable {

    @Getter
    MongoDatabase database;
    private MongodExecutable mongodExecutable;
    private MongodProcess mongod;
    private MongoClient mongoClient;
    private Datastore dataStore;
    private Morphia morphia;

    /**
     * VARIABLE
     */
    private static final String ROOT_DIR = "/GoogleDrive/UNAL/Maestría/TRABAJO FINAL/Java/CAMD-MO/";
    //    private static final String ROOT_DIR = "/MOI/solvents-camd/";
    private static final String DATABASE_DIR = "mongo/compounds-bank-db";
    private static final String DATABASE_CONFIG_DIR = "mongo/compounds-bank-db-config";
    private static final String[] DATABASE_CORE_DATA = new String[]{"collection-5-7515323141007407276.wt", "index-0--8962042801859952466.wt", "index-6-7515323141007407276.wt"};

    private static final String SMILES_FIELD = "smiles";
    public static final String COMPOUNDS_COLLECTION_NAME = "compounds";
    public static final String COMPOUNDS_DATABASE_NAME = "availability-bank";

    public static boolean IS_DB_ENABLE;
    private static final Logger LOGGER = LoggerFactory.getLogger(MongodbClient.class);

    public MongodbClient(String databaseName) {
        this(DATABASE_DIR, databaseName);
    }

    /**
     * @param databaseDirPath mongo location
     * @param databaseName    Creates it if it doesn't exist
     * @throws IOException
     */
    public MongodbClient(String databaseDirPath, String databaseName) {
        try {
            File databaseDirectory = prepareDatabaseDirectory(databaseDirPath);

            MongodStarter starter = MongodStarter.getDefaultInstance();
            String bindIp = "localhost";
            int port = 12345;
            Storage replication = new Storage(databaseDirectory.getCanonicalPath(), null, 0);
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    // .version(Version.MainGroup.PRODUCTION)
                    .version(Version.Main.V3_5) // TO USE IN WORKSPACES
                    .replication(replication)
                    .setParameter("failIndexKeyTooLong", "false")
                    .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                    .build();

            mongodExecutable = starter.prepare(mongodConfig);
            mongod = mongodExecutable.start();
            mongoClient = new MongoClient(bindIp, port);
            database = mongoClient.getDatabase(databaseName);

            morphia = new Morphia();
            morphia.map(CompoundEntry.ZincCompound.class);
            morphia.map(CompoundEntry.EPACompound.class);
            dataStore = morphia.createDatastore(mongoClient, database.getName());

            try (MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator()) {
                LOGGER.info("\n");
                LOGGER.info("AVAILABLE DATABASES");
                while (dbsCursor.hasNext())
                    LOGGER.info(dbsCursor.next());
            }
        } catch (UnknownHostException e) {
            LOGGER.error("Unknown host ", e);
        } catch (IOException e) {
            LOGGER.error("Problems starting DB ", e);
        }
    }

    /**
     * Inside the database directory, delete the configuration files, excluding the core files (data, indexes)
     * Copies the configuration files of directory co.unal.camd.availability.MongodbClient#DATABASE_CONFIG_DIR into the database directory
     *
     * @param databaseDirPath database directory
     */
    private File prepareDatabaseDirectory(String databaseDirPath) {
        try {
            LOGGER.info("Restoring database configuration files");
            File databaseDir = new File(ROOT_DIR + databaseDirPath);
            LOGGER.info("DB files in : {}", databaseDir.getCanonicalPath());

            if (!databaseDir.exists())
                return null;
            for (File file : databaseDir.listFiles())
                if (!ArrayUtils.contains(DATABASE_CORE_DATA, file.getName()) && file.length() < 100000000)
                    FileUtils.forceDelete(file);

            File databaseConfigDir = new File(ROOT_DIR + DATABASE_CONFIG_DIR);
            LOGGER.info("DB configuration files in : {}", databaseConfigDir.getCanonicalPath());
            FileUtils.copyDirectory(databaseConfigDir, databaseDir);
            return databaseDir;
        } catch (Exception e) {
            LOGGER.error("Problems clearing database directory {}", databaseDirPath, e);
            return null;
        }
    }

    public MongoCollection<Document> createCollection(String collectionName) {
        LOGGER.info("Crating collection ", collectionName);
        Bson smilesExists = Filters.exists(SMILES_FIELD);
        Bson validator = Filters.and(smilesExists);
        ValidationOptions validationOptions = new ValidationOptions()
                .validator(validator);
        database.createCollection(collectionName, new CreateCollectionOptions()
                .validationOptions(validationOptions));

        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.createIndex(Indexes.ascending(SMILES_FIELD));

        return collection;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection;
    }

    public boolean isRunning() {
        return mongod != null && mongod.isProcessRunning();
    }

    public void insertDocument(Document document, String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(document);
    }

    public List<Document> filterBySmiles(String smiles, MongoCollection<Document> collection, boolean regexMatch) {
        smiles = CdkUtils.smilesToUnique(smiles);
        List<Document> documents = new ArrayList<>();
        Block<Document> toListBlock = document -> documents.add(document);

        if (regexMatch)
            collection.find(Filters.regex(SMILES_FIELD, smiles)).forEach(toListBlock);
        else
            collection.find(Filters.eq(SMILES_FIELD, smiles)).forEach(toListBlock);

        return documents;
    }

    public <T> T deserializeDocument(Document document, final Class<T> entityClass) {
        T pojo = morphia.fromDBObject(dataStore, entityClass, new BasicDBObject(document));
        return pojo;
    }

    public static void main(String... args) {
        try (MongodbClient mongodbClient = new MongodbClient(COMPOUNDS_DATABASE_NAME)) {
            LOGGER.info("\n");
            LOGGER.info("AVAILABLE COLLECTIONS");
            mongodbClient.getDatabase().listCollectionNames().forEach((Consumer<? super String>) LOGGER::info);

            String compoundsCollectionName = COMPOUNDS_COLLECTION_NAME;
            boolean existCollection = mongodbClient.getDatabase().listCollectionNames().into(new ArrayList<>()).stream().anyMatch(compoundsCollectionName::equalsIgnoreCase);
            if (!existCollection)
                mongodbClient.createCollection(compoundsCollectionName);

            MongoCollection<Document> compoundsCollection = mongodbClient.getCollection(compoundsCollectionName);
            LOGGER.info("\n");
            LOGGER.info("Collection name: {}", compoundsCollectionName);
            LOGGER.info("Collection size: {}", compoundsCollection.count());
            LOGGER.info("AVAILABLE INDEXES ");
            for (Document index : compoundsCollection.listIndexes())
                LOGGER.info(index.toJson());

            String testSmiles = "CCC(C)(CBr)C";
            LOGGER.info("Searching {}", testSmiles);
            List<Document> documents = mongodbClient.filterBySmiles(testSmiles, compoundsCollection, false);
            LOGGER.info("Results size: {}", documents.size());
            for (Document filteredDocument : documents) {
                LOGGER.info(filteredDocument.toJson());
                if (filteredDocument.toJson().contains(ZINC_WAITOK.toString())) {
                    CompoundEntry.ZincCompound zincCompound = mongodbClient.deserializeDocument(filteredDocument, CompoundEntry.ZincCompound.class);
                    LOGGER.info(zincCompound.getSmiles());
                    LOGGER.info("{}", zincCompound.getId());
                }
                if (filteredDocument.toJson().contains(EPA.toString())) {
                    CompoundEntry.EPACompound zincCompound = mongodbClient.deserializeDocument(filteredDocument, CompoundEntry.EPACompound.class);
                    LOGGER.info(zincCompound.getSmiles());
                    LOGGER.info("{}", zincCompound.getId());
                }
            }
            LOGGER.info("Search done: {}", testSmiles);
        }
    }

    private void compactCollection(String collectionName) {
        LOGGER.info("COMPACTING");
        database.runCommand(new Document("compact", collectionName));
        LOGGER.info("COMPACTING DONE");
    }

    @Override
    public void close() {
        LOGGER.info("IS RUNNING? {}", isRunning());
        if (mongodExecutable != null)
            mongodExecutable.stop();
        LOGGER.info("IS RUNNING NOW? {}", isRunning());
    }
}
