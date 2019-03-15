package services;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.PushOptions;
import config.MongoDBEngineProvider;
import models.Brainsheet;
import models.BrainstormingFinding;
import models.BrainstormingTeam;
import org.joda.time.DateTime;
import play.Logger;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Updates.pushEach;

public class MongoDBFindingService {

    private MongoDBEngineProvider mongoDBProvider;
    MongoCollection<BrainstormingFinding> findingCollection;

    @Inject
    public MongoDBFindingService(MongoDBEngineProvider mongoDBEngineProvider) {
        this.mongoDBProvider = mongoDBEngineProvider;
        this.findingCollection = mongoDBProvider.getDatabase().getCollection("BrainstormingFinding", BrainstormingFinding.class);
    }


    public void insertFinding(BrainstormingFinding finding){
        findingCollection.insertOne(finding, (result, t) -> Logger.info("BrainstormFinding successfully inserted"));
    }

    public CompletableFuture<BrainstormingFinding> getFinding(String id){
        CompletableFuture<BrainstormingFinding> future = new CompletableFuture<>();

        findingCollection.find(eq("identifier", id)).first((result, t) -> {
            Logger.info("BrainstormingFinding successfully found");
            future.complete(result);
        });

        return future;
    }

    public CompletableFuture<Queue<BrainstormingFinding>> getAllFindingsOfTeam(BrainstormingTeam team){
        CompletableFuture<Queue<BrainstormingFinding>> future = new CompletableFuture<>();
        Queue<BrainstormingFinding> queue = new ConcurrentLinkedQueue<>();

        findingCollection.find(eq("brainstormingTeam", team.getIdentifier())).forEach(
                finding -> queue.add(finding), (result, t) -> {
                    Logger.info("BrainstormingFindings for team successfully found");
                    future.complete(queue);
                });

        return future;
    }

    public void exchangeBrainsheet(BrainstormingFinding finding, Brainsheet oldBrainsheet, Brainsheet newBrainsheet){
        //delete old Brainsheet
        findingCollection.updateOne(eq("identifier", finding.getIdentifier()),pullByFilter(Filters.eq("brainsheets", oldBrainsheet)), (result, t) -> Logger.info(result.getModifiedCount() + " old Brainsheet successfully deleted"));
        //insert new Brainsheet at the same place
        findingCollection.updateOne(eq("identifier", finding.getIdentifier()),combine(pushEach("brainsheets", Arrays.asList(newBrainsheet), new PushOptions().position(newBrainsheet.getNrOfSheet())), inc("deliveredBrainsheetsInCurrentRound", 1)), (result, t) -> Logger.info(result.getModifiedCount() + " new Brainsheet successfully inserted"));
    }

    public void nextRound(BrainstormingFinding finding){
        findingCollection.updateOne(eq("identifier", finding.getIdentifier()), combine(set("currentRoundEndTime", new DateTime().plusMinutes(finding.getBaseRoundTime()+finding.getCurrentRound()).toString()), inc("currentRound", 1), set("deliveredBrainsheetsInCurrentRound", 0)), (result, t) -> Logger.info(result.getModifiedCount() + " BrainstormingFinding successfully updated"));
    }

    public void lastRound(BrainstormingFinding finding){
        findingCollection.updateOne(eq("identifier", finding.getIdentifier()), set("currentRound", -1), (result, t) -> Logger.info(result.getModifiedCount() + " BrainstormingFinding successfully updated"));
    }




}
