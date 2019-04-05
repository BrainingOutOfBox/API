package services.database;

import com.google.inject.ImplementedBy;
import models.bo.Brainsheet;
import models.bo.BrainstormingFinding;
import models.bo.BrainstormingTeam;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@ImplementedBy(MongoDBFindingService.class)
public interface DBFindingInterface {

    CompletableFuture<BrainstormingFinding> getFinding(String id);

    CompletableFuture<Queue<BrainstormingFinding>> getAllFindingsOfTeam(BrainstormingTeam brainstormingTeam);

    void insertFinding(BrainstormingFinding finding);

    void exchangeBrainsheet(BrainstormingFinding finding, Brainsheet oldBrainsheet, Brainsheet newBrainsheet);

    void nextRound(BrainstormingFinding finding);

    void lastRound(BrainstormingFinding finding);
}
