package services.database;

import com.google.inject.ImplementedBy;
import models.bo.PatternIdea;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@ImplementedBy(MongoDBPatternService.class)
public interface DBPatternInterface {

    CompletableFuture<Queue<PatternIdea>> getAllPatternIdeas();

}
