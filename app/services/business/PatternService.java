package services.business;

import models.bo.PatternIdea;
import services.database.DBPatternInterface;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PatternService {


    private DBPatternInterface service;

    @Inject
    public PatternService(DBPatternInterface service) {
        this.service = service;
    }

    public CompletableFuture<Queue<PatternIdea>> getAllPatternIdeas(){
        return service.getAllPatternIdeas();
    }

    public CompletableFuture<PatternIdea> getPatternIdea(PatternIdea patternIdea){
        return service.getPatternIdea(patternIdea.getDescription());
    }

    public boolean insertPattern(PatternIdea patternIdea) throws ExecutionException, InterruptedException {
        CompletableFuture<PatternIdea> future = getPatternIdea(patternIdea);

        if (future.get() == null){
            service.insertPattern(patternIdea);
            return true;
        } else {
            return false;
        }
    }

    public CompletableFuture<Long> deletePattern(PatternIdea patternIdea){
        return service.deletePattern(patternIdea);
    }

}
