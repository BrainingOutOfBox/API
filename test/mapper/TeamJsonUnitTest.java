package mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mappers.ModelsMapper;
import models.bo.BrainstormingTeam;
import models.bo.Participant;
import models.dto.BrainstormingTeamDTO;
import models.dto.ParticipantDTO;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import static org.junit.Assert.assertEquals;

public class TeamJsonUnitTest {

    private ModelsMapper modelsMapper;

    @Before
    public void setUp() {
        modelsMapper = new ModelsMapper();
    }
    
    @Test
    public void jsonToBoTest(){
        JsonNode json = createTeamJsonNode();
        BrainstormingTeamDTO teamDTO = Json.fromJson(json, BrainstormingTeamDTO.class);
        BrainstormingTeam team = modelsMapper.toBrainstormingTeam(teamDTO);

        assertEquals(team.getName(), json.get("name").asText());
        assertEquals(team.getPurpose(), json.get("purpose").asText());
        assertEquals(team.getNrOfParticipants(), json.get("nrOfParticipants").asInt());
        assertEquals(team.getModerator().getUsername(), json.findPath("username").asText());
        assertEquals(team.getModerator().getPassword(), json.findPath("password").asText());
        assertEquals(team.getModerator().getFirstname(), json.findPath("firstname").asText());
        assertEquals(team.getModerator().getLastname(), json.findPath("lastname").asText());

    }

    private JsonNode createTeamJsonNode() {
        JsonNode paricipant = JsonNodeFactory.instance.objectNode();
        ((ObjectNode) paricipant).put("username", "DemoTestJson");
        ((ObjectNode) paricipant).put("password", "MirEgal");
        ((ObjectNode) paricipant).put("firstname", "Demo");
        ((ObjectNode) paricipant).put("lastname", "Test");

        JsonNode team = JsonNodeFactory.instance.objectNode();
        ((ObjectNode) team).put("name", "DemoTestJson");
        ((ObjectNode) team).put("purpose", "Test");
        ((ObjectNode) team).put("nrOfParticipants", 1);
        ((ObjectNode) team).put("moderator", paricipant);

        return team;
    }
}
