package mapper;

import com.fasterxml.jackson.databind.JsonNode;
import mappers.ModelsMapper;
import models.bo.BrainstormingTeam;
import models.bo.Participant;
import models.dto.BrainstormingTeamDTO;
import models.dto.ParticipantDTO;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TeamBOUnitTest {

    private ModelsMapper modelsMapper;

    @Before
    public void setUp() {
        modelsMapper = new ModelsMapper();
    }

    @Test
    public void boToJsonTest(){
        BrainstormingTeam team = createTeam();

        BrainstormingTeamDTO brainstormingTeamDTO = modelsMapper.tobrainstormingTeamDTO(team);
        JsonNode json = Json.toJson(brainstormingTeamDTO);

        assertEquals(team.getName(), json.get("name").asText());
        assertEquals(team.getPurpose(), json.get("purpose").asText());
        assertEquals(team.getNrOfParticipants(), json.get("nrOfParticipants").asInt());
        assertEquals(team.getModerator().getUsername(), json.findPath("username").asText());
        assertEquals(team.getModerator().getPassword(), json.findPath("password").asText());
        assertEquals(team.getModerator().getFirstname(), json.findPath("firstname").asText());
        assertEquals(team.getModerator().getLastname(), json.findPath("lastname").asText());


    }

    @Test
    public void boToDtoTest(){
        BrainstormingTeam team = createTeam();

        BrainstormingTeamDTO brainstormingTeamDTO = modelsMapper.tobrainstormingTeamDTO(team);

        assertEquals(team.getName(), brainstormingTeamDTO.getName());
        assertEquals(team.getPurpose(), brainstormingTeamDTO.getPurpose());
        assertEquals(team.getNrOfParticipants(), brainstormingTeamDTO.getNrOfParticipants());
        assertEquals(team.getModerator().getUsername(), brainstormingTeamDTO.getModerator().getUsername());
        assertEquals(team.getModerator().getPassword(), brainstormingTeamDTO.getModerator().getPassword());
        assertEquals(team.getModerator().getFirstname(), brainstormingTeamDTO.getModerator().getFirstname());
        assertEquals(team.getModerator().getLastname(), brainstormingTeamDTO.getModerator().getLastname());

    }

    private BrainstormingTeam createTeam() {
        Participant moderator = new Participant("DemoTestBO", "MirEgal", "Demo", "Test");
        ArrayList<Participant> list = new ArrayList();
        return new BrainstormingTeam("DemoTestBO", "Test",1, 1, list, moderator);

    }
}
