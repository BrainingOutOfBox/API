package services;

import com.mongodb.async.client.MongoCollection;
import config.MongoDBEngineProvider;
import models.bo.Participant;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class MongoDBParticipantService implements DBParticipantInterface {

    private MongoDBEngineProvider mongoDBProvider;
    MongoCollection<Participant> participantCollection;

    @Inject
    public MongoDBParticipantService(MongoDBEngineProvider mongoDBEngineProvider) {
        this.mongoDBProvider = mongoDBEngineProvider;
        this.participantCollection = mongoDBProvider.getDatabase().getCollection("Participant", Participant.class);
    }

    public CompletableFuture<Participant> getParticipant(String username, String password){
        CompletableFuture<Participant> future = new CompletableFuture<>();

        participantCollection.find(and( eq("username", username),
                eq("password", password))).first((participant, t) -> {
                    if (participant != null) {
                        Logger.info("Found participant");
                        future.complete(participant);
                    } else {
                        future.complete(null);
                    }
                });

        return future;
    }

    public CompletableFuture<Participant> getParticipant(String username){
        CompletableFuture<Participant> future = new CompletableFuture<>();

        participantCollection.find(eq("username", username)).first((participant, t) -> {
            if (participant != null) {
                Logger.info("Found participant");
                future.complete(participant);
            } else {
                future.complete(null);
            }
        });

        return future;
    }

    public void insertParticipant(Participant participant){
        participantCollection.insertOne(participant, (result, t) -> Logger.info("Participant successfully inserted"));
    }

    public CompletableFuture<Long> deleteParticipant(Participant participant) {
        CompletableFuture<Long> future = new CompletableFuture<>();

        participantCollection.deleteOne(and( eq("username", participant.getUsername()),
                                             eq("password", participant.getPassword())), (result, t) -> {
                                                 Logger.info(result.getDeletedCount() + " Participant successfully deleted");
                                                 future.complete(result.getDeletedCount());
                                             });

        return future;
    }

}
