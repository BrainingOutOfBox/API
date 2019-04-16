package services.database;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import config.MongoDBEngineProvider;
import models.bo.PatternIdea;
import play.Logger;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MongoDBPatternService implements DBPatternInterface{

    private MongoCollection<PatternIdea> patternIdeaCollection;

    @Inject
    public MongoDBPatternService(MongoDBEngineProvider mongoDBEngineProvider) {
        this.patternIdeaCollection = mongoDBEngineProvider.getDatabase().getCollection("Pattern", PatternIdea.class);
    }

    @Override
    public CompletableFuture<Queue<PatternIdea>> getAllPatternIdeas() {
        CompletableFuture<Queue<PatternIdea>> future = new CompletableFuture<>();
        Queue<PatternIdea> queue = new ConcurrentLinkedQueue<>();

        patternIdeaCollection.find().sort(Sorts.ascending("category")).forEach(
                finding -> queue.add(finding), (result, t) -> {
                    Logger.info("Get all available patterns");
                    future.complete(queue);
                });

        return future;
    }

}
