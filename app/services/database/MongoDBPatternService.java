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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

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

    @Override
    public CompletableFuture<PatternIdea> getPatternIdea(String description){
        CompletableFuture<PatternIdea> future = new CompletableFuture<>();

        patternIdeaCollection.find(and(
                eq("description", description))).first((patternIdea, t) -> {

            if (patternIdea != null) {
                Logger.info("Found pattern");
                future.complete(patternIdea);
            } else {
                future.complete(null);
            }

        });

        return future;
    }

    @Override
    public void insertPattern(PatternIdea patternIdea) {
        patternIdeaCollection.insertOne(patternIdea, ((result, t) -> Logger.info("Pattern successfully inserted")));
    }

    @Override
    public CompletableFuture<Long> deletePattern(PatternIdea patternIdea) {
        CompletableFuture<Long> future = new CompletableFuture<>();

        patternIdeaCollection.deleteOne(and(
                eq("description", patternIdea.getDescription()),
                eq("problem", patternIdea.getProblem()),
                eq("solution", patternIdea.getSolution()),
                eq("url", patternIdea.getUrl()),
                eq("category", patternIdea.getCategory()),
                eq("pictureId", patternIdea.getPictureId())), (result, t) -> {
            Logger.info(result.getDeletedCount() + " Pattern successfully deleted");
            future.complete(result.getDeletedCount());
        });

        return future;
    }

}
