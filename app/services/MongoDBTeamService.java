package services;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.typesafe.config.Config;
import models.BrainstormingTeam;
import models.Participant;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import play.Logger;

import javax.inject.Inject;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBTeamService {

    @Inject
    private Config config;

    private MongoClient mongoClient;
    private MongoDatabase database;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<BrainstormingTeam> teamCollection;

    public MongoDBTeamService() {
        /*
        String username = config.getString("db.default.username");
        String password = config.getString("db.default.password");
        String host = config.getString("db.default.host");
        String port = config.getString("db.default.port");
        String db = config.getString("play.db.default.database");
        */

        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://play:Methode746@localhost:40002/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase("Test");
        database = database.withCodecRegistry(pojoCodecRegistry);

        teamCollection = database.getCollection("BrainstormingTeam", BrainstormingTeam.class);
    }

    public void insertTeam(BrainstormingTeam team){
        teamCollection.insertOne(team, (result, t) -> Logger.info("Team successfully inserted"));
    }

    public void changeTeamMembers(BrainstormingTeam team, Number number ){
        teamCollection.updateOne(eq("identifier", team.getIdentifier()),combine(set("participants", team.getParticipants()), inc("currentNrOfParticipants", number)), (result, t) -> Logger.info(result.getModifiedCount() + " Team successfully updated"));
    }

    public CompletableFuture<DeleteResult> deleteTeam(BrainstormingTeam team){
        CompletableFuture<DeleteResult> future = new CompletableFuture<>();

        teamCollection.deleteOne(and(   eq("identifier", team.getIdentifier()),
                                        eq("moderator.username", team.getModerator().getUsername()),
                                        eq("moderator.password", team.getModerator().getPassword())), (result, t) -> {
                                            Logger.info(result.getDeletedCount() + " Team successfully deleted");
                                            future.complete(result);
                                        });
        return future;
    }

    public CompletableFuture<BrainstormingTeam> getTeam(String id){
        CompletableFuture<BrainstormingTeam> future = new CompletableFuture<>();

        teamCollection.find(eq("identifier", id)).first((team, t) -> {
            if (team != null) {
                Logger.info("Team successfully found");
                future.complete(team);
            } else {
                future.complete(null);
            }
        });

        return future;
    }

    public CompletableFuture<Queue<BrainstormingTeam>> getAllTeamsOfParticipant(Participant participant){
        CompletableFuture<Queue<BrainstormingTeam>> future = new CompletableFuture<>();
        Queue<BrainstormingTeam> queue = new ConcurrentLinkedQueue<>();

        teamCollection.find(eq("participants.username", participant.getUsername())).forEach(
                team -> queue.add(team), (result, t) -> {
                    Logger.info("Get all BrainstormingTeams for participant!");
                    future.complete(queue);
                });

        return future;
    }


}
