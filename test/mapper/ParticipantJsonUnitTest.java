package mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mappers.ModelsMapper;
import models.bo.Participant;
import models.dto.ParticipantDTO;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import static org.junit.Assert.assertEquals;

public class ParticipantJsonUnitTest {

    private ModelsMapper modelsMapper;

    @Before
    public void setUp() {
        modelsMapper = new ModelsMapper();
    }

    @Test
    public void jsonToBoTest() {
        JsonNode json = createParticipantJsonNode();
        ParticipantDTO participantDTO = Json.fromJson(json, ParticipantDTO.class);
        Participant participant = modelsMapper.toParticipant(participantDTO);

        assertEquals(participant.getUsername(), json.get("username").asText());
        assertEquals(participant.getPassword(), json.get("password").asText());
        assertEquals(participant.getFirstname(), json.get("firstname").asText());
        assertEquals(participant.getLastname(), json.get("lastname").asText());
    }

    private JsonNode createParticipantJsonNode(){
        JsonNode paricipant = JsonNodeFactory.instance.objectNode();
        ((ObjectNode) paricipant).put("username", "DemoTestJson");
        ((ObjectNode) paricipant).put("password", "MirEgal");
        ((ObjectNode) paricipant).put("firstname", "Demo");
        ((ObjectNode) paricipant).put("lastname", "Test");

        return paricipant;
    }
}
