package mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mappers.ModelsMapper;
import models.bo.*;
import models.dto.*;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import static org.junit.Assert.assertEquals;

public class BrainstormingFindingJsonUnitTest {

    private ModelsMapper modelsMapper;

    @Before
    public void setUp() {
        modelsMapper = new ModelsMapper();
    }

    @Test
    public void jsonToBoTest() {
        JsonNode json = createBrainstormingFindingJsonNode();
        BrainstormingFindingDTO brainstormingFindingDTO = Json.fromJson(json, BrainstormingFindingDTO.class);
        BrainstormingFinding brainstormingFinding = modelsMapper.toBrainstormingFinding(brainstormingFindingDTO);

        NoteIdea noteIdea = (NoteIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(0);
        SketchIdea sketchIdea = (SketchIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(1);
        PatternIdea patternIdea = (PatternIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(2);

        assertEquals(brainstormingFinding.getName(), json.get("name").asText());
        assertEquals(brainstormingFinding.getProblemDescription(), json.get("problemDescription").asText());
        assertEquals(brainstormingFinding.getNrOfIdeas(), json.get("nrOfIdeas").asInt());
        assertEquals(brainstormingFinding.getBaseRoundTime(), json.get("baseRoundTime").asInt());
        assertEquals(brainstormingFinding.getIdentifier(), json.get("identifier").asText());
        assertEquals(brainstormingFinding.getType(), json.get("type").asText());

        assertEquals(noteIdea.getDescription(), json.findPath("description").asText());

        assertEquals(sketchIdea.getPictureId(), json.findPath("pictureId").asText());

        assertEquals(patternIdea.getProblem(), json.findPath("problem").asText());
        assertEquals(patternIdea.getSolution(), json.findPath("solution").asText());
        assertEquals(patternIdea.getUrl(), json.findPath("url").asText());
        assertEquals(patternIdea.getCategory(), json.findPath("category").asText());


    }


    private JsonNode createBrainstormingFindingJsonNode(){
        JsonNode finding = JsonNodeFactory.instance.objectNode();
        ((ObjectNode) finding).put("name", "DemoTestJson");
        ((ObjectNode) finding).put("problemDescription", "DemoTestJson");
        ((ObjectNode) finding).put("nrOfIdeas", 3);
        ((ObjectNode) finding).put("baseRoundTime", 3);
        ((ObjectNode) finding).put("type", "software");
        ((ObjectNode) finding).put("identifier", "abc-def-ghij-123");
        ((ObjectNode) finding).putArray("brainsheets").addAll(createBrainsheetJson());

        return finding;
    }

    private ArrayNode createBrainsheetJson(){
        ArrayNode brainsheetArray = JsonNodeFactory.instance.arrayNode();
        ObjectNode brainsheet = JsonNodeFactory.instance.objectNode();
        brainsheet.put("nrOfSheet", 0);
        brainsheet.putArray("brainwaves").addAll(createBrainwaveJson());
        brainsheetArray.add(brainsheet);

        return brainsheetArray;
    }

    private ArrayNode createBrainwaveJson(){
        ArrayNode brainwaveArray = JsonNodeFactory.instance.arrayNode();
        ObjectNode brainwave = JsonNodeFactory.instance.objectNode();
        brainwave.put("nrOfBrainwave", 0);
        brainwave.putArray("ideas").addAll(createAllTypesOfJsonIdeas());
        brainwaveArray.add(brainwave);

        return brainwaveArray;
    }

    private ArrayNode createAllTypesOfJsonIdeas(){
        ArrayNode ideaArray = JsonNodeFactory.instance.arrayNode();

        ObjectNode noteIdea = JsonNodeFactory.instance.objectNode();
        noteIdea.put("type", "noteIdea");
        noteIdea.put("description", "demo");
        ideaArray.add(noteIdea);

        ObjectNode sketchIdea = JsonNodeFactory.instance.objectNode();
        sketchIdea.put("type", "sketchIdea");
        sketchIdea.put("description", "demo");
        sketchIdea.put("pictureId", "01234");
        ideaArray.add(sketchIdea);

        ObjectNode patternIdea = JsonNodeFactory.instance.objectNode();
        patternIdea.put("type", "patternIdea");
        patternIdea.put("description", "demo");
        patternIdea.put("problem", "DemoProblem");
        patternIdea.put("solution", "DemoSolution");
        patternIdea.put("url", "www.microservice-api-patterns.org");
        patternIdea.put("category", "software");
        patternIdea.put("pictureId", "56789");
        ideaArray.add(patternIdea);

        return ideaArray;
    }

}
