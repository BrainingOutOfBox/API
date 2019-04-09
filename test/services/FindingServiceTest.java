package services;

import mappers.ModelsMapper;
import models.bo.*;
import models.dto.*;
import org.junit.Before;
import org.junit.Test;
import services.business.FindingService;
import services.mocks.MockDBFindingService;
import services.mocks.MockDBTeamService;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class FindingServiceTest {

    private FindingService service;
    private ArrayList<BrainsheetDTO> brainsheetDTOS = new ArrayList<>();
    private ArrayList<BrainwaveDTO> brainwaveDTOS = new ArrayList<>();
    private ArrayList<IdeaDTO> ideaDTOS = new ArrayList<>();
    private BrainstormingFindingDTO findingDTO;

    @Before
    public void setUp() {
        this.service = new FindingService(new MockDBFindingService(), new MockDBTeamService(), new ModelsMapper());

        this.ideaDTOS.add(new NoteIdeaDTO(""));
        this.brainwaveDTOS.add(new BrainwaveDTO(0, this.ideaDTOS));
        this.brainsheetDTOS.add(new BrainsheetDTO(0, this.brainwaveDTOS));
        this.findingDTO = new BrainstormingFindingDTO("TestFinding", "Test", 2, 3, 0, "", "software", brainsheetDTOS, 0, "1111");
        this.findingDTO.setIdentifier("2222");
    }

    @Test
    public void insertFindingTest(){
        try {
            String result = service.insertFinding(findingDTO, "1111");
            assertNotEquals("", result);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFindingTest(){
        try {
            BrainstormingFinding result = service.getFinding("2222").get();
            assertEquals(findingDTO.getName(), result.getName());
            assertEquals(findingDTO.getProblemDescription(), result.getProblemDescription());
            assertEquals(findingDTO.getNrOfIdeas(), result.getNrOfIdeas());
            assertEquals(findingDTO.getBaseRoundTime(), result.getBaseRoundTime());
            assertEquals(findingDTO.getType(), result.getType());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllFindingsOfTeamTest(){
        Participant moderator = new Participant("TestModerator", "MirEgal", "Max", "Mustermann");
        ArrayList membersList = new ArrayList<>();
        membersList.add(moderator);

        BrainstormingTeam team = new BrainstormingTeam("NotInDBTeam", "Test", 4, 1, membersList, moderator);
        team.setIdentifier("1111");

        try {
            Queue<BrainstormingFinding> result = service.getAllFindingsOfTeam(team).get();
            assertEquals(1, result.size());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exchangeBrainsheet(){
        ArrayList<Brainwave> brainwaves = new ArrayList<>();
        ArrayList<Idea> ideas = new ArrayList<>();

        ideas.add(new NoteIdea("Inserted"));
        brainwaves.add(new Brainwave(0, ideas));
        Brainsheet newBrainsheet = new Brainsheet(0, brainwaves);

        try {
            boolean result = service.exchangeBrainsheet(findingDTO.getIdentifier(), newBrainsheet);
            assertTrue(result);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void startBrainstormingTest(){
        try {
            boolean result = service.startBrainstorming(findingDTO.getIdentifier());
            assertTrue(result);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void calculateRemainingTimeTest(){
        try {
            long result = service.calculateRemainingTimeOfFinding(findingDTO.getIdentifier());
            assertEquals(0, result);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertInvalidFindingTest(){
        try {
            String result = service.insertFinding(findingDTO, "1112");
            assertNull(result);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
