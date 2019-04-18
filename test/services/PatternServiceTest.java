package services;

import models.bo.PatternIdea;
import org.junit.Before;
import org.junit.Test;
import services.business.PatternService;
import services.mocks.MockDBPatternService;

import java.util.Queue;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PatternServiceTest {

    private PatternService service;
    private PatternIdea insertIdea;
    private PatternIdea testIdea;

    @Before
    public void setUp() {
        this.service = new PatternService(new MockDBPatternService());
        this.insertIdea = new PatternIdea("NotInDBPattern", "Test", "Test", "www.Test.ch", "Test", "1234");
        this.testIdea = new PatternIdea("Test Pattern Idea", "Test", "Test", "www.Test.ch", "Test", "1234");
    }

    @Test
    public void getAllPatternIdeasTest() {
        try {
            Queue<PatternIdea> result = service.getAllPatternIdeas().get();
            assertEquals(1, result.size());
            assertEquals(testIdea.getDescription(), result.peek().getDescription());
            assertEquals(testIdea.getProblem(), result.peek().getProblem());
            assertEquals(testIdea.getSolution(), result.peek().getSolution());
            assertEquals(testIdea.getUrl(), result.peek().getUrl());
            assertEquals(testIdea.getCategory(), result.peek().getCategory());
            assertEquals(testIdea.getPictureId(), result.peek().getPictureId());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createPatternTest() {
        try {

            boolean result = service.insertPattern(insertIdea);
            assertTrue(result);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createInvalidPatternTest(){
        try {

            boolean result = service.insertPattern(testIdea);
            assertFalse(result);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPatternTest(){
        try {

            PatternIdea result = service.getPatternIdea(testIdea).get();
            assertEquals(testIdea.getDescription(), result.getDescription());
            assertEquals(testIdea.getProblem(), result.getProblem());
            assertEquals(testIdea.getSolution(), result.getSolution());
            assertEquals(testIdea.getUrl(), result.getUrl());
            assertEquals(testIdea.getCategory(), result.getCategory());
            assertEquals(testIdea.getPictureId(), result.getPictureId());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deletePatternTest(){
        try {

            long result = service.deletePattern(testIdea).get();
            assertEquals(1, result);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
