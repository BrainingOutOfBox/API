package services;

import models.bo.PatternIdea;
import org.junit.Before;
import org.junit.Test;
import services.business.PatternService;
import services.mocks.MockDBPatternService;

import java.util.Queue;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class PatternServiceTest {

    private PatternService service;

    @Before
    public void setUp() {
        this.service = new PatternService(new MockDBPatternService());
    }

    @Test
    public void getAllPatternIdeas() {
        try {
            Queue<PatternIdea> result = service.getAllPatternIdeas().get();
            assertEquals(1, result.size());
            assertEquals("Test Pattern Idea", result.peek().getDescription());
            assertEquals("Test", result.peek().getProblem());
            assertEquals("Test", result.peek().getSolution());
            assertEquals("www.Test.ch", result.peek().getUrl());
            assertEquals("Test", result.peek().getCategory());
            assertEquals("1234", result.peek().getPictureId());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
