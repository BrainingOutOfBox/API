package services;

import models.bo.BrainstormingTeam;
import models.bo.Participant;
import org.junit.Before;
import org.junit.Test;
import services.business.TeamService;
import services.mocks.MockDBTeamService;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TeamServiceTest {

    private TeamService service;
    private BrainstormingTeam insertTeam;
    private ArrayList<Participant> membersList;
    private Participant moderator;
    private Participant participant;

    @Before
    public void setUp() {
        this.service = new TeamService(new MockDBTeamService());
        this.participant = new Participant("TestParticipant", "MirEgal", "Max", "Mustermann");


        this.moderator = new Participant("TestModerator", "MirEgal", "Max", "Mustermann");
        this.membersList = new ArrayList<>();
        this.membersList.add(moderator);
        this.insertTeam = new BrainstormingTeam("NotInDBTeam", "Test", 4, 1, membersList, moderator);
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
    public void leaveTeamTest(){
        boolean result = service.leaveTeam(insertTeam, moderator);
        assertTrue(result);
    }

    @Test
    public void joinTeamTest(){
        boolean result = service.joinTeam(insertTeam, participant);
        assertTrue(result);
    }


}
