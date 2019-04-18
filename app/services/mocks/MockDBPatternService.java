package services.mocks;

import models.bo.PatternIdea;
import services.database.DBPatternInterface;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MockDBPatternService implements DBPatternInterface {

    @Override
    public CompletableFuture<Queue<PatternIdea>> getAllPatternIdeas() {
        CompletableFuture<Queue<PatternIdea>> future = new CompletableFuture<>();
        Queue<PatternIdea> queue = new ConcurrentLinkedQueue<>();
        PatternIdea patternIdea = new PatternIdea("Test Pattern Idea", "Test", "Test", "www.Test.ch", "Test", "1234");

        queue.add(patternIdea);
        future.complete(queue);

        return future;
    }

    @Override
    public CompletableFuture<PatternIdea> getPatternIdea(String description) {
        return null;
    }

    @Override
    public void insertPattern(PatternIdea patternIdea) {

    }

    @Override
    public CompletableFuture<Long> deletePattern(PatternIdea patternIdea) {
        return null;
    }
}
