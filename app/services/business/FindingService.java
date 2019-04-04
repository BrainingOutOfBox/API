package services.business;

import models.bo.Brainsheet;
import models.bo.BrainstormingFinding;
import models.bo.BrainstormingTeam;
import services.database.MongoDBFindingService;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class FindingService {

    @Inject
    private MongoDBFindingService service;

    public void insertFinding(BrainstormingFinding finding){
        service.insertFinding(finding);
    }

    public CompletableFuture<Queue<BrainstormingFinding>> getAllFindingsOfTeam(BrainstormingTeam team){
        return service.getAllFindingsOfTeam(team);
    }

    public CompletableFuture<BrainstormingFinding> getFinding(String id){
        return service.getFinding(id);
    }

    public void exchangeBrainsheet(BrainstormingFinding finding, Brainsheet oldBrainsheet, Brainsheet newBrainsheet){
        service.exchangeBrainsheet(finding, oldBrainsheet, newBrainsheet);
    }

    public void nextRound(BrainstormingFinding finding){
        service.nextRound(finding);
    }

    public void lastRound(BrainstormingFinding finding){
        service.lastRound(finding);
    }
}
