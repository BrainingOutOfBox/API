package services;

import models.bo.Participant;
import org.junit.Before;
import org.junit.Test;
import services.business.ParticipantService;
import services.mocks.MockDBParticipantService;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParticipantServiceTest {

    private ParticipantService service;
    private Participant insertParticipant;
    private Participant testParticipant;

    @Before
    public void setUp() {
        service = new ParticipantService(new MockDBParticipantService());
        insertParticipant = new Participant("NotInDBParticipant", "MirEgal", "Max", "Mustermann");
        testParticipant = new Participant("TestParticipant", "MirEgal", "Max", "Mustermann");
    }


    @Test
    public void entireParticipantTest(){
        createParticipantTest();
        getParticipant();
        deleteParticipant();
    }

    private void createParticipantTest(){
        try {

            boolean result = service.insertParticipant(insertParticipant);
            assertTrue(result);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getParticipant(){
        try {

            Participant result = service.getParticipant(testParticipant).get();
            assertEquals(testParticipant.getUsername(), result.getUsername());
            assertEquals(testParticipant.getPassword(), result.getPassword());
            assertEquals(testParticipant.getFirstname(), result.getFirstname());
            assertEquals(testParticipant.getLastname(), result.getLastname());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void deleteParticipant(){
        try {

            long result = service.deleteParticipant(testParticipant).get();
            assertEquals(1, result);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
