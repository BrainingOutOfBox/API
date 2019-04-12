package mapper;

import com.fasterxml.jackson.databind.JsonNode;
import mappers.ModelsMapper;
import models.dto.*;
import models.bo.*;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class BrainstormingFindingBOUnitTest {

    private ModelsMapper modelsMapper;

    @Before
    public void setUp() {
        modelsMapper = new ModelsMapper();
    }

    @Test
    public void boToDtoTest(){
        BrainstormingFinding brainstormingFinding = createBrainstormingFinding();
        BrainstormingFindingDTO brainstormingFindingDTO = modelsMapper.toBrainstormingFindingDTO(brainstormingFinding);

        NoteIdeaDTO noteIdeaDTO = (NoteIdeaDTO) brainstormingFindingDTO.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(0);
        SketchIdeaDTO sketchIdeaDTO = (SketchIdeaDTO) brainstormingFindingDTO.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(1);
        PatternIdeaDTO patternIdeaDTO = (PatternIdeaDTO) brainstormingFindingDTO.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(2);

        NoteIdea noteIdea = (NoteIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(0);
        SketchIdea sketchIdea = (SketchIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(1);
        PatternIdea patternIdea = (PatternIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(2);

        assertEquals(brainstormingFinding.getName(), brainstormingFindingDTO.getName());
        assertEquals(brainstormingFinding.getProblemDescription(), brainstormingFindingDTO.getProblemDescription());
        assertEquals(brainstormingFinding.getNrOfIdeas(), brainstormingFindingDTO.getNrOfIdeas());
        assertEquals(brainstormingFinding.getBaseRoundTime(), brainstormingFindingDTO.getBaseRoundTime());
        assertEquals(brainstormingFinding.getIdentifier(), brainstormingFindingDTO.getIdentifier());
        assertEquals(brainstormingFinding.getType(), brainstormingFindingDTO.getType());

        assertEquals(noteIdea.getDescription(), noteIdeaDTO.getDescription());

        assertEquals(sketchIdea.getDescription(), sketchIdeaDTO.getDescription());
        assertEquals(sketchIdea.getPictureId(), sketchIdeaDTO.getPictureId());

        assertEquals(patternIdea.getDescription(), patternIdeaDTO.getDescription());
        assertEquals(patternIdea.getProblem(), patternIdeaDTO.getProblem());
        assertEquals(patternIdea.getSolution(), patternIdeaDTO.getSolution());
        assertEquals(patternIdea.getUrl(), patternIdeaDTO.getUrl());
        assertEquals(patternIdea.getCategory(), patternIdeaDTO.getCategory());
        assertEquals(patternIdea.getPictureId(), patternIdeaDTO.getPictureId());
    }

    @Test
    public void boToJsonTest(){
        BrainstormingFinding brainstormingFinding = createBrainstormingFinding();

        BrainstormingFindingDTO brainstormingFindingDTO = modelsMapper.toBrainstormingFindingDTO(brainstormingFinding);
        JsonNode json = Json.toJson(brainstormingFindingDTO);

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



    private BrainstormingFinding createBrainstormingFinding(){
        ArrayList<Brainsheet> brainsheets = createBrainsheets();

        BrainstormingFinding brainstormingFinding = new BrainstormingFinding("DemoTestBO", "DemoBO", 1, 2, 0, "", "software",brainsheets, 0, "");
        return  brainstormingFinding;
    }

    private ArrayList<Brainsheet> createBrainsheets(){
        ArrayList<Brainsheet> brainsheets = new ArrayList<>();
        Brainsheet brainsheet = new Brainsheet();
        brainsheet.setBrainwaves(createBrainwaves());
        brainsheets.add(brainsheet);
        return brainsheets;
    }

    private ArrayList<Brainwave> createBrainwaves(){
        ArrayList<Brainwave> brainwaves = new ArrayList<>();
        Brainwave brainwave = new Brainwave();
        brainwave.setIdeas(createAllTypeOfIdeas());
        brainwaves.add(brainwave);
        return brainwaves;
    }

    private ArrayList<Idea> createAllTypeOfIdeas(){
        ArrayList<Idea> ideas = new ArrayList<>();
        NoteIdea noteIdea = new NoteIdea("Demo");
        SketchIdea sketchIdea = new SketchIdea("Demo", "01234");
        PatternIdea patternIdea = new PatternIdea("Demo","DemoProblem", "DemoSolution", "www.microservice-api-patterns.org", "software", "01234");

        ideas.add(noteIdea);
        ideas.add(sketchIdea);
        ideas.add(patternIdea);

        return ideas;
    }
}
