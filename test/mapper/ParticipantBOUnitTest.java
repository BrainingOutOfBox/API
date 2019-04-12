package mapper;

import com.fasterxml.jackson.databind.JsonNode;
import mappers.ModelsMapper;
import models.bo.Participant;
import models.dto.ParticipantDTO;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import static org.junit.Assert.assertEquals;

public class ParticipantBOUnitTest {

    private ModelsMapper modelsMapper;

    @Before
    public void setUp() {
        modelsMapper = new ModelsMapper();
    }


    @Test
    public void boToJsonTest(){
        Participant participant = createParticipant();

        ParticipantDTO participantDTO = modelsMapper.toParticipantDTO(participant);
        JsonNode json = Json.toJson(participantDTO);

        assertEquals(json.get("username").asText(), participant.getUsername());
        assertEquals(json.get("password").asText(), participant.getPassword());
        assertEquals(json.get("firstname").asText(), participant.getFirstname());
        assertEquals(json.get("lastname").asText(), participant.getLastname());

    }

    @Test
    public void boToDtoTest(){
        Participant participant = createParticipant();

        ParticipantDTO participantDTO = modelsMapper.toParticipantDTO(participant);

        assertEquals(participantDTO.getUsername(), participant.getUsername());
        assertEquals(participantDTO.getPassword(), participant.getPassword());
        assertEquals(participantDTO.getFirstname(), participant.getFirstname());
        assertEquals(participantDTO.getLastname(), participant.getLastname());

    }

    private Participant createParticipant() {
        return new Participant("DemoTestBO", "MirEgal", "Demo", "Test");
    }
}
