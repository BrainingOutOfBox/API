package services.business;

import models.bo.PatternIdea;
import services.database.DBPatternInterface;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class PatternService {


    private DBPatternInterface service;

    @Inject
    public PatternService(DBPatternInterface service) {
        this.service = service;
    }

    public CompletableFuture<Queue<PatternIdea>> getAllPatternIdeas(){
        return service.getAllPatternIdeas();
    }

}
