package services;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import config.MongoDBEngineProvider;
import models.bo.BrainstormingTeam;
import models.bo.Participant;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

public class MongoDBTeamService implements DBTeamInterface {

    private MongoDBEngineProvider mongoDBProvider;
    MongoCollection<BrainstormingTeam> teamCollection;

    @Inject
    public MongoDBTeamService(MongoDBEngineProvider mongoDBEngineProvider) {
        this.mongoDBProvider = mongoDBEngineProvider;
        this.teamCollection = mongoDBProvider.getDatabase().getCollection("BrainstormingTeam", BrainstormingTeam.class);
    }

    public void insertTeam(BrainstormingTeam team){
        teamCollection.insertOne(team, (result, t) -> Logger.info("Team successfully inserted"));
    }

    public void changeTeamMembers(BrainstormingTeam team, Number number ){
        teamCollection.updateOne(eq("identifier", team.getIdentifier()),combine(set("participants", team.getParticipants()), inc("currentNrOfParticipants", number)), (result, t) -> Logger.info(result.getModifiedCount() + " Team successfully updated"));
    }

    public CompletableFuture<Long> deleteTeam(BrainstormingTeam team){
        CompletableFuture<Long> future = new CompletableFuture<>();

        teamCollection.deleteOne(and(   eq("identifier", team.getIdentifier()),
                                        eq("moderator.username", team.getModerator().getUsername()),
                                        eq("moderator.password", team.getModerator().getPassword())), (result, t) -> {
                                            Logger.info(result.getDeletedCount() + " Team successfully deleted");
                                            future.complete(result.getDeletedCount());
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
