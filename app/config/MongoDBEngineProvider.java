package config;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import com.typesafe.config.Config;
import models.bo.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Singleton
public class MongoDBEngineProvider {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private CodecRegistry pojoCodecRegistry;

    @Inject
    public MongoDBEngineProvider(Config config) {

        String url  = config.getString("db.mongo.url");
        int port = config.getInt("db.mongo.port");
        String user = config.getString("db.mongo.username");
        String password = config.getString("db.mongo.password");
        String db = config.getString("db.mongo.database");

        ClassModel<Idea> ideaClassModel = ClassModel.builder(Idea.class).enableDiscriminator(true).build();
        ClassModel<NoteIdea> noteIdeaClassModel = ClassModel.builder(NoteIdea.class).enableDiscriminator(true).build();
        ClassModel<SketchIdea> sketchIdeaClassModel = ClassModel.builder(SketchIdea.class).enableDiscriminator(true).build();
        ClassModel<PatternIdea> patternIdeaClassModel = ClassModel.builder(PatternIdea.class).enableDiscriminator(true).build();

        ClassModel<Brainwave> brainwaveClassModel = ClassModel.builder(Brainwave.class).build();
        ClassModel<Brainsheet> brainsheetClassModel = ClassModel.builder(Brainsheet.class).build();
        ClassModel<BrainstormingFinding> brainstormingFindingClassModel = ClassModel.builder(BrainstormingFinding.class).build();

        ClassModel<Participant> participantClassModel = ClassModel.builder(Participant.class).build();
        ClassModel<BrainstormingTeam> teamClassModel = ClassModel.builder(BrainstormingTeam.class).build();

        PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(ideaClassModel, noteIdeaClassModel, sketchIdeaClassModel, patternIdeaClassModel, brainwaveClassModel, brainsheetClassModel, brainstormingFindingClassModel, participantClassModel, teamClassModel).build();

        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://"+user+":"+password+"@"+url+":"+port+"/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase(db);
        database = database.withCodecRegistry(pojoCodecRegistry);
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
