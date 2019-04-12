package services;

import models.bo.BrainstormingTeam;
import models.bo.Participant;
import org.junit.Before;
import org.junit.Test;
import services.business.TeamService;
import services.mocks.MockDBTeamService;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TeamServiceTest {

    private TeamService service;
    private BrainstormingTeam insertTeam;
    private Participant moderator;
    private Participant participant;

    @Before
    public void setUp() {
        this.service = new TeamService(new MockDBTeamService());
        this.participant = new Participant("TestParticipant", "MirEgal", "Max", "Mustermann");


        this.moderator = new Participant("TestModerator", "MirEgal", "Max", "Mustermann");
        this.insertTeam = new BrainstormingTeam("NotInDBTeam", "Test", 4, 0, new ArrayList<>(), moderator);
        this.insertTeam.setIdentifier("1111");
    }

    @Test
    public void createTeamTest(){
        service.insertTeam(insertTeam);
    }

    @Test
    public void getTeamTest(){
        try {

            BrainstormingTeam result = service.getTeam(insertTeam.getIdentifier()).get();
            assertEquals(insertTeam.getIdentifier(), result.getIdentifier());
            assertEquals(insertTeam.getName(), result.getName());
            assertEquals(insertTeam.getPurpose(), result.getPurpose());
            assertEquals(insertTeam.getNrOfParticipants(), result.getNrOfParticipants());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllTeamsOfParticipantTest(){
        try {

            Queue<BrainstormingTeam> result = service.getAllTeamsOfParticipant(moderator).get();
            assertEquals(1, result.size());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTeamTest(){
        try {

            long result = service.deleteTeam(insertTeam).get();
            assertEquals(1L, result);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void joinTeamTest(){
        boolean result = service.joinTeam(insertTeam, participant);
        assertTrue(result);
    }

    @Test
    public void joinTwiceTeamTest(){
        boolean result = service.joinTeam(insertTeam, participant);
        boolean result2 = service.joinTeam(insertTeam, participant);
        assertTrue(result);
        assertFalse(result2);
    }

    @Test
    public void joinMoreThanAllowedTeamTest(){
        Participant participant2 = new Participant("TestParticipant2", "MirEgal", "Max", "Mustermann");
        Participant participant3 = new Participant("TestParticipant3", "MirEgal", "Max", "Mustermann");
        Participant participant4 = new Participant("TestParticipant4", "MirEgal", "Max", "Mustermann");
        Participant participant5 = new Participant("TestParticipant5", "MirEgal", "Max", "Mustermann");


        boolean result1 = service.joinTeam(insertTeam, participant);
        boolean result2 = service.joinTeam(insertTeam, participant2);
        boolean result3 = service.joinTeam(insertTeam, participant3);
        boolean result4 = service.joinTeam(insertTeam, participant4);
        boolean result5 = service.joinTeam(insertTeam, participant5);


        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        assertTrue(result4);
        assertFalse(result5);
    }

    @Test
    public void leaveTeamTest(){
        ArrayList<Participant> list = new ArrayList<>();
        list.add(moderator);

        BrainstormingTeam team = new BrainstormingTeam("NotInDBTeam", "Test", 4, 1, list, moderator);

        boolean result = service.leaveTeam(team, moderator);
        assertTrue(result);

    }

    @Test
    public void leaveTwiceTeamTest(){
        ArrayList<Participant> list = new ArrayList<>();
        list.add(moderator);

        BrainstormingTeam team = new BrainstormingTeam("NotInDBTeam", "Test", 4, 1, list, moderator);


        boolean result = service.leaveTeam(team, moderator);
        boolean result2 = service.leaveTeam(team, moderator);
        assertTrue(result);
        assertFalse(result2);
    }


}
