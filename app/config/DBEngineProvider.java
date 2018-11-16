package config;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import com.typesafe.config.Config;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Singleton
public class DBEngineProvider {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private CodecRegistry pojoCodecRegistry;

    @Inject
    public DBEngineProvider(Config config) {

        String url  = config.getString("db.mongo.url");
        int port = config.getInt("db.mongo.port");
        String user = config.getString("db.mongo.username");
        String password = config.getString("db.mongo.password");
        String db = config.getString("db.mongo.database");

        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://"+user+":"+password+"@"+url+":"+port+"/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase(db);
        database = database.withCodecRegistry(pojoCodecRegistry);
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
